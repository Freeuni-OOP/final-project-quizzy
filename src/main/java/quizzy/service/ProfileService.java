package quizzy.service;

import quizzy.dao.ProfileDAO;
import quizzy.model.Achievement;
import quizzy.model.AttemptSummary;

import java.util.List;

public class ProfileService {

    private final ProfileDAO profileDAO;

    public ProfileService() {
        this(new ProfileDAO());
    }

    public ProfileService(ProfileDAO profileDAO) {
        this.profileDAO = profileDAO;
    }

    public List<AttemptSummary> getQuizHistory(int userId) {
        return profileDAO.getAttempts(userId);
    }

    public List<Achievement> getAchievements(int userId) {
        return profileDAO.getAchievements(userId);
    }
}
