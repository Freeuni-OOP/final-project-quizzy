package quizzy.service;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import quizzy.dao.ReportedQuizDAO;
import quizzy.dao.UserDAO;
import quizzy.model.ReportStatus;
import quizzy.model.ReportedQuiz;
import quizzy.model.User;
import quizzy.util.HibernateUtil;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for the {@link ReportedQuiz} entity lifecycle, {@link ReportedQuizDAO}
 * queries, and the PENDING → APPROVED/REJECTED state machine.
 */
public class ReportedQuizServiceTest {

    private static final UserDAO userDAO = new UserDAO();
    private static final ReportedQuizDAO reportedQuizDAO = new ReportedQuizDAO();

    private User reporter;
    private User reviewer;
    private int quizId;

    @BeforeClass
    public static void createNonEntityTables() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.createNativeQuery(
                        "CREATE TABLE IF NOT EXISTS quizzes ("
                                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                                + "creator_id INT NOT NULL, "
                                + "title VARCHAR(200), "
                                + "description TEXT, "
                                + "random_questions BOOLEAN DEFAULT FALSE, "
                                + "one_page BOOLEAN DEFAULT TRUE, "
                                + "immediate_correction BOOLEAN DEFAULT FALSE, "
                                + "practice_mode BOOLEAN DEFAULT FALSE"
                                + ")").executeUpdate();
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    @Before
    public void setUp() {
        /* Reporter (non-admin) */
        String rName = "rep_" + System.currentTimeMillis();
        reporter = new User(0, rName, "hash", "salt", false);
        userDAO.save(reporter);
        reporter = userDAO.findByUsername(rName);
        assertNotNull(reporter);

        /* Reviewer (admin) */
        String vName = "rev_" + System.currentTimeMillis();
        reviewer = new User(0, vName, "hash", "salt", true);
        userDAO.save(reviewer);
        reviewer = userDAO.findByUsername(vName);
        assertNotNull(reviewer);

        /* Quiz to report */
        exec("INSERT INTO quizzes (creator_id, title, random_questions, "
                + "one_page, immediate_correction, practice_mode) "
                + "VALUES (?, 'Reported Quiz', FALSE, TRUE, FALSE, FALSE)",
                reporter.getId());
        quizId = lastInsertId();
    }

    @After
    public void tearDown() {
        exec("DELETE FROM reported_quizzes WHERE reporter_id IN (?, ?)",
                reporter != null ? reporter.getId() : 0,
                reviewer != null ? reviewer.getId() : 0);
        exec("DELETE FROM quizzes WHERE id = ?", quizId);
        if (reporter != null && reporter.getId() > 0)
            exec("DELETE FROM users WHERE id = ?", reporter.getId());
        if (reviewer != null && reviewer.getId() > 0)
            exec("DELETE FROM users WHERE id = ?", reviewer.getId());
    }

    // ------------------------------------------------------------------
    // creation
    // ------------------------------------------------------------------

    @Test
    public void create_DefaultsToPending() {
        ReportedQuiz r = new ReportedQuiz(0, quizId, reporter, "Inappropriate content");

        assertEquals(0, r.getId());               /* not yet persisted */
        assertEquals(quizId, r.getQuizId());
        assertEquals(reporter, r.getReporter());
        assertEquals("Inappropriate content", r.getReason());
        assertEquals(ReportStatus.PENDING, r.getStatus());
        assertNotNull("createdAt should be set on construction", r.getCreatedAt());
        assertNull("resolvedAt should be null", r.getResolvedAt());
        assertNull("reviewedById should be null", r.getReviewedById());
    }

    @Test
    public void persist_SetsIdAndCanBeFound() {
        ReportedQuiz r = new ReportedQuiz(0, quizId, reporter, "Spam");
        reportedQuizDAO.save(r);

        assertTrue("ID should be assigned after save", r.getId() > 0);

        ReportedQuiz found = reportedQuizDAO.findById(ReportedQuiz.class, r.getId());
        assertNotNull(found);
        assertEquals(ReportStatus.PENDING, found.getStatus());
        assertEquals("Spam", found.getReason());
    }

    // ------------------------------------------------------------------
    // state machine  (immutable pattern — rebuild with new status)
    // ------------------------------------------------------------------

    @Test
    public void resolve_Approve_SetsResolutionFields() {
        ReportedQuiz r = createPendingReport("Offensive");

        ReportedQuiz resolved = new ReportedQuiz(
                r.getId(), r.getQuizId(), r.getReporter(), r.getReason(),
                ReportStatus.APPROVED, r.getCreatedAt(),
                LocalDateTime.now(), reviewer.getId());
        reportedQuizDAO.update(resolved);

        ReportedQuiz reloaded = reportedQuizDAO.findById(
                ReportedQuiz.class, r.getId());
        assertEquals(ReportStatus.APPROVED, reloaded.getStatus());
        assertNotNull("resolvedAt should be set", reloaded.getResolvedAt());
        assertEquals(reviewer.getId(), reloaded.getReviewedById().intValue());
    }

    @Test
    public void resolve_Reject_SetsResolutionFields() {
        ReportedQuiz r = createPendingReport("Disagreement");

        ReportedQuiz resolved = new ReportedQuiz(
                r.getId(), r.getQuizId(), r.getReporter(), r.getReason(),
                ReportStatus.REJECTED, r.getCreatedAt(),
                LocalDateTime.now(), reviewer.getId());
        reportedQuizDAO.update(resolved);

        ReportedQuiz reloaded = reportedQuizDAO.findById(
                ReportedQuiz.class, r.getId());
        assertEquals(ReportStatus.REJECTED, reloaded.getStatus());
        assertNotNull("resolvedAt should be set", reloaded.getResolvedAt());
        assertEquals(reviewer.getId(), reloaded.getReviewedById().intValue());
    }

    @Test
    public void resolve_AlreadyResolved_CanUpdate() {
        /* Resolving an already-resolved report is allowed at the DAO level
           (idempotency guard is in ReviewQueueServlet, not the entity). */

        ReportedQuiz r = createPendingReport("Old report");

        /* First resolution */
        ReportedQuiz approved = new ReportedQuiz(
                r.getId(), r.getQuizId(), r.getReporter(), r.getReason(),
                ReportStatus.APPROVED, r.getCreatedAt(),
                LocalDateTime.now(), reviewer.getId());
        reportedQuizDAO.update(approved);

        /* Second resolution (same status — no-op in practice) */
        ReportedQuiz approvedAgain = new ReportedQuiz(
                r.getId(), r.getQuizId(), r.getReporter(), r.getReason(),
                ReportStatus.APPROVED, r.getCreatedAt(),
                LocalDateTime.now().plusHours(1), reviewer.getId());
        reportedQuizDAO.update(approvedAgain);

        ReportedQuiz reloaded = reportedQuizDAO.findById(
                ReportedQuiz.class, r.getId());
        assertEquals(ReportStatus.APPROVED, reloaded.getStatus());
    }

    // ------------------------------------------------------------------
    // DAO queries
    // ------------------------------------------------------------------

    @Test
    public void findByStatus_FiltersCorrectly() {
        ReportedQuiz r1 = createPendingReport("First");
        ReportedQuiz r2 = createPendingReport("Second");

        /* Resolve one */
        ReportedQuiz resolved = new ReportedQuiz(
                r1.getId(), r1.getQuizId(), r1.getReporter(), r1.getReason(),
                ReportStatus.REJECTED, r1.getCreatedAt(),
                LocalDateTime.now(), reviewer.getId());
        reportedQuizDAO.update(resolved);

        List<ReportedQuiz> pending = reportedQuizDAO.findByStatus(
                ReportStatus.PENDING);
        assertEquals("Only one should still be PENDING", 1, pending.size());
        assertEquals(r2.getId(), pending.get(0).getId());
    }

    @Test
    public void findByQuiz_FiltersCorrectly() {
        /* Create a second quiz */
        exec("INSERT INTO quizzes (creator_id, title, random_questions, "
                + "one_page, immediate_correction, practice_mode) "
                + "VALUES (?, 'Other Quiz', FALSE, TRUE, FALSE, FALSE)",
                reporter.getId());
        int otherQuizId = lastInsertId();

        createPendingReport("Report on quiz 1");   /* uses quizId */
        ReportedQuiz r2 = new ReportedQuiz(0, otherQuizId, reporter, "Report on quiz 2");
        reportedQuizDAO.save(r2);

        List<ReportedQuiz> forQuiz1 = reportedQuizDAO.findByQuiz(quizId);
        assertEquals("Should have 1 report for quiz 1", 1, forQuiz1.size());

        List<ReportedQuiz> forQuiz2 = reportedQuizDAO.findByQuiz(otherQuizId);
        assertEquals("Should have 1 report for quiz 2", 1, forQuiz2.size());

        /* Cleanup */
        exec("DELETE FROM reported_quizzes WHERE quiz_id = ?", otherQuizId);
        exec("DELETE FROM quizzes WHERE id = ?", otherQuizId);
    }

    @Test
    public void fullConstructor_SetsAllFieldsCorrectly() {
        LocalDateTime created = LocalDateTime.of(2026, 1, 15, 10, 30);
        LocalDateTime resolved = LocalDateTime.of(2026, 1, 16, 14, 0);

        ReportedQuiz r = new ReportedQuiz(
                42, quizId, reporter, "Full constructor test",
                ReportStatus.APPROVED, created, resolved, reviewer.getId());

        assertEquals(42, r.getId());
        assertEquals(quizId, r.getQuizId());
        assertEquals(reporter, r.getReporter());
        assertEquals("Full constructor test", r.getReason());
        assertEquals(ReportStatus.APPROVED, r.getStatus());
        assertEquals(created, r.getCreatedAt());
        assertEquals(resolved, r.getResolvedAt());
        assertEquals(reviewer.getId(), r.getReviewedById().intValue());
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    private ReportedQuiz createPendingReport(String reason) {
        ReportedQuiz r = new ReportedQuiz(0, quizId, reporter, reason);
        reportedQuizDAO.save(r);
        return r;
    }

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

    private int lastInsertId() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return ((Number) s.createNativeQuery("SELECT LAST_INSERT_ID()")
                    .uniqueResult()).intValue();
        }
    }
}
