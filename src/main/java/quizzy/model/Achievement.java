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
    PRACTICE_MAKES_PERFECT
public class Achievement {
    private int id;
    private String name;
    private String description;

    public Achievement(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
