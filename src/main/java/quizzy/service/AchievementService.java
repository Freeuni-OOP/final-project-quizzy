package quizzy.service;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import quizzy.dao.UserAchievementDAO;
import quizzy.dao.UserDAO;
import quizzy.model.Achievement;
import quizzy.model.User;
import quizzy.model.UserAchievement;
import quizzy.util.HibernateUtil;

import java.util.List;

/**
 * Core achievement engine — checks milestone conditions and awards achievements.
 * All methods are static; the class cannot be instantiated.
 *
 * <p>Integration hooks for other members:
 * <ul>
 *   <li>M1 calls {@link #checkAuthorAchievements(int)} after quiz creation</li>
 *   <li>M1 calls {@link #checkQuizTakerAchievements(int, int, int)} after quiz submission</li>
 *   <li>M5 calls {@link #awardAchievement(int, Achievement)} after practice mode</li>
 *   <li>M3 calls {@link #getUserAchievements(int)} for profile display</li>
 * </ul>
 *
 * <p>{@code Quiz} and {@code QuizAttempt} are plain POJOs (not Hibernate entities),
 * so queries against {@code quizzes} and {@code quiz_attempts} tables use native SQL.
 */
public final class AchievementService {

    private static final UserAchievementDAO userAchievementDAO = new UserAchievementDAO();
    private static final UserDAO userDAO = new UserDAO();

    private AchievementService() {
        /* utility class — prevent instantiation */
    }

    // -----------------------------------------------------------------------
    // Public integration hooks
    // -----------------------------------------------------------------------

    /**
     * Checks and awards author-based achievements for a user who just created
     * a quiz. Called by M1 after persisting the quiz.
     *
     * <p>Awards {@code AMATEUR_AUTHOR} (≥1), {@code PROLIFIC_AUTHOR} (≥5),
     * and {@code PRODIGIOUS_AUTHOR} (≥10) based on the user's total quiz count.</p>
     *
     * @param userId the ID of the user who created the quiz
     */
    public static void checkAuthorAchievements(int userId) {
        long quizCount = countColumn("SELECT COUNT(*) FROM quizzes WHERE creator_id = ?", userId);

        if (quizCount >= 1) {
            awardAchievement(userId, Achievement.AMATEUR_AUTHOR);
        }
        if (quizCount >= 5) {
            awardAchievement(userId, Achievement.PROLIFIC_AUTHOR);
        }
        if (quizCount >= 10) {
            awardAchievement(userId, Achievement.PRODIGIOUS_AUTHOR);
        }
    }

    /**
     * Checks and awards taker-based achievements for a user who just completed
     * a quiz attempt. Called by M1 after persisting the attempt.
     *
     * <p>Awards {@code QUIZ_MACHINE} when the user has taken ≥10 quizzes,
     * and {@code I_AM_THE_GREATEST} when the user's score is the highest
     * recorded for that quiz at the moment of completion.</p>
     *
     * @param userId the ID of the user who submitted the attempt
     * @param quizId the ID of the quiz that was taken
     * @param score  the score the user achieved on this attempt
     */
    public static void checkQuizTakerAchievements(int userId, int quizId, int score) {
        long attemptCount = countColumn(
                "SELECT COUNT(*) FROM quiz_attempts WHERE user_id = ?", userId);

        if (attemptCount >= 10) {
            awardAchievement(userId, Achievement.QUIZ_MACHINE);
        }

        /* I_AM_THE_GREATEST: check if this score is the highest for the quiz.
           The current attempt is already persisted when this hook fires, so
           MAX(score) will be at least this score.  If the user already holds
           the record from a previous attempt, dedup prevents double-awarding. */
        long currentMax = countColumn(
                "SELECT COALESCE(MAX(score), 0) FROM quiz_attempts WHERE quiz_id = ?", quizId);

        if (score >= currentMax) {
            awardAchievement(userId, Achievement.I_AM_THE_GREATEST);
        }
    }

    /**
     * Awards a specific achievement to a user if they do not already have it.
     * Safe to call multiple times — deduplication is handled.
     *
     * @param userId      the ID of the user receiving the achievement
     * @param achievement the achievement to award
     * @return {@code true} if the achievement was newly awarded,
     *         {@code false} if the user already had it or the user was not found
     */
    public static boolean awardAchievement(int userId, Achievement achievement) {
        if (hasAchievement(userId, achievement)) {
            return false;
        }

        User user = userDAO.findById(User.class, userId);
        if (user == null) {
            return false;
        }

        UserAchievement ua = new UserAchievement(0, user, achievement);
        userAchievementDAO.save(ua);
        return true;
    }

    /**
     * Returns every achievement earned by the given user.
     *
     * @param userId the ID of the user
     * @return list of earned achievements (empty if none)
     */
    public static List<UserAchievement> getUserAchievements(int userId) {
        return userAchievementDAO.findByUser(userId);
    }

    /**
     * Checks whether a user has already earned a specific achievement.
     *
     * @param userId      the ID of the user
     * @param achievement the achievement to check
     * @return {@code true} if the user already has this achievement
     */
    public static boolean hasAchievement(int userId, Achievement achievement) {
        return userAchievementDAO.hasAchievement(userId, achievement);
    }

    // -----------------------------------------------------------------------
    // Private native-SQL helpers
    //
    // Quiz and QuizAttempt are plain POJOs (not @Entity), so we cannot use
    // HQL.  These helpers run native SQL against the raw table names and
    // return a single long value.
    // -----------------------------------------------------------------------

    /**
     * Executes a native SQL query that returns a single scalar number and
     * extracts it as a {@code long}.
     *
     * @param sql    the native SQL string with one {@code ?} placeholder
     * @param param  the value to bind to the placeholder
     * @return the scalar result as a {@code long} (0 if the query returns null)
     */
    private static long countColumn(String sql, int param) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            NativeQuery<?> query = session.createNativeQuery(sql);
            query.setParameter(1, param);
            Number result = (Number) query.uniqueResult();
            return result != null ? result.longValue() : 0L;
        } finally {
            session.close();
        }
    }
}
