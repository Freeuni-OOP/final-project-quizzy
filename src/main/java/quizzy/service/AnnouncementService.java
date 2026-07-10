package quizzy.service;

import quizzy.dao.AnnouncementDAO;
import quizzy.model.Announcement;

import java.util.List;

/**
 * Business-logic service for admin announcements.
 * All methods are static — no instantiation required.
 */
public final class AnnouncementService {

    private static final AnnouncementDAO announcementDAO = new AnnouncementDAO();

    private AnnouncementService() {
        /* utility class — prevent instantiation */
    }

    /**
     * Returns the most recent announcements for display on the homepage.
     */
    public static List<Announcement> getRecentAnnouncements(int limit) {
        return announcementDAO.findRecent(limit);
    }

    /**
     * Returns every announcement, ordered by creation time descending.
     */
    public static List<Announcement> getAllAnnouncements() {
        return announcementDAO.findRecent(Integer.MAX_VALUE);
    }

    /**
     * Creates a new announcement and persists it to the database.
     */
    public static Announcement createAnnouncement(String title, String content, int creatorId) {
        Announcement announcement = new Announcement(0, creatorId, title, content);
        announcementDAO.save(announcement);
        return announcement;
    }

    /**
     * Updates the title and content of an existing announcement.
     * The creation timestamp and creator are left unchanged.
     */
    public static Announcement updateAnnouncement(int id, String title, String content) {
        Announcement existing = announcementDAO.findById(Announcement.class, id);
        if (existing == null) {
            return null;
        }
        /* Build a new instance with updated fields — entities are immutable (no setters).
           Preserve the original creation timestamp. */
        Announcement updated = new Announcement(id, existing.getCreatorId(), title, content,
                existing.getCreatedAt());
        announcementDAO.update(updated);
        return updated;
    }

    /**
     * Deletes an announcement by its ID. No-op if the ID does not exist.
     */
    public static boolean deleteAnnouncement(int id) {
        Announcement existing = announcementDAO.findById(Announcement.class, id);
        if (existing == null) {
            return false;
        }
        announcementDAO.delete(existing);
        return true;
    }
}
