package quizzy.admin;

import quizzy.model.User;
import quizzy.util.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


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
