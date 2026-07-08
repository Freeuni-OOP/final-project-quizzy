package quizzy.model.question;

import quizzy.model.Quiz;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@Entity
@DiscriminatorValue("FILL_BLANK")
public class FillBlankQuestion extends TextQuestion {

    protected FillBlankQuestion() {
    }

    public FillBlankQuestion(int id, Quiz quiz, String prompt, int questionOrder, List<String> correctAnswers) {
        super(id, quiz, prompt, questionOrder, correctAnswers);
    }

    @Override
    public QuestionType getType() {
        return QuestionType.FILL_BLANK;
    }
}
