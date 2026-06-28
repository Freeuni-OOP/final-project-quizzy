package quizzy.model;

public class Quiz {
    private int id;
    private int creatorId;
    private String title;
    private String description;
    private boolean randomQuestions;
    private boolean onePage;
    private boolean immediateCorrection;
    private boolean practiceMode;

    public Quiz(int id, int creatorId, String title, String description, boolean randomQuestions, boolean onePage, boolean immediateCorrection, boolean practiceMode) {
        this.id = id;
        this.creatorId = creatorId;
        this.title = title;
        this.description = description;
        this.randomQuestions = randomQuestions;
        this.onePage = onePage;
        this.immediateCorrection = immediateCorrection;
        this.practiceMode = practiceMode;
    }

    public int getId() {
        return id;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRandomQuestions() {
        return randomQuestions;
    }

    public boolean isOnePage() {
        return onePage;
    }

    public boolean isImmediateCorrection() {
        return immediateCorrection;
    }

    public boolean isPracticeMode() {
        return practiceMode;
    }
}
