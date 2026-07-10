package quizzy.web;

import quizzy.model.Quiz;
import quizzy.model.User;
import quizzy.service.AttemptView;
import quizzy.service.QuizSummaryService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Quiz summary page with leaderboard — {@code GET /quiz/summary?id=X}.
 * Shows quiz details, top scorers (all-time and last 24h), average score,
 * and the current user's history on this quiz.
 */
@WebServlet("/quiz/summary")
public class QuizSummaryServlet extends HttpServlet {

    private final QuizSummaryService summaryService = new QuizSummaryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int quizId = parseId(request.getParameter("id"));
        if (quizId <= 0) {
            response.sendRedirect(request.getContextPath() + "/quizzes");
            return;
        }

        Quiz quiz = summaryService.getQuiz(quizId);
        if (quiz == null) {
            response.sendRedirect(request.getContextPath() + "/quizzes");
            return;
        }

        List<AttemptView> topAttempts = summaryService.getTopAttempts(quizId);
        List<AttemptView> recentTopAttempts = summaryService.getRecentTopAttempts(quizId);
        Double averageScore = summaryService.getAverageScore(quizId);

        /* Determine a representative maxScore for display from top attempt */
        Integer maxScoreForDisplay = null;
        if (topAttempts != null && !topAttempts.isEmpty()) {
            maxScoreForDisplay = topAttempts.get(0).getMaxScore();
        }

        /* User-specific data (only if logged in) */
        User me = SessionUtil.current(request);
        List<AttemptView> userAttempts = null;
        if (me != null) {
            userAttempts = summaryService.getUserAttempts(quizId, me.getId());
        }

        request.setAttribute("quiz", quiz);
        request.setAttribute("topAttempts", topAttempts);
        request.setAttribute("recentTopAttempts", recentTopAttempts);
        request.setAttribute("userAttempts", userAttempts);
        request.setAttribute("averageScore", averageScore);
        request.setAttribute("maxScore", maxScoreForDisplay);

        request.getRequestDispatcher("/WEB-INF/jsp/quiz-summary.jsp")
                .forward(request, response);
    }

    private static int parseId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
