package quizzy.model.question;

import java.util.ArrayList;

public class FillBlankQuestion extends TextQuestion {

    public FillBlankQuestion(int id, int quizId, String prompt, ArrayList<String> correctAnswers) {
        super(id, quizId, prompt, correctAnswers);
    }

    @Override
    public QuestionType getType() {
        return QuestionType.FILL_BLANK;
    }
}