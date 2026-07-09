package quizzy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private int score;

    @Column(name = "max_score", nullable = false)
    private int maxScore;

    @Column(name = "time_taken_seconds", nullable = false)
    private long timeTakenSeconds;

    protected QuizAttempt() {
    }

    public QuizAttempt(int id, User user, Quiz quiz, int score, int maxScore, long timeTakenSeconds) {
        this.id = id;
        this.user = user;
        this.quiz = quiz;
        this.score = score;
        this.maxScore = maxScore;
        this.timeTakenSeconds = timeTakenSeconds;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public int getUserId() {
        return user.getId();
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public int getQuizId() {
        return quiz.getId();
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