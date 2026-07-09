package quizzy.web;

import quizzy.model.Friendship;
import quizzy.model.FriendshipStatus;
import quizzy.model.User;
import quizzy.service.FriendService;
import quizzy.service.ProfileService;
import quizzy.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// public profile: username, quiz history, achievements, and the right add-friend button.
// no ?id -> shows your own profile.
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final FriendService friendService = new FriendService();
    private final ProfileService profileService = new ProfileService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User me = SessionUtil.current(request);

        int profileId = parseId(request.getParameter("id"), me.getId());
        User profileUser = userService.findById(profileId);
        if (profileUser == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No such user.");
            return;
        }

        request.setAttribute("profileUser", profileUser);
        request.setAttribute("attempts", profileService.getQuizHistory(profileId));
        request.setAttribute("achievements", profileService.getAchievements(profileId));
        request.setAttribute("friendState", friendState(me.getId(), profileUser.getId()));

        request.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(request, response);
    }

    // SELF / FRIENDS / PENDING_OUT (I asked them) / PENDING_IN (they asked me) / NONE
    private String friendState(int viewerId, int profileId) {
        if (viewerId == profileId) {
            return "SELF";
        }
        Friendship rel = friendService.relationship(viewerId, profileId);
        if (rel == null || rel.getStatus() == FriendshipStatus.DECLINED) {
            return "NONE";
        }
        if (rel.getStatus() == FriendshipStatus.ACCEPTED) {
            return "FRIENDS";
        }
        return rel.getRequesterId() == viewerId ? "PENDING_OUT" : "PENDING_IN";
    }

    private int parseId(String raw, int fallback) {
        try {
            return raw == null ? fallback : Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
