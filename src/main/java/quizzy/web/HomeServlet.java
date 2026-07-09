package quizzy.web;

import quizzy.dao.QuizAttemptDAO;
import quizzy.dao.QuizDAO;
import quizzy.model.Announcement;
import quizzy.model.Quiz;
import quizzy.model.QuizAttempt;
import quizzy.model.User;
import quizzy.service.AnnouncementService;
import quizzy.service.FriendService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Still placeholder, waiting on one more file each:
 *   - popularQuizzes      -- needs a "most popular" ranking method; QuizDAO
 *                            only has findAll()/findByCreator() as far as we've
 *                            confirmed. Using findAll() as a stand-in for now.
 *   - unreadMessageCount  -- needs Message.java's field names (e.g. isRead) to
 *                            avoid guessing blindly at something with real
 *                            compile risk. Left at 0 until confirmed.
 */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private static final int LIST_LIMIT = 5;

    private final QuizDAO quizDAO = new QuizDAO();
    private final QuizAttemptDAO attemptDAO = new QuizAttemptDAO();
    private final FriendService friendService = new FriendService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = SessionUtil.current(request);

        List<Announcement> announcements = AnnouncementService.getRecentAnnouncements(5);
        request.setAttribute("announcements", announcements);

        List<Quiz> allQuizzes = quizDAO.findAll();

        // "Recently created" -- Quiz has no createdAt field as far as we've
        // seen, so id descending is our proxy for recency (higher id = made later).
        List<Quiz> recentQuizzes = new ArrayList<>(allQuizzes);
        recentQuizzes.sort(Comparator.comparingInt(Quiz::getId).reversed());
        request.setAttribute("recentQuizzes", firstN(recentQuizzes, LIST_LIMIT));

        // TODO: "popular" should really be ranked by attempt count / rating.
        // Standing in with the same list until QuizDAO has a real ranking
        // method (or we add one once we've seen the current file).
        request.setAttribute("popularQuizzes", firstN(allQuizzes, LIST_LIMIT));

        if (currentUser != null) {
            request.setAttribute("myCreatedQuizzes", quizDAO.findByCreator(currentUser.getId()));

            List<QuizAttempt> myAttempts = attemptDAO.findByUser(currentUser.getId());
            request.setAttribute("myRecentAttempts", firstN(myAttempts, LIST_LIMIT));

            // ASSUMPTION: FriendService.getFriends(userId) returns List<User>
            // (inferred from how FriendsServlet passes it straight to friends.jsp).
            // For each friend, show their single most recent attempt, if any.
            // N+1 query pattern -- fine at this project's scale, not something
            // to worry about optimizing for a class assignment.
            List<Object> friendActivity = new ArrayList<>();
            for (User friend : friendService.getFriends(currentUser.getId())) {
                List<QuizAttempt> friendAttempts = attemptDAO.findByUser(friend.getId());
                if (!friendAttempts.isEmpty()) {
                    friendActivity.add(friendAttempts.get(0));
                }
            }
            request.setAttribute("friendActivity", firstN(friendActivity, LIST_LIMIT));
        } else {
            request.setAttribute("myCreatedQuizzes", Collections.emptyList());
            request.setAttribute("myRecentAttempts", Collections.emptyList());
            request.setAttribute("friendActivity", Collections.emptyList());
        }

        // TODO: needs Message.java's field names before we can count unread safely
        request.setAttribute("unreadMessageCount", 0);

        request.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(request, response);
    }

    private <T> List<T> firstN(List<T> list, int n) {
        return list.size() <= n ? list : list.subList(0, n);
    }
}