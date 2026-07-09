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

import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link AchievementService}.
 *
 * <p>These tests run against an H2 in-memory database (no external MySQL needed).
 * The mapped entity tables are created automatically by {@code hbm2ddl.auto=create-drop}.
 * Non-entity tables ({@code quizzes}, {@code quiz_attempts}) are created in
 * {@link #createNonEntityTables()}.</p>
 *
 * <p>Test data is created in {@code @Before} and cleaned up in {@code @After}.</p>
 */
public class AchievementServiceTest {

    private static final UserDAO userDAO = new UserDAO();

    private User testUser;

    /**
     * Creates the {@code quizzes} and {@code quiz_attempts} tables in H2.
     * These tables are not mapped as Hibernate entities ({@code Quiz} and
     * {@code QuizAttempt} are plain POJOs), so {@code hbm2ddl} won't create them.
     */
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
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    @Before
    public void setUp() {
        String uniqueUsername = "test_ach_" + System.currentTimeMillis();
        testUser = new User(0, uniqueUsername, "hash", "salt", false);
        userDAO.save(testUser);
        /* Re-fetch to get the database-assigned ID */
        testUser = userDAO.findByUsername(uniqueUsername);
        assertNotNull("Failed to create test user", testUser);
    }

    @After
    public void tearDown() {
        if (testUser == null || testUser.getId() == 0) {
            return;
        }
        int uid = testUser.getId();

        /* Delete in reverse FK order */
        executeDelete("DELETE FROM user_achievements WHERE user_id = ?", uid);
        executeDelete("DELETE FROM quiz_attempts WHERE user_id = ?", uid);
        executeDelete("DELETE FROM quizzes WHERE creator_id = ?", uid);
        executeDelete("DELETE FROM users WHERE id = ?", uid);
    }

    // ------------------------------------------------------------------
    // awardAchievement
    // ------------------------------------------------------------------

    @Test
    public void testAwardAchievement_NewAchievement_ReturnsTrue() {
        boolean awarded = AchievementService.awardAchievement(
                testUser.getId(), Achievement.AMATEUR_AUTHOR);
        assertTrue("First award should return true", awarded);
        assertTrue("Achievement should be recorded",
                AchievementService.hasAchievement(testUser.getId(), Achievement.AMATEUR_AUTHOR));
    }

    @Test
    public void testAwardAchievement_Duplicate_ReturnsFalse() {
        AchievementService.awardAchievement(testUser.getId(), Achievement.AMATEUR_AUTHOR);
        boolean second = AchievementService.awardAchievement(
                testUser.getId(), Achievement.AMATEUR_AUTHOR);
        assertFalse("Duplicate award should return false", second);

        /* Verify only one record exists */
        List<UserAchievement> list = AchievementService.getUserAchievements(testUser.getId());
        long count = list.stream()
                .filter(ua -> ua.getAchievement() == Achievement.AMATEUR_AUTHOR)
                .count();
        assertEquals("Should have exactly one AMATEUR_AUTHOR record", 1, count);
    }

    @Test
    public void testAwardAchievement_UserNotFound_ReturnsFalse() {
        boolean awarded = AchievementService.awardAchievement(99999, Achievement.AMATEUR_AUTHOR);
        assertFalse("Nonexistent user should return false", awarded);
    }

    @Test
    public void testAwardAchievement_MultipleDifferent_AllAwarded() {
        assertTrue(AchievementService.awardAchievement(
                testUser.getId(), Achievement.AMATEUR_AUTHOR));
        assertTrue(AchievementService.awardAchievement(
                testUser.getId(), Achievement.QUIZ_MACHINE));
        assertTrue(AchievementService.awardAchievement(
                testUser.getId(), Achievement.PRACTICE_MAKES_PERFECT));

        List<UserAchievement> list = AchievementService.getUserAchievements(testUser.getId());
        assertEquals("Should have 3 distinct achievements", 3, list.size());
    }

    // ------------------------------------------------------------------
    // hasAchievement
    // ------------------------------------------------------------------

    @Test
    public void testHasAchievement_NotYetAwarded_ReturnsFalse() {
        assertFalse("Should not have achievement before awarding",
                AchievementService.hasAchievement(testUser.getId(), Achievement.PRODIGIOUS_AUTHOR));
    }

    @Test
    public void testHasAchievement_AfterAwarding_ReturnsTrue() {
        AchievementService.awardAchievement(testUser.getId(), Achievement.PRODIGIOUS_AUTHOR);
        assertTrue("Should have achievement after awarding",
                AchievementService.hasAchievement(testUser.getId(), Achievement.PRODIGIOUS_AUTHOR));
    }

    // ------------------------------------------------------------------
    // getUserAchievements
    // ------------------------------------------------------------------

    @Test
    public void testGetUserAchievements_EmptyForNewUser() {
        List<UserAchievement> list = AchievementService.getUserAchievements(testUser.getId());
        assertNotNull("Result list should not be null", list);
        assertTrue("New user should have no achievements", list.isEmpty());
    }

    @Test
    public void testGetUserAchievements_ReturnsAllAwarded() {
        AchievementService.awardAchievement(testUser.getId(), Achievement.AMATEUR_AUTHOR);
        AchievementService.awardAchievement(testUser.getId(), Achievement.QUIZ_MACHINE);

        List<UserAchievement> list = AchievementService.getUserAchievements(testUser.getId());
        assertEquals(2, list.size());
    }

    // ------------------------------------------------------------------
    // checkAuthorAchievements
    // ------------------------------------------------------------------

    @Test
    public void testCheckAuthorAchievements_OneQuiz_AwardsAmateur() {
        insertQuiz(testUser.getId());

        AchievementService.checkAuthorAchievements(testUser.getId());

        assertTrue("AMATEUR_AUTHOR should be awarded after 1 quiz",
                AchievementService.hasAchievement(testUser.getId(), Achievement.AMATEUR_AUTHOR));
        assertFalse("PROLIFIC_AUTHOR should NOT be awarded after only 1 quiz",
                AchievementService.hasAchievement(testUser.getId(), Achievement.PROLIFIC_AUTHOR));
    }

    @Test
    public void testCheckAuthorAchievements_FiveQuizzes_AwardsProlific() {
        for (int i = 0; i < 5; i++) {
            insertQuiz(testUser.getId());
        }

        AchievementService.checkAuthorAchievements(testUser.getId());

        assertTrue("AMATEUR_AUTHOR should be awarded",
                AchievementService.hasAchievement(testUser.getId(), Achievement.AMATEUR_AUTHOR));
        assertTrue("PROLIFIC_AUTHOR should be awarded after 5 quizzes",
                AchievementService.hasAchievement(testUser.getId(), Achievement.PROLIFIC_AUTHOR));
        assertFalse("PRODIGIOUS_AUTHOR should NOT be awarded after only 5 quizzes",
                AchievementService.hasAchievement(testUser.getId(), Achievement.PRODIGIOUS_AUTHOR));
    }

    @Test
    public void testCheckAuthorAchievements_TenQuizzes_AwardsProdigious() {
        for (int i = 0; i < 10; i++) {
            insertQuiz(testUser.getId());
        }

        AchievementService.checkAuthorAchievements(testUser.getId());

        assertTrue(AchievementService.hasAchievement(testUser.getId(), Achievement.AMATEUR_AUTHOR));
        assertTrue(AchievementService.hasAchievement(testUser.getId(), Achievement.PROLIFIC_AUTHOR));
        assertTrue("PRODIGIOUS_AUTHOR should be awarded after 10 quizzes",
                AchievementService.hasAchievement(testUser.getId(), Achievement.PRODIGIOUS_AUTHOR));
    }

    // ------------------------------------------------------------------
    // checkQuizTakerAchievements — QUIZ_MACHINE
    // ------------------------------------------------------------------

    @Test
    public void testCheckQuizTakerAchievements_TenAttempts_AwardsQuizMachine() {
        int quizId = insertQuiz(testUser.getId());
        for (int i = 0; i < 10; i++) {
            insertQuizAttempt(testUser.getId(), quizId, 5);
        }

        AchievementService.checkQuizTakerAchievements(testUser.getId(), quizId, 5);

        assertTrue("QUIZ_MACHINE should be awarded after 10 attempts",
                AchievementService.hasAchievement(testUser.getId(), Achievement.QUIZ_MACHINE));
    }

    @Test
    public void testCheckQuizTakerAchievements_LessThanTen_NoQuizMachine() {
        int quizId = insertQuiz(testUser.getId());
        for (int i = 0; i < 5; i++) {
            insertQuizAttempt(testUser.getId(), quizId, 5);
        }

        AchievementService.checkQuizTakerAchievements(testUser.getId(), quizId, 5);

        assertFalse("QUIZ_MACHINE should NOT be awarded after only 5 attempts",
                AchievementService.hasAchievement(testUser.getId(), Achievement.QUIZ_MACHINE));
    }

    // ------------------------------------------------------------------
    // checkQuizTakerAchievements — I_AM_THE_GREATEST
    // ------------------------------------------------------------------

    @Test
    public void testCheckQuizTakerAchievements_HighestScore_AwardsGreatest() {
        int quizId = insertQuiz(testUser.getId());

        /* Another user scores 50 */
        int otherUserId = insertOtherUser();
        insertQuizAttempt(otherUserId, quizId, 50);

        /* Our test user scores 90 — should be the greatest */
        insertQuizAttempt(testUser.getId(), quizId, 90);
        AchievementService.checkQuizTakerAchievements(testUser.getId(), quizId, 90);

        assertTrue("I_AM_THE_GREATEST should be awarded for highest score",
                AchievementService.hasAchievement(testUser.getId(), Achievement.I_AM_THE_GREATEST));

        /* Clean up other user and their data */
        executeDelete("DELETE FROM quiz_attempts WHERE user_id = ?", otherUserId);
        executeDelete("DELETE FROM users WHERE id = ?", otherUserId);
    }

    @Test
    public void testCheckQuizTakerAchievements_NotHighest_NoAward() {
        int quizId = insertQuiz(testUser.getId());

        /* Another user already has a higher score */
        int otherUserId = insertOtherUser();
        insertQuizAttempt(otherUserId, quizId, 100);

        /* Our test user scores 50 — not the greatest */
        insertQuizAttempt(testUser.getId(), quizId, 50);
        AchievementService.checkQuizTakerAchievements(testUser.getId(), quizId, 50);

        assertFalse("I_AM_THE_GREATEST should NOT be awarded when score is not highest",
                AchievementService.hasAchievement(testUser.getId(), Achievement.I_AM_THE_GREATEST));

        /* Clean up other user */
        executeDelete("DELETE FROM quiz_attempts WHERE user_id = ?", otherUserId);
        executeDelete("DELETE FROM users WHERE id = ?", otherUserId);
    }

    @Test
    public void testCheckQuizTakerAchievements_FirstAttempt_AwardsGreatest() {
        int quizId = insertQuiz(testUser.getId());

        /* First attempt on this quiz — should be the greatest by default */
        insertQuizAttempt(testUser.getId(), quizId, 80);
        AchievementService.checkQuizTakerAchievements(testUser.getId(), quizId, 80);

        assertTrue("I_AM_THE_GREATEST should be awarded for the first attempt on a quiz",
                AchievementService.hasAchievement(testUser.getId(), Achievement.I_AM_THE_GREATEST));
    }

    @Test
    public void testCheckQuizTakerAchievements_DuplicateGreatest_NotDoubleAwarded() {
        int quizId = insertQuiz(testUser.getId());

        /* First high score */
        insertQuizAttempt(testUser.getId(), quizId, 95);
        AchievementService.checkQuizTakerAchievements(testUser.getId(), quizId, 95);
        assertTrue(AchievementService.hasAchievement(testUser.getId(), Achievement.I_AM_THE_GREATEST));

        /* Second attempt with same score — service dedup prevents duplicate */
        insertQuizAttempt(testUser.getId(), quizId, 95);
        AchievementService.checkQuizTakerAchievements(testUser.getId(), quizId, 95);

        List<UserAchievement> list = AchievementService.getUserAchievements(testUser.getId());
        long count = list.stream()
                .filter(ua -> ua.getAchievement() == Achievement.I_AM_THE_GREATEST)
                .count();
        assertEquals("I_AM_THE_GREATEST should appear only once", 1, count);
    }

    // ------------------------------------------------------------------
    // Edge cases
    // ------------------------------------------------------------------

    @Test
    public void testAchievementService_CannotInstantiate() {
        /* Verify the private constructor exists and class is a utility */
        try {
            java.lang.reflect.Constructor<AchievementService> ctor =
                    AchievementService.class.getDeclaredConstructor();
            assertTrue("Constructor should be private",
                    java.lang.reflect.Modifier.isPrivate(ctor.getModifiers()));
        } catch (NoSuchMethodException e) {
            fail("AchievementService should have a private no-arg constructor");
        }
    }

    // ------------------------------------------------------------------
    // Native SQL helpers for test data setup
    // ------------------------------------------------------------------

    private int insertQuiz(int creatorId) {
        executeUpdate(
                "INSERT INTO quizzes (creator_id, title, random_questions, one_page, "
                        + "immediate_correction, practice_mode) "
                        + "VALUES (?, 'Test Quiz', FALSE, TRUE, FALSE, FALSE)",
                creatorId);
        return queryLastInsertId();
    }

    private void insertQuizAttempt(int userId, int quizId, int score) {
        executeUpdate(
                "INSERT INTO quiz_attempts (user_id, quiz_id, score, max_score, time_taken_seconds) "
                        + "VALUES (?, ?, ?, ?, 60)",
                userId, quizId, score, score);
    }

    private int insertOtherUser() {
        String username = "test_ach_other_" + System.currentTimeMillis();
        User other = new User(0, username, "hash", "salt", false);
        userDAO.save(other);
        User saved = userDAO.findByUsername(username);
        return saved != null ? saved.getId() : 0;
    }

    // ------------------------------------------------------------------
    // Low-level helpers
    // ------------------------------------------------------------------

    private void executeUpdate(String sql, int... params) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                NativeQuery<?> query = session.createNativeQuery(sql);
                for (int i = 0; i < params.length; i++) {
                    query.setParameter(i + 1, params[i]);
                }
                query.executeUpdate();
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    private void executeDelete(String sql, int param) {
        executeUpdate(sql, param);
    }

    private int queryLastInsertId() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery<?> query = session.createNativeQuery("SELECT LAST_INSERT_ID()");
            Number result = (Number) query.uniqueResult();
            return result != null ? result.intValue() : 0;
        }
    }
}
