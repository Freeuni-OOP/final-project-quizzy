package quizzy.web;

import quizzy.dao.QuestionDAO;
import quizzy.dao.QuizAttemptDAO;
import quizzy.dao.QuizDAO;
import quizzy.model.Quiz;
import quizzy.model.QuizAttempt;
import quizzy.model.User;
import quizzy.model.question.Question;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * M3-owned. POST /quiz/submit — see contract: quizId + answer_{questionId} params.
 *
 * One-page mode: every question's answer arrives in a single request; grade
 * them all, finish immediately.
 *
 * Multi-page mode: only the CURRENT question's answer arrives per request
 * (ActiveQuizSession tracks which one that is); grade it, advance, and either
 * show immediate-correction feedback, silently move to the next question, or
 * finish if that was the last one.
 */
@WebServlet("/quiz/submit")
public class QuizSubmitServlet extends HttpServlet {

    private final QuizDAO quizDAO = new QuizDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final QuizAttemptDAO attemptDAO = new QuizAttemptDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer quizId = parseId(request.getParameter("quizId"));
        Quiz quiz = (quizId == null) ? null : quizDAO.findById(quizId);

        if (quiz == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        HttpSession session = request.getSession(true);
        String key = ActiveQuizSession.sessionKey(quizId);
        ActiveQuizSession state = (ActiveQuizSession) session.getAttribute(key);

        if (state == null) {
            // session expired, or someone POSTed here directly without GETting
            // /quiz/take first — restart cleanly rather than crash
            response.sendRedirect(request.getContextPath() + "/quiz/take?id=" + quizId);
            return;
        }

        List<Question> questions = questionDAO.findByQuiz(quizId);
        Map<Integer, Question> byId = new HashMap<>();
        for (Question q : questions) {
            byId.put(q.getId(), q);
        }

        if (quiz.isOnePage()) {
            int totalScore = 0;
            int totalMax = 0;
            for (Integer qid : state.getQuestionOrder()) {
                Question q = byId.get(qid);
                String answer = request.getParameter("answer_" + qid);
                totalScore += q.grade(answer);
                totalMax += q.getMaxScore();
            }
            finishAttempt(request, response, session, key, quiz, state, totalScore, totalMax);
            return;
        }

        // --- multi-page: grade just the current question ---
        Integer currentId = state.currentQuestionId();
        Question current = byId.get(currentId);
        String answer = request.getParameter("answer_" + currentId);
        int points = current.grade(answer);

        state.addToScore(points);
        state.setLastAnswerCorrect(points > 0);
        state.setLastCorrectAnswerText(String.join(" / ", current.getCorrectAnswers()));
        state.advance();

        if (state.isComplete()) {
            int totalMax = 0;
            for (Integer qid : state.getQuestionOrder()) {
                totalMax += byId.get(qid).getMaxScore();
            }
            finishAttempt(request, response, session, key, quiz, state, state.getScoreSoFar(), totalMax);
            return;
        }

        if (quiz.isImmediateCorrection()) {
            request.setAttribute("quiz", quiz);
            request.setAttribute("wasCorrect", state.isLastAnswerCorrect());
            request.setAttribute("correctAnswerText", state.getLastCorrectAnswerText());
            request.getRequestDispatcher("/WEB-INF/jsp/quiz-feedback.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/quiz/take?id=" + quizId);
        }
    }

    private void finishAttempt(HttpServletRequest request, HttpServletResponse response,
                                HttpSession session, String key, Quiz quiz, ActiveQuizSession state,
                                int score, int maxScore) throws IOException {

        session.removeAttribute(key);

        if (state.isPractice()) {
            // Per spec, practice-mode scores are never recorded.
            SessionUtil.setFlash(request, "Practice complete! You scored " + score + "/" + maxScore + ".");
            response.sendRedirect(request.getContextPath() + "/quiz/summary?id=" + quiz.getId());
            return;
        }

        User currentUser = SessionUtil.current(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?next=/quiz/summary?id=" + quiz.getId());
            return;
        }

        // ASSUMPTION — QuizAttempt.java's current (post-Hibernate) constructor
        // hasn't been confirmed directly; inferred from QuizAttemptDAO's HQL
        // field names (user, quiz, score, maxScore, timeTakenSeconds, completedAt).
        // id=0 for new rows, matching the convention used everywhere else
        // (Quiz, Question, Answer all take id=0 for inserts).
        QuizAttempt attempt = new QuizAttempt(0, currentUser, quiz, score, maxScore,
                state.getTimeTakenSeconds(), LocalDateTime.now());
        attemptDAO.save(attempt);

        response.sendRedirect(request.getContextPath() + "/quiz/results?attemptId=" + attempt.getId());
    }

    private Integer parseId(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }
}
