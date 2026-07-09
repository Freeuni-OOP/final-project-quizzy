package quizzy.web;

import quizzy.dao.QuestionDAO;
import quizzy.dao.QuizDAO;
import quizzy.model.Quiz;
import quizzy.model.question.Question;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * M3-owned. GET /quiz/take?id={quizId}[&practice=true]
 *
 * Full practice-mode repeat/rotation logic (repeat-until-3-correct, remove
 * from rotation) is M5's extension per the team plan — this servlet only
 * wires the "practice" flag through so QuizSubmitServlet knows not to persist
 * a QuizAttempt for practice runs. It behaves like a normal single-pass quiz
 * otherwise, which M5 can layer the real rotation logic on top of later.
 */
@WebServlet("/quiz/take")
public class QuizTakeServlet extends HttpServlet {

    private final QuizDAO quizDAO = new QuizDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer quizId = parseId(request.getParameter("id"));
        if (quizId == null) {
            request.setAttribute("quiz", null);
            request.getRequestDispatcher("/WEB-INF/jsp/quiz-take.jsp").forward(request, response);
            return;
        }

        Quiz quiz = quizDAO.findById(quizId);
        request.setAttribute("quiz", quiz);

        if (quiz == null) {
            request.getRequestDispatcher("/WEB-INF/jsp/quiz-take.jsp").forward(request, response);
            return;
        }

        List<Question> questions = questionDAO.findByQuiz(quizId);

        if (questions.isEmpty()) {
            request.setAttribute("noQuestions", true);
            request.getRequestDispatcher("/WEB-INF/jsp/quiz-take.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        String key = ActiveQuizSession.sessionKey(quizId);
        ActiveQuizSession state = (ActiveQuizSession) session.getAttribute(key);

        boolean practice = "true".equals(request.getParameter("practice"));

        // Start a fresh attempt if none is in progress yet for this quiz.
        if (state == null) {
            List<Integer> order = new ArrayList<>();
            for (Question q : questions) {
                order.add(q.getId());
            }
            if (quiz.isRandomQuestions()) {
                Collections.shuffle(order);
            }
            state = new ActiveQuizSession(quizId, practice, order);
            session.setAttribute(key, state);
        }

        // index questions by id for quick lookup regardless of display order
        Map<Integer, Question> byId = new HashMap<>();
        for (Question q : questions) {
            byId.put(q.getId(), q);
        }

        if (quiz.isOnePage()) {
            List<Question> orderedQuestions = new ArrayList<>();
            for (Integer qid : state.getQuestionOrder()) {
                orderedQuestions.add(byId.get(qid));
            }
            request.setAttribute("orderedQuestions", orderedQuestions);
        } else {
            if (state.isComplete()) {
                // shouldn't normally happen (submit servlet redirects away once
                // complete), but guard anyway in case of a stray refresh
                session.removeAttribute(key);
                response.sendRedirect(request.getContextPath() + "/quiz/summary?id=" + quizId);
                return;
            }
            Question current = byId.get(state.currentQuestionId());
            request.setAttribute("currentQuestion", current);
            request.setAttribute("questionNumber", state.getCurrentIndex() + 1);
            request.setAttribute("totalQuestions", state.getQuestionOrder().size());
        }

        request.setAttribute("practice", state.isPractice());
        request.getRequestDispatcher("/WEB-INF/jsp/quiz-take.jsp").forward(request, response);
    }

    private Integer parseId(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }
}
