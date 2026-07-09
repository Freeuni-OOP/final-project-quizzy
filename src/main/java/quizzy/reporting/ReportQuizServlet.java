package quizzy.reporting;

import quizzy.dao.ReportedQuizDAO;
import quizzy.model.ReportedQuiz;
import quizzy.model.User;
import quizzy.util.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Handles user-submitted quiz reports — mapped to {@code /report-quiz}
 * in {@code web.xml}.
 *
 * <p><b>GET</b> — shows the report form. Requires {@code quizId} parameter.<br>
 * <b>POST</b> — creates a {@link ReportedQuiz} with status {@code PENDING}.</p>
 */
public class ReportQuizServlet extends HttpServlet {

    private final ReportedQuizDAO reportedQuizDAO = new ReportedQuizDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = SessionUtils.getCurrentUser(request.getSession());

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath()
                    + "/login.jsp?message="
                    + java.net.URLEncoder.encode("You must be logged in to report a quiz.", StandardCharsets.UTF_8));
            return;
        }

        String quizIdParam = request.getParameter("quizId");
        if (quizIdParam == null || quizIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        request.setAttribute("quizId", quizIdParam);
        request.setAttribute("message", request.getParameter("message"));

        request.getRequestDispatcher("/WEB-INF/report-form.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        User currentUser = SessionUtils.getCurrentUser(request.getSession());

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath()
                    + "/login.jsp?message="
                    + java.net.URLEncoder.encode("You must be logged in to report a quiz.", StandardCharsets.UTF_8));
            return;
        }

        String quizIdParam = request.getParameter("quizId");
        String reason = request.getParameter("reason");

        int quizId;
        try {
            quizId = Integer.parseInt(quizIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath()
                    + "/report-quiz?quizId=" + quizIdParam
                    + "&message=" + java.net.URLEncoder.encode("Invalid quiz ID.", StandardCharsets.UTF_8));
            return;
        }

        if (reason == null || reason.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath()
                    + "/report-quiz?quizId=" + quizId
                    + "&message=" + java.net.URLEncoder.encode("Please provide a reason for your report.", StandardCharsets.UTF_8));
            return;
        }

        try {
            ReportedQuiz report = new ReportedQuiz(0, quizId, currentUser, reason.trim());
            reportedQuizDAO.save(report);

            response.sendRedirect(request.getContextPath()
                    + "/report-quiz?quizId=" + quizId
                    + "&message=" + java.net.URLEncoder.encode("Thank you! Your report has been submitted for review.", StandardCharsets.UTF_8));
        } catch (RuntimeException e) {
            response.sendRedirect(request.getContextPath()
                    + "/report-quiz?quizId=" + quizId
                    + "&message=" + java.net.URLEncoder.encode("Error submitting report: " + e.getMessage(), StandardCharsets.UTF_8));
        }
    }
}
