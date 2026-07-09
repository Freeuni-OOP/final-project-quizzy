package quizzy.web;

import quizzy.model.Message;
import quizzy.model.User;
import quizzy.service.MessageService;
import quizzy.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/inbox")
public class InboxServlet extends HttpServlet {

    private final MessageService messageService = new MessageService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User me = SessionUtil.current(request);

        List<Message> messages = messageService.getInbox(me.getId());
        List<InboxItem> items = new ArrayList<>(messages.size());
        Map<Integer, String> nameCache = new HashMap<>();   // don't look up the same sender twice
        for (Message message : messages) {
            String senderName = nameCache.computeIfAbsent(message.getSenderId(), userService::usernameOf);
            items.add(new InboxItem(message, senderName));
        }

        request.setAttribute("items", items);
        request.getRequestDispatcher("/WEB-INF/jsp/inbox.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User me = SessionUtil.current(request);
        int messageId = parseId(request.getParameter("messageId"));

        String message;
        if (messageId <= 0) {
            message = "Invalid message.";
        } else {
            message = messageService.delete(messageId, me.getId())
                    ? "Message deleted."
                    : "That message is no longer in your inbox.";
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
