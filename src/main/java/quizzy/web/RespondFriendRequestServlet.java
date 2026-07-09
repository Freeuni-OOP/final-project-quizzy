package quizzy.web;

import quizzy.model.User;
import quizzy.service.FriendService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// second step of the friend flow - accept/decline from the inbox
@WebServlet("/friends/respond")
public class RespondFriendRequestServlet extends HttpServlet {

    private final FriendService friendService = new FriendService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User me = SessionUtil.current(request);
        int requesterId = parseId(request.getParameter("requesterId"));
        String action = request.getParameter("action");

        String message;
        if (requesterId <= 0 || action == null) {
            message = "Invalid request.";
        } else if ("accept".equals(action)) {
            message = friendService.acceptRequest(me.getId(), requesterId)
                    ? "Friend request accepted."
                    : "That request is no longer pending.";
        } else if ("decline".equals(action)) {
            message = friendService.declineRequest(me.getId(), requesterId)
                    ? "Friend request declined."
                    : "That request is no longer pending.";
        } else {
            message = "Unknown action.";
        }

        SessionUtil.setFlash(request, message);
        response.sendRedirect(request.getContextPath() + "/inbox");
    }

    private int parseId(String raw) {
        try {
            return raw == null ? -1 : Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
