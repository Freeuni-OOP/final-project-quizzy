package quizzy.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import quizzy.model.User;

/**
 * Data Access Object for {@link User} entities.
 */
public class UserDAO extends BaseDAO<User> {

    public UserDAO() {
        super();
    }

    /**
     * Finds a user by their unique username.
     *
     * @param username the username to search for
     * @return the matching User, or {@code null} if not found
     */
    public User findByUsername(String username) {
        Session session = sessionFactory.openSession();
        try {
            Query<User> query = session.createQuery(
                    "FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Returns all users filtered by their admin status.
     *
     * @param isAdmin whether to retrieve admins (true) or non-admins (false)
     * @return list of matching users
     */
    public java.util.List<User> findByAdminStatus(boolean isAdmin) {
        Session session = sessionFactory.openSession();
        try {
            Query<User> query = session.createQuery(
                    "FROM User WHERE isAdmin = :isAdmin", User.class);
            query.setParameter("isAdmin", isAdmin);
            return query.list();
        } finally {
            session.close();
        }
    }
}
