package quizzy.model.question;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "answer_text", nullable = false, columnDefinition = "TEXT")
    private String answerText;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    protected Answer() {
    }

    public Answer(int id, String answerText, boolean correct) {
        this.id = id;
        this.answerText = answerText;
        this.correct = correct;
    }

    void setQuestion(Question question) {
        this.question = question;
    }

    public int getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }

    public String getAnswerText() {
        return answerText;
    }

    public boolean isCorrect() {
        return correct;
    }
}
