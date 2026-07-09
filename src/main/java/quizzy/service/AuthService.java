package quizzy.service;

import quizzy.dao.UserDAO;
import quizzy.model.User;
import quizzy.util.PasswordUtil;

public class AuthService {

    private static final int USERNAME_MIN = 3;
    private static final int USERNAME_MAX = 50;   // users.username is VARCHAR(50)
    private static final int PASSWORD_MIN = 6;

    private final UserDAO userDAO;

    public AuthService() {
        this(new UserDAO());
    }

    // lets tests pass in a fake DAO
    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User register(String username, String password, String confirmPassword) throws AuthException {
        username = username == null ? "" : username.trim();

        if (username.length() < USERNAME_MIN || username.length() > USERNAME_MAX) {
            throw new AuthException("Username must be between " + USERNAME_MIN + " and " + USERNAME_MAX + " characters.");
        }
        if (!username.matches("[A-Za-z0-9_.-]+")) {
            throw new AuthException("Username may only contain letters, digits, and _ . -");
        }
        if (password == null || password.length() < PASSWORD_MIN) {
            throw new AuthException("Password must be at least " + PASSWORD_MIN + " characters.");
        }
        if (!password.equals(confirmPassword)) {
            throw new AuthException("Passwords do not match.");
        }
        if (userDAO.usernameExists(username)) {
            throw new AuthException("That username is already taken.");
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hash(salt, password);
        return userDAO.createUser(username, hash, salt);
    }

    // returns the user on success, null otherwise. don't tell the caller which of the two was
    // wrong so we don't reveal whether a username exists.
    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        User user = userDAO.findByUsername(username.trim());
        if (user == null) {
            return null;
        }
        if (PasswordUtil.verify(password, user.getSalt(), user.getPasswordHash())) {
            return user;
        }
        return null;
    }
}
