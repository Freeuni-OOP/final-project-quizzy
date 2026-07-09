package quizzy.web;

import quizzy.model.User;
import quizzy.service.FriendService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/friends/request")
public class SendFriendRequestServlet extends HttpServlet {

    private final FriendService friendService = new FriendService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User me = SessionUtil.current(request);
        int targetId = parseId(request.getParameter("targetId"));

        String message;
        if (targetId <= 0) {
            message = "Invalid user.";
        } else {
            message = describe(friendService.sendRequest(me.getId(), targetId));
        }
        SessionUtil.setFlash(request, message);
        response.sendRedirect(redirectTarget(request, targetId));
    }

    private String describe(FriendService.RequestOutcome outcome) {
        switch (outcome) {
            case SENT:                    return "Friend request sent.";
            case SELF:                    return "You can't send a friend request to yourself.";
            case ALREADY_FRIENDS:         return "You are already friends.";
            case REQUEST_ALREADY_PENDING: return "You already have a pending request to this user.";
            case INCOMING_REQUEST_EXISTS: return "This user already sent you a request - check your inbox.";
            case TARGET_NOT_FOUND:
            default:                      return "That user could not be found.";
        }
    }

    private String redirectTarget(HttpServletRequest request, int targetId) {
        String context = request.getContextPath();
        return targetId > 0 ? context + "/profile?id=" + targetId : context + "/search";
    }

    private int parseId(String raw) {
        try {
            return raw == null ? -1 : Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
