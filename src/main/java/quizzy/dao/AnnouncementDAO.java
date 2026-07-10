package quizzy.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import quizzy.model.Announcement;

import java.util.List;

/**
 * Data Access Object for {@link Announcement} entities.
 */
public class AnnouncementDAO extends BaseDAO<Announcement> {

    public AnnouncementDAO() {
        super();
    }

    // Returns the most recent announcements, ordered by creation date descending.
    public List<Announcement> findRecent(int limit) {
        Session session = sessionFactory.openSession();
        try {
            Query<Announcement> query = session.createQuery(
                    "FROM Announcement ORDER BY createdAt DESC", Announcement.class);
            query.setMaxResults(limit);
            return query.list();
        } finally {
            session.close();
        }
    }

    // Returns all announcements created by a specific admin.
    public List<Announcement> findByCreator(int creatorId) {
        Session session = sessionFactory.openSession();
        try {
            Query<Announcement> query = session.createQuery(
                    "FROM Announcement WHERE creatorId = :creatorId ORDER BY createdAt DESC",
                    Announcement.class);
            query.setParameter("creatorId", creatorId);
            return query.list();
        } finally {
            session.close();
        }
    }
}
