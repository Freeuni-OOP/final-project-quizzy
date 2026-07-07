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
     *
     * @param limit the maximum number of announcements to return
     * @return the newest announcements, ordered descending by creation time
     */
    public static List<Announcement> getRecentAnnouncements(int limit) {
        return announcementDAO.findRecent(limit);
    }

    /**
     * Returns every announcement, ordered by creation time descending (newest first).
     *
     * @return all announcements ordered by creation time descending
     */
    public static List<Announcement> getAllAnnouncements() {
        return announcementDAO.findRecent(Integer.MAX_VALUE);
    }

    /**
     * Creates a new announcement and persists it to the database.
     *
     * @param title     the announcement headline (max 200 characters)
     * @param content   the announcement body text
     * @param creatorId the ID of the admin creating this announcement
     * @return the newly persisted {@link Announcement} (its {@code id} is now populated)
     */
    public static Announcement createAnnouncement(String title, String content, int creatorId) {
        Announcement announcement = new Announcement(0, creatorId, title, content);
        announcementDAO.save(announcement);
        return announcement;
    }

    /**
     * Updates the title and content of an existing announcement.
     * The creation timestamp and creator are left unchanged.
     *
     * @param id      the ID of the announcement to update
     * @param title   the new title
     * @param content the new content
     * @return the updated {@link Announcement}, or {@code null} if it does not exist
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
     *
     * @param id the ID of the announcement to delete
     * @return {@code true} if an announcement was deleted, {@code false} if none matched
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
