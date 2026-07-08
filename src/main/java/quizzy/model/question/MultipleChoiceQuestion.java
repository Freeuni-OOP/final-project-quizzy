package quizzy.model.question;

import quizzy.model.Quiz;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoiceQuestion extends Question {

    protected MultipleChoiceQuestion() {
    }

    public MultipleChoiceQuestion(int id, Quiz quiz, String prompt, int questionOrder,
                                  List<String> choices, String correctAnswer) {
        super(id, quiz, prompt, questionOrder);

        for (String choice : choices) {
            addAnswer(new Answer(0, choice, normalize(choice).equals(normalize(correctAnswer))));
        }
    }

    public ArrayList<String> getChoices() {
        ArrayList<String> choices = new ArrayList<>();

        for (Answer answer : getAnswers()) {
            choices.add(answer.getAnswerText());
        }

        return choices;
    }

    public String getCorrectAnswer() {
        for (Answer answer : getAnswers()) {
            if (answer.isCorrect()) {
                return answer.getAnswerText();
            }
        }

        return "";
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
        if (normalize(answer).equals(normalize(getCorrectAnswer()))) {
            return 1;
        }

        return 0;
    }
}
