package quizzy.model.question;

import java.util.ArrayList;

public class MultipleChoiceQuestion extends Question {
    private ArrayList<String> choices;
    private String correctAnswer;

    public MultipleChoiceQuestion(int id, int quizId, String prompt, ArrayList<String> choices, String correctAnswer) {
        super(id, quizId, prompt);
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    @Override
    public QuestionType getType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public int getMaxScore() {
        return 1;
    }

    @Override
    public int grade(String answer) {
        if (normalize(answer).equals(normalize(correctAnswer))) {
            return 1;
        }

        return 0;
    }
}