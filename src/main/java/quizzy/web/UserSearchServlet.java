package quizzy.web;

import quizzy.model.User;
import quizzy.service.FriendService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/search")
public class UserSearchServlet extends HttpServlet {

    private final FriendService friendService = new FriendService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User me = SessionUtil.current(request);
        String query = request.getParameter("q");

        List<User> results = friendService.search(query, me.getId());
        request.setAttribute("query", query == null ? "" : query);
        request.setAttribute("results", results);
        request.getRequestDispatcher("/WEB-INF/jsp/search.jsp").forward(request, response);
    }
}
