package quizzy.web;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// blocks the logged-in-only pages and bounces anonymous users to /login
@WebFilter(urlPatterns = {"/home", "/profile", "/search", "/friends", "/friends/*", "/inbox"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (SessionUtil.isLoggedIn(request)) {
            chain.doFilter(req, res);
            return;
        }

        // remember where they were going so we can send them back after login
        String target = request.getRequestURI().substring(request.getContextPath().length());
        if (request.getQueryString() != null) {
            target += "?" + request.getQueryString();
        }
        String next = response.encodeRedirectURL(target);
        response.sendRedirect(request.getContextPath() + "/login?next="
                + java.net.URLEncoder.encode(next, "UTF-8"));
    }
}
