package quizzy.model.question;

public abstract class Question {
    private int id;
    private int quizId;
    private String prompt;

    public Question(int id, int quizId, String prompt) {
        this.id = id;
        this.quizId = quizId;
        this.prompt = prompt;
    }

    public int getId() {
        return id;
    }

    public int getQuizId() {
        return quizId;
    }

    public String getPrompt() {
        return prompt;
    }

    public abstract QuestionType getType();

    public abstract int getMaxScore();

    public abstract int grade(String answer);

    public String normalize(String text) {
        if (text == null) {
            return "";
        }

        return text.trim().toLowerCase();
    }
}