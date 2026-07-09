package quizzy.model;

// one row of a user's quiz history (attempt + the quiz title), for the profile page
public class AttemptSummary {
    private int quizId;
    private String quizTitle;
    private int score;
    private int maxScore;
    private long timeTakenSeconds;

    public AttemptSummary(int quizId, String quizTitle, int score, int maxScore, long timeTakenSeconds) {
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.score = score;
        this.maxScore = maxScore;
        this.timeTakenSeconds = timeTakenSeconds;
    }

    public int getQuizId() {
        return quizId;
    }

    public String getQuizTitle() {
        return quizTitle;
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
