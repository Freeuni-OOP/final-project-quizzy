package quizzy.service;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import quizzy.dao.UserDAO;
import quizzy.model.Achievement;
import quizzy.model.User;
import quizzy.model.UserAchievement;
import quizzy.util.HibernateUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Stress and edge-case tests for {@link AchievementService}.
 * Extends coverage beyond the basic award/dedup/threshold tests.
 */
public class AchievementEdgeCaseTest {

    private static final UserDAO userDAO = new UserDAO();
    private User testUser;
    private int userId;

    @BeforeClass
    public static void createNonEntityTables() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.createNativeQuery(
                        "CREATE TABLE IF NOT EXISTS quizzes ("
                                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                                + "creator_id INT NOT NULL, "
                                + "title VARCHAR(200), "
                                + "description TEXT, "
                                + "random_questions BOOLEAN DEFAULT FALSE, "
                                + "one_page BOOLEAN DEFAULT TRUE, "
                                + "immediate_correction BOOLEAN DEFAULT FALSE, "
                                + "practice_mode BOOLEAN DEFAULT FALSE"
                                + ")").executeUpdate();

                session.createNativeQuery(
                        "CREATE TABLE IF NOT EXISTS quiz_attempts ("
                                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                                + "user_id INT NOT NULL, "
                                + "quiz_id INT NOT NULL, "
                                + "score INT NOT NULL, "
                                + "max_score INT NOT NULL, "
                                + "time_taken_seconds BIGINT NOT NULL, "
                                + "completed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                                + ")").executeUpdate();
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Before
    public void setUp() {
        String name = "edge_" + System.currentTimeMillis();
        testUser = new User(0, name, "hash", "salt", false);
        userDAO.save(testUser);
        testUser = userDAO.findByUsername(name);
        assertNotNull(testUser);
        userId = testUser.getId();
    }

    @After
    public void tearDown() {
        if (userId == 0) return;
        exec("DELETE FROM user_achievements WHERE user_id = ?", userId);
        exec("DELETE FROM quiz_attempts WHERE user_id = ?", userId);
        exec("DELETE FROM quizzes WHERE creator_id = ?", userId);
        exec("DELETE FROM users WHERE id = ?", userId);
    }

    // ------------------------------------------------------------------
    // Author threshold boundaries
    // ------------------------------------------------------------------

    @Test
    public void checkAuthor_ExactlyOneQuiz_AwardsAmateurOnly() {
        insertQuiz(userId); /* exactly 1 */

        AchievementService.checkAuthorAchievements(userId);

        assertTrue("AMATEUR_AUTHOR should be awarded",
                AchievementService.hasAchievement(userId, Achievement.AMATEUR_AUTHOR));
        assertFalse("PROLIFIC_AUTHOR should NOT be awarded at 1 quiz",
                AchievementService.hasAchievement(userId, Achievement.PROLIFIC_AUTHOR));
        assertFalse("PRODIGIOUS_AUTHOR should NOT be awarded at 1 quiz",
                AchievementService.hasAchievement(userId, Achievement.PRODIGIOUS_AUTHOR));
    }

    @Test
    public void checkAuthor_ExactlyFiveQuizzes_AwardsAmateurAndProlificButNotProdigious() {
        for (int i = 0; i < 5; i++) {
            insertQuiz(userId);
        }

        AchievementService.checkAuthorAchievements(userId);

        assertTrue(AchievementService.hasAchievement(userId, Achievement.AMATEUR_AUTHOR));
        assertTrue(AchievementService.hasAchievement(userId, Achievement.PROLIFIC_AUTHOR));
        assertFalse("PRODIGIOUS needs >= 10, not 5",
                AchievementService.hasAchievement(userId, Achievement.PRODIGIOUS_AUTHOR));
    }

    @Test
    public void checkAuthor_ExactlyTenQuizzes_AwardsAllThree() {
        for (int i = 0; i < 10; i++) {
            insertQuiz(userId);
        }

        AchievementService.checkAuthorAchievements(userId);

        assertTrue(AchievementService.hasAchievement(userId, Achievement.AMATEUR_AUTHOR));
        assertTrue(AchievementService.hasAchievement(userId, Achievement.PROLIFIC_AUTHOR));
        assertTrue(AchievementService.hasAchievement(userId, Achievement.PRODIGIOUS_AUTHOR));
    }

    // ------------------------------------------------------------------
    // Quiz taker edge cases
    // ------------------------------------------------------------------

    @Test
    public void checkQuizTaker_ExactlyTenAttempts_AwardsQuizMachine() {
        int quizId = insertQuiz(userId);

        /* Insert exactly 10 attempts */
        for (int i = 0; i < 10; i++) {
            insertAttempt(userId, quizId, 5);
        }

        AchievementService.checkQuizTakerAchievements(userId, quizId, 5);

        assertTrue("QUIZ_MACHINE should be awarded at exactly 10",
                AchievementService.hasAchievement(userId, Achievement.QUIZ_MACHINE));
    }

    @Test
    public void checkQuizTaker_ScoreEqualsExistingMax_BothGetGreatest() {
        int quizId = insertQuiz(userId);

        /* First user takes the quiz with score 8 — gets GREATEST */
        insertAttempt(userId, quizId, 8);
        AchievementService.checkQuizTakerAchievements(userId, quizId, 8);
        assertTrue("First taker should get GREATEST",
                AchievementService.hasAchievement(userId, Achievement.I_AM_THE_GREATEST));

        /* Second user ties with score 8 — should ALSO get GREATEST */
        int otherId = insertOtherUser();
        insertAttempt(otherId, quizId, 8);
        AchievementService.checkQuizTakerAchievements(otherId, quizId, 8);
        assertTrue("Tie for highest should also get GREATEST",
                AchievementService.hasAchievement(otherId, Achievement.I_AM_THE_GREATEST));

        /* Cleanup other user */
        exec("DELETE FROM quiz_attempts WHERE user_id = ?", otherId);
        exec("DELETE FROM user_achievements WHERE user_id = ?", otherId);
        exec("DELETE FROM users WHERE id = ?", otherId);
    }

    // ------------------------------------------------------------------
    // Concurrent award stress test
    // ------------------------------------------------------------------

    @Test
    public void concurrentAward_SameAchievement_OnlyOneRecord() throws Exception {
        int threads = 5;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger awardedCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    latch.await(); /* all threads start simultaneously */
                    boolean awarded = AchievementService.awardAchievement(
                            userId, Achievement.AMATEUR_AUTHOR);
                    if (awarded) awardedCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        latch.countDown(); /* GO */
        pool.shutdown();
        /* Wait up to 10 seconds for all threads */
        for (int i = 0; i < 100 && !pool.isTerminated(); i++) {
            Thread.sleep(100);
        }
        assertTrue("Threads should finish", pool.isTerminated());

        /* Exactly one thread should succeed — all others get false (dedup) */
        assertEquals("Exactly 1 thread should report success", 1, awardedCount.get());

        /* Verify exactly one record in DB */
        List<UserAchievement> list = AchievementService.getUserAchievements(userId);
        long count = list.stream()
                .filter(ua -> ua.getAchievement() == Achievement.AMATEUR_AUTHOR)
                .count();
        assertEquals("DB should have exactly 1 record", 1, count);
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    private int insertQuiz(int creatorId) {
        exec("INSERT INTO quizzes (creator_id, title, random_questions, "
                + "one_page, immediate_correction, practice_mode) "
                + "VALUES (?, 'Edge Quiz', FALSE, TRUE, FALSE, FALSE)", creatorId);
        return lastInsertId();
    }

    private void insertAttempt(int uid, int qid, int score) {
        exec("INSERT INTO quiz_attempts (user_id, quiz_id, score, max_score, "
                + "time_taken_seconds) VALUES (?, ?, ?, ?, 60)", uid, qid, score, score);
    }

    private int insertOtherUser() {
        String name = "edge_other_" + System.currentTimeMillis();
        User u = new User(0, name, "hash", "salt", false);
        userDAO.save(u);
        User saved = userDAO.findByUsername(name);
        return saved != null ? saved.getId() : 0;
    }

    private void exec(String sql, int... params) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            try {
                NativeQuery<?> q = s.createNativeQuery(sql);
                for (int i = 0; i < params.length; i++)
                    q.setParameter(i + 1, params[i]);
                q.executeUpdate();
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    private int lastInsertId() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return ((Number) s.createNativeQuery(
                    "SELECT LAST_INSERT_ID()").uniqueResult()).intValue();
        }
    }
}
