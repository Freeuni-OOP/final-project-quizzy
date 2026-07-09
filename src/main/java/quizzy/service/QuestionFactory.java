package quizzy.service;

import quizzy.model.Quiz;
import quizzy.model.question.FillBlankQuestion;
import quizzy.model.question.MultipleChoiceQuestion;
import quizzy.model.question.PictureQuestion;
import quizzy.model.question.Question;
import quizzy.model.question.TextQuestion;

import java.util.ArrayList;
import java.util.List;

public class QuestionFactory {

    public Question createQuestion(Quiz quiz, String questionType, String prompt,
                                   int questionOrder, String correctAnswer,
                                   List<String> choices, String imageUrl) {
        List<String> correctAnswers = splitCorrectAnswers(correctAnswer);

        if ("TEXT".equals(questionType)) {
            return new TextQuestion(0, quiz, prompt, questionOrder, correctAnswers);
        }

        if ("FILL_BLANK".equals(questionType)) {
            return new FillBlankQuestion(0, quiz, prompt, questionOrder, correctAnswers);
        }

        if ("PICTURE_RESPONSE".equals(questionType)) {
            return new PictureQuestion(0, quiz, prompt, imageUrl, questionOrder, correctAnswers);
        }

        if ("MULTIPLE_CHOICE".equals(questionType)) {
            String correctChoice = "";

            if (!correctAnswers.isEmpty()) {
                correctChoice = correctAnswers.get(0);
            }

            return new MultipleChoiceQuestion(0, quiz, prompt, questionOrder, choices, correctChoice);
        }

        throw new IllegalArgumentException("Unknown question type");
    }

    public Question createQuestion(Quiz quiz, String questionType, String prompt,
                                   int questionOrder, String correctAnswer,
                                   String choiceA, String choiceB, String choiceC,
                                   String choiceD, String imageUrl) {
        List<String> choices = new ArrayList<>();

        addChoice(choices, choiceA);
        addChoice(choices, choiceB);
        addChoice(choices, choiceC);
        addChoice(choices, choiceD);

        return createQuestion(
                quiz,
                questionType,
                prompt,
                questionOrder,
                correctAnswer,
                choices,
                imageUrl
        );
    }

    private List<String> splitCorrectAnswers(String correctAnswer) {
        List<String> correctAnswers = new ArrayList<>();

        if (correctAnswer == null) {
            return correctAnswers;
        }

        String[] parts = correctAnswer.split("\\|");

        for (String part : parts) {
            String answer = part.trim();

            if (!answer.isEmpty()) {
                correctAnswers.add(answer);
            }
        }

        return correctAnswers;
    }

    private void addChoice(List<String> choices, String choice) {
        if (choice == null) {
            return;
        }

        String trimmedChoice = choice.trim();

        if (!trimmedChoice.isEmpty()) {
            choices.add(trimmedChoice);
        }
    }
}
