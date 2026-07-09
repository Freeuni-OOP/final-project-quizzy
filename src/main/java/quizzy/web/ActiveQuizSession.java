package quizzy.web;

import java.io.Serializable;
import java.util.List;

/**
 * Tracks one in-progress quiz attempt in the HttpSession — never persisted to
 * the database. Stored under key "quizSession_" + quizId, so a user can have
 * multiple quizzes in progress at once (e.g. different browser tabs) without
 * them clobbering each other.
 *
 * One instance covers both one-page and multi-page quizzes: for one-page mode
 * only questionOrder/startTimeMillis/practice matter (the whole quiz is graded
 * in a single submit); for multi-page mode, currentIndex/scoreSoFar also track
 * progress between requests.
 */
public class ActiveQuizSession implements Serializable {

    private final int quizId;
    private final boolean practice;
    private final List<Integer> questionOrder;
    private final long startTimeMillis;

    private int currentIndex = 0;
    private int scoreSoFar = 0;

    // Set right after grading a question, read once by the feedback page,
    // then not needed again — QuizSubmitServlet sets these, quiz-feedback.jsp reads them.
    private boolean lastAnswerCorrect;
    private String lastCorrectAnswerText;

    public ActiveQuizSession(int quizId, boolean practice, List<Integer> questionOrder) {
        this.quizId = quizId;
        this.practice = practice;
        this.questionOrder = questionOrder;
        this.startTimeMillis = System.currentTimeMillis();
    }

    public int getQuizId() {
        return quizId;
    }

    public boolean isPractice() {
        return practice;
    }

    public List<Integer> getQuestionOrder() {
        return questionOrder;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void advance() {
        currentIndex++;
    }

    public boolean isComplete() {
        return currentIndex >= questionOrder.size();
    }

    public Integer currentQuestionId() {
        return isComplete() ? null : questionOrder.get(currentIndex);
    }

    public int getScoreSoFar() {
        return scoreSoFar;
    }

    public void addToScore(int points) {
        scoreSoFar += points;
    }

    public long getTimeTakenSeconds() {
        return (System.currentTimeMillis() - startTimeMillis) / 1000;
    }

    public boolean isLastAnswerCorrect() {
        return lastAnswerCorrect;
    }

    public void setLastAnswerCorrect(boolean lastAnswerCorrect) {
        this.lastAnswerCorrect = lastAnswerCorrect;
    }

    public String getLastCorrectAnswerText() {
        return lastCorrectAnswerText;
    }

    public void setLastCorrectAnswerText(String lastCorrectAnswerText) {
        this.lastCorrectAnswerText = lastCorrectAnswerText;
    }

    public static String sessionKey(int quizId) {
        return "quizSession_" + quizId;
    }
}
