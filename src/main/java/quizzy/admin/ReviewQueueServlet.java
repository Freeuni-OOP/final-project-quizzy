package quizzy.admin;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import quizzy.dao.ReportedQuizDAO;
import quizzy.model.ReportStatus;
import quizzy.model.ReportedQuiz;
import quizzy.model.User;
import quizzy.util.HibernateUtil;
import quizzy.util.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

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

        User admin = SessionUtils.getCurrentUser(request.getSession());
        if (admin == null) {
            return "Your session has expired. Please log in again.";
        }
        int adminId = admin.getId();

        /* Read the quiz ID before the transaction (this is safe — the quiz
           column won't change even if another admin races us). */
        ReportedQuiz report = reportedQuizDAO.findById(ReportedQuiz.class, reportId);
        if (report == null) {
            return "Report #" + reportId + " not found.";
        }
        int quizId = report.getQuizId();

        /* Run the status update and cascade delete in a SINGLE transaction.
           The atomic UPDATE … WHERE status = 'PENDING' eliminates the TOCTOU
           race on double-resolution. */
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            /* 1. Atomically mark as APPROVED — only if still PENDING. */
            Query<?> statusUpdate = session.createNativeQuery(
                    "UPDATE reported_quizzes SET status = :status, "
                            + "resolved_at = :resolvedAt, reviewed_by = :reviewedBy "
                            + "WHERE id = :id AND status = :pending");
            statusUpdate.setParameter("status", ReportStatus.APPROVED.name());
            statusUpdate.setParameter("resolvedAt", LocalDateTime.now());
            statusUpdate.setParameter("reviewedBy", adminId);
            statusUpdate.setParameter("id", reportId);
            statusUpdate.setParameter("pending", ReportStatus.PENDING.name());
            int rows = statusUpdate.executeUpdate();

            if (rows == 0) {
                tx.rollback();
                return "Report #" + reportId + " has already been resolved.";
            }

            /* 2. Cascade-delete the quiz and all its dependent rows. */
            String[] cascadeSql = {
                    "DELETE a FROM answers a "
                            + "INNER JOIN questions q ON a.question_id = q.id "
                            + "WHERE q.quiz_id = :qid",
                    "DELETE FROM questions WHERE quiz_id = :qid",
                    "DELETE FROM messages WHERE quiz_id = :qid",
                    "DELETE FROM reported_quizzes WHERE quiz_id = :qid",
                    "DELETE FROM quiz_attempts WHERE quiz_id = :qid",
                    "DELETE FROM quizzes WHERE id = :qid"
            };
            for (String sql : cascadeSql) {
                session.createNativeQuery(sql)
                        .setParameter("qid", quizId)
                        .executeUpdate();
            }

            tx.commit();
            return "Report #" + reportId + " approved. Quiz #" + quizId + " has been removed.";
        } catch (RuntimeException e) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (RuntimeException ignored) {
                }
            }
            return "An error occurred while processing the report: " + e.getMessage();
        } finally {
            session.close();
        }
    }

    private String rejectReport(HttpServletRequest request) {
        int reportId = parseId(request.getParameter("reportId"));
        if (reportId <= 0) {
            return "Invalid report ID.";
        }

        User admin = SessionUtils.getCurrentUser(request.getSession());
        if (admin == null) {
            return "Your session has expired. Please log in again.";
        }
        int adminId = admin.getId();

        /* Atomically mark the report as REJECTED — only if still PENDING. */
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Query<?> statusUpdate = session.createNativeQuery(
                    "UPDATE reported_quizzes SET status = :status, "
                            + "resolved_at = :resolvedAt, reviewed_by = :reviewedBy "
                            + "WHERE id = :id AND status = :pending");
            statusUpdate.setParameter("status", ReportStatus.REJECTED.name());
            statusUpdate.setParameter("resolvedAt", LocalDateTime.now());
            statusUpdate.setParameter("reviewedBy", adminId);
            statusUpdate.setParameter("id", reportId);
            statusUpdate.setParameter("pending", ReportStatus.PENDING.name());
            int rows = statusUpdate.executeUpdate();

            tx.commit();

            if (rows == 0) {
                ReportedQuiz report = reportedQuizDAO.findById(ReportedQuiz.class, reportId);
                if (report == null) {
                    return "Report #" + reportId + " not found.";
                }
                return "Report #" + reportId + " has already been resolved.";
            }

            return "Report #" + reportId + " rejected. The quiz remains.";
        } catch (RuntimeException e) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (RuntimeException ignored) {
                }
            }
            return "An error occurred while processing the report: " + e.getMessage();
        } finally {
            session.close();
        }
    }

    /* ---- Helpers ---- */

    private static int parseId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}
