package quizzy.admin;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import quizzy.dao.UserDAO;
import quizzy.model.User;
import quizzy.util.HibernateUtil;
import quizzy.util.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ModerationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /* AJAX user lookup — returns JSON { id, username, isAdmin } */
        String lookup = request.getParameter("lookup");
        if (lookup != null) {
            writeJson(response, lookupUserJson(parseId(lookup)));
            return;
        }

        /* AJAX quiz lookup — returns JSON { id, title, creator } */
        String qlookup = request.getParameter("qlookup");
        if (qlookup != null) {
            writeJson(response, lookupQuizJson(parseId(qlookup)));
            return;
        }

        request.setAttribute("message", request.getParameter("message"));
        request.getRequestDispatcher("/WEB-INF/admin/moderation.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String message;

        if (!SessionUtils.isAdmin(request.getSession())) {
            message = "You do not have permission to perform this action.";
        } else {
            try {
                switch (action != null ? action : "") {
                    case "promote":
                        message = promoteUser(request);
                        break;
                    case "remove-user":
                        message = removeUser(request);
                        break;
                    case "remove-quiz":
                        message = removeQuiz(request);
                        break;
                    case "clear-history":
                        message = clearQuizHistory(request);
                        break;
                    default:
                        message = "Unknown action.";
                        break;
                }
            } catch (RuntimeException e) {
                message = "An error occurred: " + e.getMessage();
            }
        }

        response.sendRedirect(request.getContextPath()
                + "/admin/moderation?message="
                + java.net.URLEncoder.encode(message, "UTF-8"));
    }

    /* ---- Action handlers ---- */

    private String promoteUser(HttpServletRequest request) {
        int userId = parseId(request.getParameter("userId"));
        if (userId <= 0) {
            return "Invalid user ID.";
        }

        // First check whether the user exists and is already an admin.
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Object result = session.createNativeQuery(
                            "SELECT is_admin FROM users WHERE id = :uid")
                    .setParameter("uid", userId)
                    .uniqueResult();
            if (result == null) {
                return "User #" + userId + " not found.";
            }
            if (Boolean.TRUE.equals(result)) {
                return "User #" + userId + " is already an admin.";
            }
        }

        int updated = executeUpdate("UPDATE users SET is_admin = TRUE WHERE id = :uid",
                "uid", userId);
        return updated > 0
                ? "User #" + userId + " has been promoted to admin."
                : "User #" + userId + " not found.";
    }

    // Deletes a user and all their associated data.
    private String removeUser(HttpServletRequest request) {
        int userId = parseId(request.getParameter("userId"));
        if (userId <= 0) {
            return "Invalid user ID.";
        }

        // Prevent an admin from deleting themselves.
        int currentUserId = SessionUtils.getCurrentUser(request.getSession()).getId();
        if (userId == currentUserId) {
            return "You cannot delete your own account.";
        }

        int rows = cascadeDelete(
                /* 1 */ "DELETE FROM user_achievements WHERE user_id = :uid",
                /* 2 */ "DELETE FROM reported_quizzes WHERE reporter_id = :uid OR reviewed_by = :rvid",
                /* 3 */ "DELETE FROM messages WHERE sender_id = :uid OR receiver_id = :rvid",
                /* 4 */ "DELETE FROM friendships WHERE requester_id = :uid OR receiver_id = :rvid",
                /* 5 */ "DELETE FROM quiz_attempts WHERE user_id = :uid",
                /* 6 */ "DELETE FROM quizzes WHERE creator_id = :uid",
                /* 7 */ "DELETE FROM announcements WHERE creator_id = :uid",
                /* 8 */ "DELETE FROM users WHERE id = :uid"
        ).with("uid", userId).with("rvid", userId).execute();

        return rows > 0
                ? "User #" + userId + " and all associated data have been removed."
                : "User #" + userId + " not found.";
    }

    // Deletes a quiz and all its dependent rows.

    private String removeQuiz(HttpServletRequest request) {
        int quizId = parseId(request.getParameter("quizId"));
        if (quizId <= 0) {
            return "Invalid quiz ID.";
        }

        int rows = cascadeDelete(
                /* 1 */ "DELETE a FROM answers a "
                      + "INNER JOIN questions q ON a.question_id = q.id "
                      + "WHERE q.quiz_id = :qid",
                /* 2 */ "DELETE FROM questions WHERE quiz_id = :qid",
                /* 3 */ "DELETE FROM messages WHERE quiz_id = :qid",
                /* 4 */ "DELETE FROM reported_quizzes WHERE quiz_id = :qid",
                /* 5 */ "DELETE FROM quiz_attempts WHERE quiz_id = :qid",
                /* 6 */ "DELETE FROM quizzes WHERE id = :qid"
        ).with("qid", quizId).execute();

        return rows > 0
                ? "Quiz #" + quizId + " and all associated data have been removed."
                : "Quiz #" + quizId + " not found.";
    }

    private String clearQuizHistory(HttpServletRequest request) {
        int quizId = parseId(request.getParameter("quizId"));
        if (quizId <= 0) {
            return "Invalid quiz ID.";
        }

        int cleared = executeUpdate(
                "DELETE FROM quiz_attempts WHERE quiz_id = :qid", "qid", quizId);
        return cleared > 0
                ? "Cleared " + cleared + " attempt(s) for quiz #" + quizId + "."
                : "No attempts found for quiz #" + quizId + ".";
    }

    /* ---- Helpers ---- */

    private static int parseId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void writeJson(HttpServletResponse response, String json)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    private static String lookupUserJson(int id) {
        if (id <= 0) return "null";
        User user = new UserDAO().findById(id);
        if (user == null) return "null";
        return String.format("{\"id\":%d,\"username\":\"%s\",\"isAdmin\":%b}",
                user.getId(), user.getUsername(), user.isAdmin());
    }

    private static String lookupQuizJson(int id) {
        if (id <= 0) return "null";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Object[] row = (Object[]) session.createNativeQuery(
                            "SELECT q.title, u.username FROM quizzes q " +
                            "JOIN users u ON u.id = q.creator_id WHERE q.id = :id")
                    .setParameter("id", id)
                    .uniqueResult();
            if (row == null) return "null";
            return String.format("{\"id\":%d,\"title\":\"%s\",\"creator\":\"%s\"}",
                    id, row[0], row[1]);
        }
    }

    // Executes a single native SQL UPDATE or DELETE inside its own transaction.
    private static int executeUpdate(String sql, String paramName, int paramValue) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            session.beginTransaction();
            Query<?> query = session.createNativeQuery(sql);
            query.setParameter(paramName, paramValue);
            int rows = query.executeUpdate();
            session.getTransaction().commit();
            return rows;
        } catch (RuntimeException e) {
            try {
                session.getTransaction().rollback();
            } catch (RuntimeException ignored) {
                /* transaction may already be rolled back */
            }
            throw e;
        } finally {
            session.close();
        }
    }

    /**
     * Builder for executing a chain of native DELETE statements inside a
     * single transaction.  If any statement fails the entire batch is rolled back.
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

        // Runs all statements in a single transaction and returns the row count
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
