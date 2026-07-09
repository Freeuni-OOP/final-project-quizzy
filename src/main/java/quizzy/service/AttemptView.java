package quizzy.service;

import java.time.LocalDateTime;

public class AttemptView {
    private String username;
    private int score;
    private int maxScore;
    private long timeTakenSeconds;
    private LocalDateTime completedAt;

    public AttemptView(String username, int score, int maxScore,
                       long timeTakenSeconds, LocalDateTime completedAt) {
        this.username = username;
        this.score = score;
        this.maxScore = maxScore;
        this.timeTakenSeconds = timeTakenSeconds;
        this.completedAt = completedAt;
    }

    public String getUsername() {
        return username;
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

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}
