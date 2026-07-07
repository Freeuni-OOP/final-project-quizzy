package quizzy.model;

public class QuizAttempt {
    private int id;
    private int userId;
    private int quizId;
    private int score;
    private int maxScore;
    private long timeTakenSeconds;

    public QuizAttempt(int id, int userId, int quizId, int score, int maxScore, long timeTakenSeconds) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.maxScore = maxScore;
        this.timeTakenSeconds = timeTakenSeconds;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getQuizId() {
        return quizId;
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
}