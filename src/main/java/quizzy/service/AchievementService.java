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
     * a quiz.
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
     * a quiz attempt.
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
     */
    public static boolean hasAchievement(int userId, Achievement achievement) {
        return userAchievementDAO.hasAchievement(userId, achievement);
    }



    private static long countColumn(String sql, int param) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery<?> query = session.createNativeQuery(sql);
            query.setParameter(1, param);
            Number result = (Number) query.uniqueResult();
            return result != null ? result.longValue() : 0L;
        }
    }
}
