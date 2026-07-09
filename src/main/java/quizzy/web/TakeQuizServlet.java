package quizzy.web;

import quizzy.model.User;
import quizzy.model.question.Question;
import quizzy.service.QuizEngine;
import quizzy.service.QuizSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Starts a quiz attempt — {@code GET /quiz/take?id=X[&practice=true]}.
 *
 * <p>Creates a {@link QuizSession} and stores it in the HTTP session.
 * The session persistence key is {@code quizzy.quizSession}.  Subsequent
 * answer submissions ({@code POST /quiz/submit}) read and update this
 * session object.</p>
 */
@WebServlet("/quiz/take")
public class TakeQuizServlet extends HttpServlet {

    static final String SESSION_KEY = "quizzy.quizSession";

    private final QuizEngine engine = new QuizEngine();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User me = SessionUtil.current(request);
        if (me == null) {
            response.sendRedirect(request.getContextPath()
                    + "/login?next=" + java.net.URLEncoder.encode(
                    "/quiz/take?id=" + request.getParameter("id"), "UTF-8"));
            return;
        }

        int quizId = parseId(request.getParameter("id"));
        if (quizId <= 0) {
            response.sendRedirect(request.getContextPath() + "/quizzes");
            return;
        }

        boolean practice = "true".equals(request.getParameter("practice"));

        try {
            QuizSession session = engine.startQuiz(quizId, practice);
            List<Question> questions = session.getQuestions();

            if (questions.isEmpty()) {
                request.setAttribute("quiz", session.getQuiz());
                request.setAttribute("noQuestions", true);
                request.getRequestDispatcher("/WEB-INF/jsp/quiz-take.jsp")
                        .forward(request, response);
                return;
            }

            /* Store the session in the HTTP session so /quiz/submit can
               find it across multiple requests. */
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute(SESSION_KEY, session);

            /* Show the first question (multi-page) or all questions (one-page). */
            Question current = session.getCurrentQuestion();
            request.setAttribute("quiz", session.getQuiz());
            request.setAttribute("practice", practice);
            request.setAttribute("orderedQuestions", questions);
            request.setAttribute("currentQuestion", current);
            request.setAttribute("questionNumber", session.getCurrentQuestionIndex() + 1);
            request.setAttribute("totalQuestions", questions.size());

            request.getRequestDispatcher("/WEB-INF/jsp/quiz-take.jsp")
                    .forward(request, response);

        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/quizzes");
        }
    }

    private static int parseId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
