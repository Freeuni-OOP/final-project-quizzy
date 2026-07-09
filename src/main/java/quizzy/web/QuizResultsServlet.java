package quizzy.web;

import quizzy.dao.QuizAttemptDAO;
import quizzy.model.QuizAttempt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * M3-owned. GET /quiz/results?attemptId={attemptId}
 *
 * Not gated by AuthFilter or an ownership check — consistent with how
 * /quiz/summary and /quiz/take currently behave (see notes on those
 * servlets). Worth the team deciding together whether results should be
 * private to the attempt's own user.
 */
@WebServlet("/quiz/results")
public class QuizResultsServlet extends HttpServlet {

    private final QuizAttemptDAO attemptDAO = new QuizAttemptDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer attemptId = parseId(request.getParameter("attemptId"));
        QuizAttempt attempt = (attemptId == null) ? null : attemptDAO.findById(attemptId);

        request.setAttribute("attempt", attempt);
        request.getRequestDispatcher("/WEB-INF/jsp/quiz-results.jsp").forward(request, response);
    }

    private Integer parseId(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }
}
