package quizzy.web;

import quizzy.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

// one place for reading/writing the logged-in user (and flash messages) in the session
public final class SessionUtil {

    public static final String CURRENT_USER = "currentUser";
    private static final String FLASH = "flash";

    private SessionUtil() {
    }

    public static void login(HttpServletRequest request, User user) {
        // throw away any old session and start a fresh one so the session id changes on login
        HttpSession old = request.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(CURRENT_USER, user);
    }

    public static User current(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute(CURRENT_USER);
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return current(request) != null;
    }

    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    // one-shot message shown once after a redirect (Post/Redirect/Get)
    public static void setFlash(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute(FLASH, message);
    }

    public static String consumeFlash(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object message = session.getAttribute(FLASH);
        if (message != null) {
            session.removeAttribute(FLASH);
        }
        return (String) message;
    }
}
