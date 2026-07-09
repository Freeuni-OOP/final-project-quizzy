package quizzy.web;

import quizzy.model.Quiz;
import quizzy.model.User;
import quizzy.service.AchievementService;
import quizzy.service.QuizCreationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Create a new quiz — {@code GET /quiz/create} shows the form,
 * {@code POST /quiz/create} persists the quiz and its questions.
 *
 * <p>Uses {@link QuizCreationService} for persistence and calls
 * {@link AchievementService#checkAuthorAchievements(int)} after
 * successful creation.</p>
 */
@WebServlet("/quiz/create")
public class CreateQuizServlet extends HttpServlet {

    private final QuizCreationService creationService = new QuizCreationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User me = SessionUtil.current(request);
        if (me == null) {
            response.sendRedirect(request.getContextPath() + "/login?next="
                    + java.net.URLEncoder.encode("/quiz/create", "UTF-8"));
            return;
        }

        request.setAttribute("error", request.getParameter("error"));
        request.getRequestDispatcher("/WEB-INF/jsp/quiz-create.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User me = SessionUtil.current(request);
        if (me == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        /* ---- parse quiz metadata ---- */
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        boolean randomQuestions = "on".equals(request.getParameter("randomQuestions"));
        boolean onePage = "on".equals(request.getParameter("onePage"));
        boolean immediateCorrection = "on".equals(request.getParameter("immediateCorrection"));
        boolean practiceMode = "on".equals(request.getParameter("practiceMode"));

        if (title == null || title.trim().isEmpty()) {
            request.setAttribute("error", "Title is required.");
            doGet(request, response);
            return;
        }

        /* ---- collect question arrays from the form ---- */
        String[] prompts = request.getParameterValues("questionPrompt");
        String[] types = request.getParameterValues("questionType");
        String[] answers = request.getParameterValues("correctAnswer");

        /* Pad choice arrays to the same length as prompts (some question
           types don't have choices, so indices may be sparse). */
        int n = prompts != null ? prompts.length : 0;
        String[] choiceA = padArray(request.getParameterValues("choiceA"), n);
        String[] choiceB = padArray(request.getParameterValues("choiceB"), n);
        String[] choiceC = padArray(request.getParameterValues("choiceC"), n);
        String[] choiceD = padArray(request.getParameterValues("choiceD"), n);
        String[] imageUrls = padArray(request.getParameterValues("imageUrl"), n);

        try {
            Quiz quiz = creationService.createQuiz(
                    me, title.trim(), description != null ? description.trim() : "",
                    randomQuestions, onePage, immediateCorrection, practiceMode,
                    prompts, types, answers,
                    choiceA, choiceB, choiceC, choiceD, imageUrls);

            /* Award author achievements after quiz creation. */
            AchievementService.checkAuthorAchievements(me.getId());

            response.sendRedirect(request.getContextPath()
                    + "/quiz/summary?id=" + quiz.getId());
        } catch (RuntimeException e) {
            request.setAttribute("error", "Failed to create quiz: " + e.getMessage());
            doGet(request, response);
        }
    }

    /**
     * Pads or truncates an array to exactly the given length.
     */
    private static String[] padArray(String[] values, int targetLength) {
        if (values == null) {
            return new String[targetLength];
        }
        if (values.length >= targetLength) {
            return values;
        }
        String[] padded = new String[targetLength];
        System.arraycopy(values, 0, padded, 0, values.length);
        return padded;
    }
}
