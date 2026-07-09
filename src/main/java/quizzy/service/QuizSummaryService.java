package quizzy.service;

import quizzy.dao.QuizAttemptDAO;
import quizzy.dao.QuizDAO;
import quizzy.model.Quiz;

import java.time.LocalDateTime;
import java.util.List;

public class QuizSummaryService {
    private static final int TOP_ATTEMPTS_LIMIT = 10;

    private QuizDAO quizDAO;
    private QuizAttemptDAO attemptDAO;

    public QuizSummaryService() {
        this.quizDAO = new QuizDAO();
        this.attemptDAO = new QuizAttemptDAO();
    }

    public Quiz getQuiz(int quizId) {
        return quizDAO.findById(quizId);
    }

    public List<AttemptView> getTopAttempts(int quizId) {
        return attemptDAO.findTopAttemptViewsByQuiz(quizId, TOP_ATTEMPTS_LIMIT);
    }

    public List<AttemptView> getRecentTopAttempts(int quizId) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return attemptDAO.findRecentTopAttemptViewsByQuiz(quizId, since, TOP_ATTEMPTS_LIMIT);
    }

    public List<AttemptView> getUserAttempts(int quizId, int userId) {
        return attemptDAO.findUserAttemptViewsForQuiz(quizId, userId);
    }

    public Double getAverageScore(int quizId) {
        Double averageScore = attemptDAO.findAverageScoreByQuiz(quizId);

        if (averageScore == null) {
            return 0.0;
        }

        return averageScore;
    }
}
