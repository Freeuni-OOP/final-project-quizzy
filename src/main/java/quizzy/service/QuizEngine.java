package quizzy.service;

import quizzy.dao.QuestionDAO;
import quizzy.dao.QuizAttemptDAO;
import quizzy.dao.QuizDAO;
import quizzy.model.Quiz;
import quizzy.model.QuizAttempt;
import quizzy.model.User;
import quizzy.model.question.Question;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizEngine {
    private QuizDAO quizDAO;
    private QuestionDAO questionDAO;
    private QuizAttemptDAO attemptDAO;

    public QuizEngine() {
        this.quizDAO = new QuizDAO();
        this.questionDAO = new QuestionDAO();
        this.attemptDAO = new QuizAttemptDAO();
    }

    public QuizSession startQuiz(int quizId, boolean practiceMode) {
        Quiz quiz = quizDAO.findById(quizId);

        if (quiz == null) {
            throw new IllegalArgumentException("Quiz not found");
        }

        List<Question> questions = questionDAO.findByQuiz(quizId);

        if (quiz.isRandomQuestions()) {
            Collections.shuffle(questions);
        }

        return new QuizSession(quiz, questions, practiceMode);
    }

    public void recordAnswer(QuizSession session, int questionId, String answer) {
        session.recordAnswer(questionId, answer);
    }

    public int gradeQuestion(Question question, String answer) {
        return question.grade(answer);
    }

    public QuizResult finishQuiz(QuizSession session, User user) {
        int score = 0;
        int maxScore = 0;
        Map<Integer, Integer> questionScores = new HashMap<>();

        for (Question question : session.getQuestions()) {
            String userAnswer = session.getAnswer(question.getId());
            int questionScore = question.grade(userAnswer);

            score += questionScore;
            maxScore += question.getMaxScore();
            questionScores.put(question.getId(), questionScore);
        }

        long timeTakenSeconds = session.getElapsedSeconds();
        QuizAttempt attempt = null;

        if (!session.isPracticeMode()) {
            attempt = new QuizAttempt(0, user, session.getQuiz(), score, maxScore, timeTakenSeconds);
            attemptDAO.save(attempt);
        }

        return new QuizResult(
                session.getQuiz(),
                user,
                score,
                maxScore,
                timeTakenSeconds,
                session.isPracticeMode(),
                attempt,
                session.getAnswers(),
                questionScores
        );
    }

    public QuizResult finishQuiz(Quiz quiz, User user, List<Question> questions,
                                 Map<Integer, String> answers,
                                 long timeTakenSeconds,
                                 boolean practiceMode) {
        int score = 0;
        int maxScore = 0;
        Map<Integer, Integer> questionScores = new HashMap<>();

        for (Question question : questions) {
            String userAnswer = answers.get(question.getId());
            int questionScore = question.grade(userAnswer);

            score += questionScore;
            maxScore += question.getMaxScore();
            questionScores.put(question.getId(), questionScore);
        }

        QuizAttempt attempt = null;

        if (!practiceMode) {
            attempt = new QuizAttempt(0, user, quiz, score, maxScore, timeTakenSeconds);
            attemptDAO.save(attempt);
        }

        return new QuizResult(
                quiz,
                user,
                score,
                maxScore,
                timeTakenSeconds,
                practiceMode,
                attempt,
                answers,
                questionScores
        );
    }
}
