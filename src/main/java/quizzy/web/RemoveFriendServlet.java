package quizzy.web;

import quizzy.model.User;
import quizzy.service.FriendService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/friends/remove")
public class RemoveFriendServlet extends HttpServlet {

    private final FriendService friendService = new FriendService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User me = SessionUtil.current(request);
        int otherId = parseId(request.getParameter("friendId"));

        String message;
        if (otherId <= 0) {
            message = "Invalid user.";
        } else {
            message = friendService.removeFriend(me.getId(), otherId)
                    ? "Friend removed."
                    : "You weren't friends with that user.";
        }
        SessionUtil.setFlash(request, message);
        response.sendRedirect(request.getContextPath() + "/friends");
    }

    private int parseId(String raw) {
        try {
            return raw == null ? -1 : Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
