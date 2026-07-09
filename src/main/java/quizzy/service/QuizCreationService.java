package quizzy.service;

import quizzy.dao.QuestionDAO;
import quizzy.dao.QuizDAO;
import quizzy.model.Quiz;
import quizzy.model.User;
import quizzy.model.question.Question;

public class QuizCreationService {
    private QuizDAO quizDAO;
    private QuestionDAO questionDAO;
    private QuestionFactory questionFactory;

    public QuizCreationService() {
        this.quizDAO = new QuizDAO();
        this.questionDAO = new QuestionDAO();
        this.questionFactory = new QuestionFactory();
    }

    public Quiz createQuiz(User creator,
                           String title,
                           String description,
                           boolean randomQuestions,
                           boolean onePage,
                           boolean immediateCorrection,
                           boolean practiceMode,
                           String[] questionPrompts,
                           String[] questionTypes,
                           String[] correctAnswers,
                           String[] choiceA,
                           String[] choiceB,
                           String[] choiceC,
                           String[] choiceD,
                           String[] imageUrls) {
        Quiz quiz = new Quiz(
                0,
                creator,
                title,
                description,
                randomQuestions,
                onePage,
                immediateCorrection,
                practiceMode
        );

        quizDAO.save(quiz);

        if (questionPrompts == null) {
            return quiz;
        }

        for (int i = 0; i < questionPrompts.length; i++) {
            String prompt = questionPrompts[i];

            if (isEmpty(prompt)) {
                continue;
            }

            Question question = questionFactory.createQuestion(
                    quiz,
                    getValue(questionTypes, i),
                    prompt,
                    i + 1,
                    getValue(correctAnswers, i),
                    getValue(choiceA, i),
                    getValue(choiceB, i),
                    getValue(choiceC, i),
                    getValue(choiceD, i),
                    getValue(imageUrls, i)
            );

            questionDAO.save(question);
        }

        return quiz;
    }

    private String getValue(String[] values, int index) {
        if (values == null) {
            return "";
        }

        if (index >= values.length) {
            return "";
        }

        if (values[index] == null) {
            return "";
        }

        return values[index];
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
