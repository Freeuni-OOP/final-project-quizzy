package quizzy.web;

import quizzy.service.FriendService;
import quizzy.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/friends")
public class FriendsServlet extends HttpServlet {

    private final FriendService friendService = new FriendService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User me = SessionUtil.current(request);
        request.setAttribute("friends", friendService.getFriends(me.getId()));
        request.getRequestDispatcher("/WEB-INF/jsp/friends.jsp").forward(request, response);
    }
}
