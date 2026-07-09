package quizzy.admin;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import quizzy.dao.ReportedQuizDAO;
import quizzy.model.ReportStatus;
import quizzy.model.ReportedQuiz;
import quizzy.util.HibernateUtil;
import quizzy.util.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin review queue for reported quizzes — mapped to {@code /admin/reports}
 * in {@code web.xml}.
 *
 * <p><b>GET</b> — lists all PENDING reports.<br>
 * <b>POST</b> — dispatches to approve or reject based on the
 * {@code action} parameter.</p>
 *
 * <p>Approve deletes the quiz via native SQL (Quiz is not a Hibernate entity).
 * Reject only updates the report status.</p>
 */
public class ReviewQueueServlet extends HttpServlet {

    private final ReportedQuizDAO reportedQuizDAO = new ReportedQuizDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<ReportedQuiz> pending = reportedQuizDAO.findByStatus(ReportStatus.PENDING);
        request.setAttribute("reports", pending);
        request.setAttribute("message", request.getParameter("message"));

        request.getRequestDispatcher("/WEB-INF/admin/reports.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String action = request.getParameter("action");
        String message;

        if (!SessionUtils.isAdmin(request.getSession())) {
            message = "You do not have permission to perform this action.";
        } else {
            switch (action != null ? action : "") {
                case "approve":
                    message = approveReport(request);
                    break;
                case "reject":
                    message = rejectReport(request);
                    break;
                default:
                    message = "Unknown action.";
                    break;
            }
        }

        response.sendRedirect(request.getContextPath()
                + "/admin/reports?message="
                + java.net.URLEncoder.encode(message, StandardCharsets.UTF_8));
    }

    /* ---- Action handlers ---- */

    private String approveReport(HttpServletRequest request) {
        int reportId = parseId(request.getParameter("reportId"));
        if (reportId <= 0) {
            return "Invalid report ID.";
        }

        ReportedQuiz report = reportedQuizDAO.findById(ReportedQuiz.class, reportId);
        if (report == null) {
            return "Report #" + reportId + " not found.";
        }

        if (report.getStatus() != ReportStatus.PENDING) {
            return "Report #" + reportId + " has already been resolved.";
        }

        int adminId = SessionUtils.getCurrentUser(request.getSession()).getId();

        /* Delete the reported quiz (native SQL cascade same pattern as ModerationServlet). */
        cascadeDelete(
                /* 1 */ "DELETE a FROM answers a "
                      + "INNER JOIN questions q ON a.question_id = q.id "
                      + "WHERE q.quiz_id = :qid",
                /* 2 */ "DELETE FROM questions WHERE quiz_id = :qid",
                /* 3 */ "DELETE FROM messages WHERE quiz_id = :qid",
                /* 4 */ "DELETE FROM reported_quizzes WHERE quiz_id = :qid",
                /* 5 */ "DELETE FROM quiz_attempts WHERE quiz_id = :qid",
                /* 6 */ "DELETE FROM quizzes WHERE id = :qid"
        ).with("qid", report.getQuizId()).execute();

        /* Update the report to APPROVED. Build new instance (immutable pattern). */
        ReportedQuiz resolved = new ReportedQuiz(
                report.getId(),
                report.getQuizId(),
                report.getReporter(),
                report.getReason());
        resolved = buildResolved(resolved, ReportStatus.APPROVED, adminId);
        reportedQuizDAO.update(resolved);

        return "Report #" + reportId + " approved. Quiz #" + report.getQuizId() + " has been removed.";
    }

    private String rejectReport(HttpServletRequest request) {
        int reportId = parseId(request.getParameter("reportId"));
        if (reportId <= 0) {
            return "Invalid report ID.";
        }

        ReportedQuiz report = reportedQuizDAO.findById(ReportedQuiz.class, reportId);
        if (report == null) {
            return "Report #" + reportId + " not found.";
        }

        if (report.getStatus() != ReportStatus.PENDING) {
            return "Report #" + reportId + " has already been resolved.";
        }

        int adminId = SessionUtils.getCurrentUser(request.getSession()).getId();

        /* Update the report to REJECTED. Build new instance (immutable pattern). */
        ReportedQuiz resolved = new ReportedQuiz(
                report.getId(),
                report.getQuizId(),
                report.getReporter(),
                report.getReason());
        resolved = buildResolved(resolved, ReportStatus.REJECTED, adminId);
        reportedQuizDAO.update(resolved);

        return "Report #" + reportId + " rejected. Quiz #" + report.getQuizId() + " remains.";
    }

    /* ---- Helpers ---- */

    private static int parseId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Builds a resolved copy of a ReportedQuiz using the full 8-arg constructor
     * (immutable entity pattern).
     */
    private static ReportedQuiz buildResolved(ReportedQuiz base, ReportStatus status, int reviewedById) {
        return new ReportedQuiz(
                base.getId(),
                base.getQuizId(),
                base.getReporter(),
                base.getReason(),
                status,
                base.getCreatedAt(),
                LocalDateTime.now(),
                reviewedById);
    }

    /**
     * Builder for executing a chain of native DELETE statements inside a
     * single transaction.  Copied from ModerationServlet's pattern.
     */
    private static CascadeDelete cascadeDelete(String... sqlStatements) {
        return new CascadeDelete(sqlStatements);
    }

    private static class CascadeDelete {
        private final String[] statements;
        private final List<Param> params = new ArrayList<>();

        CascadeDelete(String[] statements) {
            this.statements = statements;
        }

        CascadeDelete with(String name, int value) {
            params.add(new Param(name, value));
            return this;
        }

        int execute() {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = null;
            int lastRows = 0;
            try {
                tx = session.beginTransaction();
                for (String sql : statements) {
                    Query<?> query = session.createNativeQuery(sql);
                    for (Param p : params) {
                        query.setParameter(p.name, p.value);
                    }
                    lastRows = query.executeUpdate();
                }
                tx.commit();
                return lastRows;
            } catch (RuntimeException e) {
                if (tx != null) {
                    try {
                        tx.rollback();
                    } catch (RuntimeException ignored) {
                        /* transaction may already be rolled back */
                    }
                }
                throw e;
            } finally {
                session.close();
            }
        }

        private static class Param {
            final String name;
            final int value;

            Param(String name, int value) {
                this.name = name;
                this.value = value;
            }
        }
    }
}
