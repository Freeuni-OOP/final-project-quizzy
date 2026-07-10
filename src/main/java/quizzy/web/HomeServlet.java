package quizzy.web;

import quizzy.dao.QuizAttemptDAO;
import quizzy.dao.QuizDAO;
import quizzy.model.Announcement;
import quizzy.model.Message;
import quizzy.model.Quiz;
import quizzy.model.QuizAttempt;
import quizzy.model.User;
import quizzy.service.AnnouncementService;
import quizzy.service.FriendService;
import quizzy.service.MessageService;
import quizzy.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private static final int LIST_LIMIT = 5;
    private static final int MESSAGE_PREVIEW_LIMIT = 3;

    private final QuizDAO quizDAO = new QuizDAO();
    private final QuizAttemptDAO attemptDAO = new QuizAttemptDAO();
    private final FriendService friendService = new FriendService();
    private final MessageService messageService = new MessageService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = SessionUtil.current(request);

        List<Announcement> announcements = AnnouncementService.getRecentAnnouncements(5);
        request.setAttribute("announcements", announcements);

        List<Quiz> allQuizzes = quizDAO.findAll();

        List<Quiz> recentQuizzes = new ArrayList<>(allQuizzes);
        recentQuizzes.sort(Comparator.comparingInt(Quiz::getId).reversed());
        request.setAttribute("recentQuizzes", firstN(recentQuizzes, LIST_LIMIT));

        request.setAttribute("popularQuizzes", quizDAO.findPopular(LIST_LIMIT));

        if (currentUser != null) {
            request.setAttribute("myCreatedQuizzes", quizDAO.findByCreator(currentUser.getId()));

            List<QuizAttempt> myAttempts = attemptDAO.findByUser(currentUser.getId());
            request.setAttribute("myRecentAttempts", firstN(myAttempts, LIST_LIMIT));

            List<QuizAttempt> friendActivity = new ArrayList<>();
            for (User friend : friendService.getFriends(currentUser.getId())) {
                List<QuizAttempt> friendAttempts = attemptDAO.findByUser(friend.getId());
                if (!friendAttempts.isEmpty()) {
                    friendActivity.add(friendAttempts.get(0));
                }
            }
            request.setAttribute("friendActivity", firstN(friendActivity, LIST_LIMIT));

            List<Message> inbox = messageService.getInbox(currentUser.getId());
            request.setAttribute("unreadMessageCount", inbox.size());

            // build display-ready previews the same way InboxServlet does,
            // just capped to a handful for the homepage sidebar
            List<InboxItem> previews = new ArrayList<>();
            Map<Integer, String> nameCache = new HashMap<>();
            for (Message message : firstN(inbox, MESSAGE_PREVIEW_LIMIT)) {
                String senderName = nameCache.computeIfAbsent(message.getSenderId(), userService::usernameOf);
                previews.add(new InboxItem(message, senderName));
            }
            request.setAttribute("messagePreviews", previews);
        } else {
            request.setAttribute("myCreatedQuizzes", Collections.emptyList());
            request.setAttribute("myRecentAttempts", Collections.emptyList());
            request.setAttribute("friendActivity", Collections.emptyList());
            request.setAttribute("unreadMessageCount", 0);
            request.setAttribute("messagePreviews", Collections.emptyList());
        }

        request.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(request, response);
    }

    private <T> List<T> firstN(List<T> list, int n) {
        return list.size() <= n ? list : list.subList(0, n);
    }
}
