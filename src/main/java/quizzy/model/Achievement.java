package quizzy.model;

/**
 * Achievements awarded to users for reaching specific milestones.
 * Once earned, an achievement is never removed.
 */
public enum Achievement {

    /** User created at least one quiz. */
    AMATEUR_AUTHOR,

    /** User created at least five quizzes. */
    PROLIFIC_AUTHOR,

    /** User created at least ten quizzes. */
    PRODIGIOUS_AUTHOR,

    /** User took at least ten quizzes. */
    QUIZ_MACHINE,

    /** User had the highest score on a quiz at the moment of completion. */
    I_AM_THE_GREATEST,

    /** User took a quiz in practice mode. */
    PRACTICE_MAKES_PERFECT;

    /**
     * Returns a human-readable display name for this achievement.
     */
    public String getName() {
        switch (this) {
            case AMATEUR_AUTHOR:
                return "Amateur Author";
            case PROLIFIC_AUTHOR:
                return "Prolific Author";
            case PRODIGIOUS_AUTHOR:
                return "Prodigious Author";
            case QUIZ_MACHINE:
                return "Quiz Machine";
            case I_AM_THE_GREATEST:
                return "I Am The Greatest";
            case PRACTICE_MAKES_PERFECT:
                return "Practice Makes Perfect";
            default:
                return name();
        }
    }

    /**
     * Returns a human-readable description for this achievement.
     */
    public String getDescription() {
        switch (this) {
            case AMATEUR_AUTHOR:
                return "Created your first quiz!";
            case PROLIFIC_AUTHOR:
                return "Created 5 quizzes!";
            case PRODIGIOUS_AUTHOR:
                return "Created 10 quizzes!";
            case QUIZ_MACHINE:
                return "Took 10 quizzes!";
            case I_AM_THE_GREATEST:
                return "Got the highest score on a quiz!";
            case PRACTICE_MAKES_PERFECT:
                return "Took a quiz in practice mode!";
            default:
                return "";
        }
    }
}
