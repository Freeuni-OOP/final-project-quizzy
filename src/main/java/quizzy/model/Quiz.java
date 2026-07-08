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
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "random_questions", nullable = false)
    private boolean randomQuestions;

    @Column(name = "one_page", nullable = false)
    private boolean onePage;

    @Column(name = "immediate_correction", nullable = false)
    private boolean immediateCorrection;

    @Column(name = "practice_mode", nullable = false)
    private boolean practiceMode;

    protected Quiz() {
    }

    public Quiz(int id, User creator, String title, String description,
                boolean randomQuestions, boolean onePage,
                boolean immediateCorrection, boolean practiceMode) {
        this.id = id;
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.randomQuestions = randomQuestions;
        this.onePage = onePage;
        this.immediateCorrection = immediateCorrection;
        this.practiceMode = practiceMode;
    }

    public int getId() {
        return id;
    }

    public User getCreator() {
        return creator;
    }

    public int getCreatorId() {
        return creator.getId();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRandomQuestions() {
        return randomQuestions;
    }

    public boolean isOnePage() {
        return onePage;
    }

    public boolean isImmediateCorrection() {
        return immediateCorrection;
    }

    public boolean isPracticeMode() {
        return practiceMode;
    }
}