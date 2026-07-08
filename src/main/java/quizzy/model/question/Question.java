package quizzy.model.question;

import quizzy.model.Quiz;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String prompt;

    @Column(name = "question_order", nullable = false)
    private int questionOrder;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    protected Question() {
    }

    public Question(int id, Quiz quiz, String prompt, int questionOrder) {
        this.id = id;
        this.quiz = quiz;
        this.prompt = prompt;
        this.questionOrder = questionOrder;
    }

    protected void addAnswer(Answer answer) {
        answer.setQuestion(this);
        answers.add(answer);
    }

    protected void addCorrectAnswer(String answerText) {
        addAnswer(new Answer(0, answerText, true));
    }

    protected void addWrongAnswer(String answerText) {
        addAnswer(new Answer(0, answerText, false));
    }

    public int getId() {
        return id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public int getQuizId() {
        return quiz.getId();
    }

    public String getPrompt() {
        return prompt;
    }

    public int getQuestionOrder() {
        return questionOrder;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public ArrayList<String> getCorrectAnswers() {
        ArrayList<String> result = new ArrayList<>();

        for (Answer answer : answers) {
            if (answer.isCorrect()) {
                result.add(answer.getAnswerText());
            }
        }

        return result;
    }

    public abstract QuestionType getType();

    public abstract int getMaxScore();

    public abstract int grade(String answer);

    public String normalize(String text) {
        if (text == null) {
            return "";
        }

        return text.trim().toLowerCase();
    }
}
