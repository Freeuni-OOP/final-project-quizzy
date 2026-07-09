package quizzy.service;

import quizzy.dao.UserDAO;
import quizzy.model.User;

// read-side user lookups for the web layer (profile by id, message sender names, ...)
public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this(new UserDAO());
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User findById(int id) {
        return userDAO.findById(id);
    }

    public String usernameOf(int id) {
        User user = userDAO.findById(id);
        return user == null ? "(unknown user)" : user.getUsername();
    }
}
