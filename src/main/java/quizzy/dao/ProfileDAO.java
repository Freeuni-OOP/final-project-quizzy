package quizzy.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import quizzy.model.Achievement;
import quizzy.model.AttemptSummary;
import quizzy.model.UserAchievement;
import quizzy.util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Read-only queries for the profile page.
 * Uses M4's {@link UserAchievementDAO} for achievements and native SQL for
 * quiz attempts (which span M1's quizzes and quiz_attempts tables).
 */
public class ProfileDAO {

    private final UserAchievementDAO userAchievementDAO = new UserAchievementDAO();

    /**
     * Returns the user's quiz attempt history with quiz titles.
     */
    public List<AttemptSummary> getAttempts(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            @SuppressWarnings("unchecked")
            List<Object[]> rows = session.createNativeQuery(
                    "SELECT a.quiz_id, q.title, a.score, a.max_score, a.time_taken_seconds "
                            + "FROM quiz_attempts a JOIN quizzes q ON q.id = a.quiz_id "
                            + "WHERE a.user_id = :uid ORDER BY a.id DESC")
                    .setParameter("uid", userId)
                    .list();

            List<AttemptSummary> attempts = new ArrayList<>();
            for (Object[] row : rows) {
                attempts.add(new AttemptSummary(
                        ((Number) row[0]).intValue(),
                        (String) row[1],
                        ((Number) row[2]).intValue(),
                        ((Number) row[3]).intValue(),
                        ((Number) row[4]).longValue()));
            }
            return attempts;
        }
    }

    /**
     * Returns the achievements earned by a user.
     * Uses M4's enum-based Achievement system (no achievements lookup table).
     */
    public List<Achievement> getAchievements(int userId) {
        List<UserAchievement> earned = userAchievementDAO.findByUser(userId);
        return earned.stream()
                .map(UserAchievement::getAchievement)
                .collect(Collectors.toList());
    }
}
