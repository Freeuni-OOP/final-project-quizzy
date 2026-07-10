package quizzy.service;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import quizzy.dao.UserDAO;
import quizzy.model.Announcement;
import quizzy.model.User;
import quizzy.util.HibernateUtil;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link AnnouncementService} — CRUD, immutable updates, and
 * edge cases.  Runs against H2 in-memory (no external MySQL needed).
 */
public class AnnouncementServiceTest {

    private static final UserDAO userDAO = new UserDAO();
    private User adminUser;
    private int creatorId;

    @BeforeClass
    public static void ensureSchema() {
        /* Mapped entities are created by hbm2ddl=create-drop — nothing to do */
    }

    @Before
    public void setUp() {
        String unique = "ann_admin_" + System.currentTimeMillis();
        adminUser = new User(0, unique, "hash", "salt", true);
        userDAO.save(adminUser);
        adminUser = userDAO.findByUsername(unique);
        assertNotNull("Failed to create admin user", adminUser);
        creatorId = adminUser.getId();
    }

    @After
    public void tearDown() {
        if (adminUser == null || adminUser.getId() == 0) return;
        int uid = adminUser.getId();
        exec("DELETE FROM announcements WHERE creator_id = ?", uid);
        exec("DELETE FROM users WHERE id = ?", uid);
    }

    // ------------------------------------------------------------------
    // create
    // ------------------------------------------------------------------

    @Test
    public void create_ValidInput_ReturnsPersistedAnnouncement() {
        Announcement a = AnnouncementService.createAnnouncement(
                "Welcome", "Hello world!", creatorId);

        assertNotNull(a);
        assertTrue("ID should be assigned", a.getId() > 0);
        assertEquals("Welcome", a.getTitle());
        assertEquals("Hello world!", a.getContent());
        assertEquals(creatorId, a.getCreatorId());
        assertNotNull("createdAt should be set", a.getCreatedAt());
    }

    @Test
    public void create_EmptyTitle_StillPersists() {
        /* Service layer does NOT validate — validation is in the servlet.
           This test verifies that empty content doesn't crash. */
        Announcement a = AnnouncementService.createAnnouncement("", "", creatorId);
        assertNotNull(a);
        assertTrue(a.getId() > 0);
        assertEquals("", a.getTitle());
    }

    // ------------------------------------------------------------------
    // update  (immutable pattern)
    // ------------------------------------------------------------------

    @Test
    public void update_Existing_PreservesCreatorAndTimestamp() {
        Announcement original = AnnouncementService.createAnnouncement(
                "Old Title", "Old body", creatorId);

        Announcement updated = AnnouncementService.updateAnnouncement(
                original.getId(), "New Title", "New body");

        assertNotNull(updated);
        assertEquals("New Title", updated.getTitle());
        assertEquals("New body", updated.getContent());
        assertEquals("Creator must be preserved", creatorId, updated.getCreatorId());
        assertEquals("createdAt must be preserved",
                original.getCreatedAt(), updated.getCreatedAt());
    }

    @Test
    public void update_Nonexistent_ReturnsNull() {
        Announcement result = AnnouncementService.updateAnnouncement(
                99999, "Title", "Content");
        assertNull(result);
    }

    // ------------------------------------------------------------------
    // delete
    // ------------------------------------------------------------------

    @Test
    public void delete_Existing_ReturnsTrue() {
        Announcement a = AnnouncementService.createAnnouncement(
                "To delete", "Body", creatorId);
        boolean deleted = AnnouncementService.deleteAnnouncement(a.getId());
        assertTrue("First delete should succeed", deleted);
    }

    @Test
    public void delete_AlreadyDeleted_ReturnsFalse() {
        Announcement a = AnnouncementService.createAnnouncement(
                "To delete", "Body", creatorId);
        assertTrue(AnnouncementService.deleteAnnouncement(a.getId()));
        assertFalse("Second delete should return false",
                AnnouncementService.deleteAnnouncement(a.getId()));
    }

    @Test
    public void delete_Nonexistent_ReturnsFalse() {
        assertFalse(AnnouncementService.deleteAnnouncement(99999));
    }

    // ------------------------------------------------------------------
    // query
    // ------------------------------------------------------------------

    @Test
    public void getRecentAnnouncements_RespectsLimit() {
        for (int i = 1; i <= 5; i++) {
            AnnouncementService.createAnnouncement(
                    "Title " + i, "Content " + i, creatorId);
        }

        List<Announcement> recent = AnnouncementService.getRecentAnnouncements(3);
        assertEquals("Should return at most 3", 3, recent.size());

        /* Verify newest-first ordering (highest ID first) */
        assertTrue("First should be newer than second",
                recent.get(0).getId() > recent.get(1).getId());
        assertTrue("Second should be newer than third",
                recent.get(1).getId() > recent.get(2).getId());
    }

    @Test
    public void getAllAnnouncements_ReturnsAllOrderedByDate() {
        for (int i = 1; i <= 4; i++) {
            AnnouncementService.createAnnouncement(
                    "Title " + i, "Content " + i, creatorId);
        }

        List<Announcement> all = AnnouncementService.getAllAnnouncements();
        assertEquals(4, all.size());
        for (int i = 1; i < all.size(); i++) {
            assertTrue("Should be newest-first",
                    all.get(i - 1).getId() > all.get(i).getId());
        }
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    private void exec(String sql, int... params) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            try {
                NativeQuery<?> q = s.createNativeQuery(sql);
                for (int i = 0; i < params.length; i++) {
                    q.setParameter(i + 1, params[i]);
                }
                q.executeUpdate();
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
}
