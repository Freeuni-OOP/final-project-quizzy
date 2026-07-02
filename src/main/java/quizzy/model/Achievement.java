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
}
