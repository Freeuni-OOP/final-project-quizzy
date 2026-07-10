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

    /**
     * Convenience overload for {@link #findById(Class, int)}.
     */
    public User findById(int id) {
        return findById(User.class, id);
    }

    /**
     * Creates and persists a new non-admin user.
     *
     * @param username     unique login name
     * @param passwordHash hashed password
     * @param salt         random salt
     * @return the newly persisted User (with ID populated)
     */
    public User createUser(String username, String passwordHash, String salt) {
        User user = new User(0, username, passwordHash, salt, false);
        save(user);
        return user;
    }

    /**
     * Searches users by partial username match, excluding the given user.
     *
     * @param query    the partial username to search for
     * @param excludeId ID of a user to exclude from results
     * @return matching users, limited to 50
     */
    public java.util.List<User> searchByUsername(String query, int excludeId) {
        Session session = sessionFactory.openSession();
        try {
            Query<User> q = session.createQuery(
                    "FROM User WHERE username LIKE :query AND id <> :excludeId ORDER BY username",
                    User.class);
            q.setParameter("query", "%" + query + "%");
            q.setParameter("excludeId", excludeId);
            q.setMaxResults(50);
            return q.list();
        } finally {
            session.close();
        }
    }

    /**
     * Checks whether a username is already taken.
     *
     * @param username the username to check
     * @return {@code true} if the username exists
     */
    public boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }
}
