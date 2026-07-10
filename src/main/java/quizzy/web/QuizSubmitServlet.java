package quizzy.web;

import quizzy.model.Quiz;
import quizzy.model.User;
import quizzy.model.question.Question;
import quizzy.service.AchievementService;
import quizzy.service.QuizEngine;
import quizzy.service.QuizResult;
import quizzy.service.QuizSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/quiz/submit")
public class QuizSubmitServlet extends HttpServlet {

    private final QuizEngine engine = new QuizEngine();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User me = SessionUtil.current(request);
        if (me == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null) {
            response.sendRedirect(request.getContextPath() + "/quizzes");
            return;
        }

        QuizSession session = (QuizSession) httpSession.getAttribute(
                TakeQuizServlet.SESSION_KEY);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/quizzes");
            return;
        }

        Quiz quiz = session.getQuiz();
        List<Question> questions = session.getQuestions();
        boolean onePage = quiz.isOnePage();

        if (onePage) {
            /* ---- one-page: grade all answers at once ---- */
            Map<Integer, String> answers = new HashMap<>();
            for (Question q : questions) {
                String paramName = "answer_" + q.getId();
                String value = request.getParameter(paramName);
                answers.put(q.getId(), value != null ? value.trim() : "");
            }

            long elapsed = session.getElapsedSeconds();
            QuizResult result = engine.finishQuiz(quiz, me, questions,
                    answers, elapsed, session.isPracticeMode());

            httpSession.removeAttribute(TakeQuizServlet.SESSION_KEY);

            /* Award taker achievements after quiz completion. */
            if (!session.isPracticeMode() && result.getAttempt() != null) {
                AchievementService.checkQuizTakerAchievements(
                        me.getId(), quiz.getId(), result.getScore());
            }

            request.setAttribute("attempt", result.getAttempt());
            request.getRequestDispatcher("/WEB-INF/jsp/quiz-results.jsp")
                    .forward(request, response);

        } else {
            /* ---- multi-page: record answer, advance ---- */
            Question current = session.getCurrentQuestion();
            if (current == null) {
                /* Session already finished — shouldn't happen. */
                httpSession.removeAttribute(TakeQuizServlet.SESSION_KEY);
                response.sendRedirect(request.getContextPath()
                        + "/quiz/summary?id=" + quiz.getId());
                return;
            }

            String answer = request.getParameter("answer_" + current.getId());
            engine.recordAnswer(session, current.getId(),
                    answer != null ? answer.trim() : "");

            session.moveToNextQuestion();

            if (session.isFinished()) {
                /* Last question done — grade everything. */
                QuizResult result = engine.finishQuiz(session, me);

                httpSession.removeAttribute(TakeQuizServlet.SESSION_KEY);

                if (!session.isPracticeMode() && result.getAttempt() != null) {
                    AchievementService.checkQuizTakerAchievements(
                            me.getId(), quiz.getId(), result.getScore());
                }

                request.setAttribute("result", result);
                request.getRequestDispatcher("/WEB-INF/jsp/quiz-feedback.jsp")
                        .forward(request, response);
            } else {
                /* Show the next question. */
                Question next = session.getCurrentQuestion();
                request.setAttribute("quiz", quiz);
                request.setAttribute("practice", session.isPracticeMode());
                request.setAttribute("orderedQuestions", questions);
                request.setAttribute("currentQuestion", next);
                request.setAttribute("questionNumber",
                        session.getCurrentQuestionIndex() + 1);
                request.setAttribute("totalQuestions", questions.size());

                request.getRequestDispatcher("/WEB-INF/jsp/quiz-take.jsp")
                        .forward(request, response);
            }
        }
    }
}
