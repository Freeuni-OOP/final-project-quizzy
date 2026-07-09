package quizzy.model.question;

import quizzy.model.Quiz;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("TEXT")
public class TextQuestion extends Question {

    protected TextQuestion() {
    }

    public TextQuestion(int id, Quiz quiz, String prompt, int questionOrder, List<String> correctAnswers) {
        super(id, quiz, prompt, questionOrder);

        for (String answer : correctAnswers) {
            addCorrectAnswer(answer);
        }
    }

    @Override
    public QuestionType getType() {
        return QuestionType.TEXT;
    }

    @Override
    public int getMaxScore() {
        return 1;
    }

    @Override
    public int grade(String answer) {
        ArrayList<String> correctAnswers = getCorrectAnswers();

        for (String correctAnswer : correctAnswers) {
            if (normalize(answer).equals(normalize(correctAnswer))) {
                return 1;
            }
        }

        return 0;
    }
}
