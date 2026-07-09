package quizzy.admin;

import quizzy.model.User;
import quizzy.util.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Admin landing page — accessible at {@code GET /admin/}.
 * Displays navigation links to all admin sub-sections.
 *
 * <p>Registered in {@code web.xml}. Protected by
 * {@link quizzy.filter.AdminAuthorizationFilter} which runs before this
 * servlet for any {@code /admin/*} URL.</p>
 */
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = SessionUtils.getCurrentUser(request.getSession());
        request.setAttribute("adminUser", currentUser);

        request.getRequestDispatcher("/WEB-INF/admin/dashboard.jsp")
                .forward(request, response);
    }
}
