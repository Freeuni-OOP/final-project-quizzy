package quizzy.web;

import quizzy.dao.QuizDAO;
import quizzy.model.Quiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Browse all quizzes — {@code GET /quizzes}.
 * Lists every quiz in the system, newest first.
 */
@WebServlet("/quizzes")
public class QuizBrowseServlet extends HttpServlet {

    private final QuizDAO quizDAO = new QuizDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Quiz> quizzes = quizDAO.findAll();
        request.setAttribute("quizzes", quizzes);

        request.getRequestDispatcher("/WEB-INF/jsp/quizzes.jsp")
                .forward(request, response);
    }
}
