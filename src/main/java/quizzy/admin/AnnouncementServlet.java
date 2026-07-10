package quizzy.admin;

import quizzy.model.Announcement;
import quizzy.model.User;
import quizzy.service.AnnouncementService;
import quizzy.util.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public class AnnouncementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Announcement> announcements = AnnouncementService.getAllAnnouncements();
        request.setAttribute("announcements", announcements);
        request.setAttribute("message", request.getParameter("message"));

        request.getRequestDispatcher("/WEB-INF/admin/announcements.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        User admin = SessionUtils.getCurrentUser(request.getSession());
        int creatorId = admin != null ? admin.getId() : 0;

        String message;

        switch (action != null ? action : "") {
            case "create": {
                String title = request.getParameter("title");
                String content = request.getParameter("content");

                if (title == null || title.trim().isEmpty()
                        || content == null || content.trim().isEmpty()) {
                    message = "Title and content are required.";
                    break;
                }
                AnnouncementService.createAnnouncement(title.trim(), content.trim(), creatorId);
                message = "Announcement created.";
                break;
            }

            case "update": {
                int id = parseInt(request.getParameter("id"), -1);
                String title = request.getParameter("title");
                String content = request.getParameter("content");

                if (id <= 0 || title == null || title.trim().isEmpty()
                        || content == null || content.trim().isEmpty()) {
                    message = "Invalid announcement data.";
                    break;
                }
                Announcement updated = AnnouncementService.updateAnnouncement(
                        id, title.trim(), content.trim());
                message = updated != null ? "Announcement updated." : "Announcement not found.";
                break;
            }

            case "delete": {
                int id = parseInt(request.getParameter("id"), -1);
                if (id <= 0) {
                    message = "Invalid announcement ID.";
                    break;
                }
                boolean deleted = AnnouncementService.deleteAnnouncement(id);
                message = deleted ? "Announcement deleted." : "Announcement not found.";
                break;
            }

            default:
                message = "Unknown action.";
                break;
        }

        response.sendRedirect(request.getContextPath()
                + "/admin/announcements?message="
                + java.net.URLEncoder.encode(message, "UTF-8"));
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
