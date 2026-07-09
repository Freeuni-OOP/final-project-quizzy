package quizzy.service;

import org.junit.Before;
import org.junit.Test;
import quizzy.dao.UserDAO;
import quizzy.model.User;
import quizzy.util.PasswordUtil;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AuthServiceTest {

    private InMemoryUserDAO userDAO;
    private AuthService authService;

    @Before
    public void setUp() {
        userDAO = new InMemoryUserDAO();
        authService = new AuthService(userDAO);
    }

    @Test
    public void registerCreatesUserWithSaltedHashAndNoPlaintext() throws AuthException {
        User user = authService.register("alice", "s3cret!", "s3cret!");

        assertNotNull(user);
        assertEquals("alice", user.getUsername());
        assertFalse(user.isAdmin());
        assertFalse("stored credential must not be the plaintext", "s3cret!".equals(user.getPasswordHash()));
        assertTrue(PasswordUtil.verify("s3cret!", user.getSalt(), user.getPasswordHash()));
    }

    @Test
    public void registerTrimsUsername() throws AuthException {
        User user = authService.register("  bob  ", "password", "password");
        assertEquals("bob", user.getUsername());
        assertNotNull(userDAO.findByUsername("bob"));
    }

    @Test
    public void registerRejectsDuplicateUsername() throws AuthException {
        authService.register("carol", "password", "password");
        assertRegistrationRejected("carol", "password", "password");
    }

    @Test
    public void registerRejectsShortUsername() {
        assertRegistrationRejected("ab", "password", "password");
    }

    @Test
    public void registerRejectsIllegalUsernameCharacters() {
        assertRegistrationRejected("bad name!", "password", "password");
    }

    @Test
    public void registerRejectsShortPassword() {
        assertRegistrationRejected("dave", "123", "123");
    }

    @Test
    public void registerRejectsMismatchedConfirmation() {
        assertRegistrationRejected("erin", "password", "different");
    }

    @Test
    public void loginSucceedsWithCorrectCredentials() throws AuthException {
        authService.register("frank", "letmein1", "letmein1");
        User user = authService.login("frank", "letmein1");
        assertNotNull(user);
        assertEquals("frank", user.getUsername());
    }

    @Test
    public void loginTrimsUsername() throws AuthException {
        authService.register("grace", "letmein1", "letmein1");
        assertNotNull(authService.login("  grace  ", "letmein1"));
    }

    @Test
    public void loginFailsWithWrongPassword() throws AuthException {
        authService.register("heidi", "letmein1", "letmein1");
        assertNull(authService.login("heidi", "wrongpass"));
    }

    @Test
    public void loginFailsForUnknownUser() {
        assertNull(authService.login("nobody", "whatever"));
    }

    @Test
    public void loginIsNullSafe() {
        assertNull(authService.login(null, "x"));
        assertNull(authService.login("x", null));
    }

    private void assertRegistrationRejected(String username, String password, String confirm) {
        try {
            authService.register(username, password, confirm);
            fail("Expected AuthException for username=" + username);
        } catch (AuthException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    // in-memory stand-in for UserDAO so the test doesn't need a database
    private static class InMemoryUserDAO extends UserDAO {
        private final Map<String, User> byUsername = new HashMap<>();
        private int nextId = 1;

        @Override
        public User createUser(String username, String passwordHash, String salt) {
            User user = new User(nextId++, username, passwordHash, salt, false);
            byUsername.put(username, user);
            return user;
        }

        @Override
        public User findByUsername(String username) {
            return byUsername.get(username);
        }

        @Override
        public boolean usernameExists(String username) {
            return byUsername.containsKey(username);
        }
    }
}
