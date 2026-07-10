package quizzy.util;

import quizzy.model.User;

import javax.servlet.http.HttpSession;

public final class SessionUtils {

    /** Session attribute key for the logged-in {@link User}. */
    public static final String CURRENT_USER_ATTR = "currentUser";

    private SessionUtils() {
        /* utility class — prevent instantiation */
    }

    /**
     * Returns the currently logged-in user, or {@code null} if the session
     * is new or the user has not logged in.
     *
     * @param session the current HTTP session (never {@code null})
     * @return the logged-in user, or {@code null}
     */
    public static User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute(CURRENT_USER_ATTR);
    }

    /**
     * Stores a user in the session after successful authentication.
     * M2's login servlet should call this once credentials are verified.
     *
     * @param session the current HTTP session (never {@code null})
     * @param user    the authenticated user (never {@code null})
     */
    public static void setCurrentUser(HttpSession session, User user) {
        session.setAttribute(CURRENT_USER_ATTR, user);
    }

    /**
     * Returns {@code true} if a user is currently authenticated.
     *
     * @param session the current HTTP session (never {@code null})
     * @return {@code true} if {@link #getCurrentUser} would return non-null
     */
    public static boolean isLoggedIn(HttpSession session) {
        return getCurrentUser(session) != null;
    }

    /**
     * Returns {@code true} if the logged-in user has administrator privileges.
     * Safe to call on any session — returns {@code false} for guests.
     *
     * @param session the current HTTP session (never {@code null})
     * @return {@code true} if logged in AND {@link User#isAdmin()} is true
     */
    public static boolean isAdmin(HttpSession session) {
        User user = getCurrentUser(session);
        return user != null && user.isAdmin();
    }
}
