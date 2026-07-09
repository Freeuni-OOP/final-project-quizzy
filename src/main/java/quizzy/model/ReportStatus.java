package quizzy.model;

/**
 * Status of a reported quiz in the admin review workflow.
 */
public enum ReportStatus {

    /** Report submitted but not yet reviewed by an admin. */
    PENDING,

    /** Admin approved the report; the quiz was removed. */
    APPROVED,

    /** Admin rejected the report; the quiz remains on the site. */
    REJECTED
}
