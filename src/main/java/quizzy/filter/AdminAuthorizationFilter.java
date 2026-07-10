package quizzy.filter;

import quizzy.util.SessionUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AdminAuthorizationFilter implements Filter {

    private static final String LOGIN_PAGE = "/login";
    private static final String ERROR_ATTR = "loginError";

    @Override
    public void init(FilterConfig filterConfig) {
        /* nothing to initialise */
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        if (session == null || !SessionUtils.isAdmin(session)) {
            /* Not an admin — redirect to login with an explanation. */
            if (session != null) {
                session.setAttribute(ERROR_ATTR,
                        "You must be logged in as an administrator to access that page.");
            } else {
                httpRequest.getSession().setAttribute(ERROR_ATTR,
                        "You must be logged in as an administrator to access that page.");
            }
            httpResponse.sendRedirect(httpRequest.getContextPath() + LOGIN_PAGE);
            return;
        }

        /* Admin user — allow the request to proceed. */
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        /* nothing to clean up */
    }
}
