package quizzy.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import quizzy.model.Achievement;
import quizzy.model.UserAchievement;

import java.util.List;

/**
 * Data Access Object for {@link UserAchievement} entities.
 */
public class UserAchievementDAO extends BaseDAO<UserAchievement> {

    public UserAchievementDAO() {
        super();
    }

    // Returns all achievements earned by a specific user.
    public List<UserAchievement> findByUser(int userId) {
        Session session = sessionFactory.openSession();
        try {
            Query<UserAchievement> query = session.createQuery(
                    "FROM UserAchievement ua WHERE ua.user.id = :userId", UserAchievement.class);
            query.setParameter("userId", userId);
            return query.list();
        } finally {
            session.close();
        }
    }

    // Checks whether a user has already earned a specific achievement.
    public boolean hasAchievement(int userId, Achievement achievement) {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM UserAchievement ua "
                            + "WHERE ua.user.id = :userId AND ua.achievement = :achievement",
                    Long.class);
            query.setParameter("userId", userId);
            query.setParameter("achievement", achievement);
            return query.uniqueResult() > 0;
        } finally {
            session.close();
        }
    }
}
