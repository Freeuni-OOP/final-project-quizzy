package quizzy.service;

import quizzy.model.Quiz;
import quizzy.model.QuizAttempt;
import quizzy.model.User;

import java.util.Map;

public class QuizResult {
    private Quiz quiz;
    private User user;
    private int score;
    private int maxScore;
    private long timeTakenSeconds;
    private boolean practiceMode;
    private QuizAttempt attempt;
    private Map<Integer, String> userAnswers;
    private Map<Integer, Integer> questionScores;

    public QuizResult(Quiz quiz, User user, int score, int maxScore,
                      long timeTakenSeconds, boolean practiceMode,
                      QuizAttempt attempt,
                      Map<Integer, String> userAnswers,
                      Map<Integer, Integer> questionScores) {
        this.quiz = quiz;
        this.user = user;
        this.score = score;
        this.maxScore = maxScore;
        this.timeTakenSeconds = timeTakenSeconds;
        this.practiceMode = practiceMode;
        this.attempt = attempt;
        this.userAnswers = userAnswers;
        this.questionScores = questionScores;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public User getUser() {
        return user;
    }

    public int getScore() {
        return score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public long getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public boolean isPracticeMode() {
        return practiceMode;
    }

    public QuizAttempt getAttempt() {
        return attempt;
    }

    public Map<Integer, String> getUserAnswers() {
        return userAnswers;
    }

    public Map<Integer, Integer> getQuestionScores() {
        return questionScores;
    }
}
