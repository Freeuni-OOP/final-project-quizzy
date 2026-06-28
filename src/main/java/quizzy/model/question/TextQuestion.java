package quizzy.model.question;

import java.util.ArrayList;

public class TextQuestion extends Question {
    private ArrayList<String> correctAnswers;

    public TextQuestion(int id, int quizId, String prompt, ArrayList<String> correctAnswers) {
        super(id, quizId, prompt);
        this.correctAnswers = correctAnswers;
    }

    public ArrayList<String> getCorrectAnswers() {
        return correctAnswers;
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
        for (String correctAnswer : correctAnswers) {
            if (normalize(answer).equals(normalize(correctAnswer))) {
                return 1;
            }
        }
        return 0;
    }
}