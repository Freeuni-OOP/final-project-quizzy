package quizzy.service;

import quizzy.model.Quiz;
import quizzy.model.question.Question;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizSession {
    private Quiz quiz;
    private List<Question> questions;
    private int currentQuestionIndex;
    private long startTimeMillis;
    private boolean practiceMode;
    private Map<Integer, String> answers;

    public QuizSession(Quiz quiz, List<Question> questions, boolean practiceMode) {
        this.quiz = quiz;
        this.questions = questions;
        this.practiceMode = practiceMode;
        this.currentQuestionIndex = 0;
        this.startTimeMillis = System.currentTimeMillis();
        this.answers = new HashMap<>();
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public Question getCurrentQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            return null;
        }

        return questions.get(currentQuestionIndex);
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void recordAnswer(int questionId, String answer) {
        answers.put(questionId, answer);
    }

    public String getAnswer(int questionId) {
        return answers.get(questionId);
    }

    public Map<Integer, String> getAnswers() {
        return answers;
    }

    public void moveToNextQuestion() {
        currentQuestionIndex++;
    }

    public boolean isFinished() {
        return currentQuestionIndex >= questions.size();
    }

    public long getElapsedSeconds() {
        long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
        return elapsedMillis / 1000;
    }

    public boolean isPracticeMode() {
        return practiceMode;
    }
}
