package quizzy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Represents a user report flagging a quiz as inappropriate.
 * Reports go through an admin review workflow: PENDING → APPROVED or REJECTED.
 */
@Entity
@Table(name = "reported_quizzes")
public class ReportedQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "quiz_id", nullable = false)
    private int quizId;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "reviewed_by")
    private Integer reviewedById;

    /** Required by Hibernate. */
    protected ReportedQuiz() {
    }

    /**
     * Constructs a new report with PENDING status and a creation timestamp of now.
     *
     * @param id       the database-assigned ID (0 for new reports)
     * @param quizId   the ID of the quiz being reported
     * @param reporter the user submitting the report
     * @param reason   the reason the quiz is being flagged
     */
    public ReportedQuiz(int id, int quizId, User reporter, String reason) {
        this(id, quizId, reporter, reason, ReportStatus.PENDING,
                LocalDateTime.now(), null, null);
    }

    /**
     * Full constructor used when reconstructing an existing report for updates
     * (e.g. setting resolution fields after admin review).
     */
    public ReportedQuiz(int id, int quizId, User reporter, String reason,
                        ReportStatus status, LocalDateTime createdAt,
                        LocalDateTime resolvedAt, Integer reviewedById) {
        this.id = id;
        this.quizId = quizId;
        this.reporter = reporter;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
        this.reviewedById = reviewedById;
    }

    public int getId() {
        return id;
    }

    public int getQuizId() {
        return quizId;
    }

    public User getReporter() {
        return reporter;
    }

    public String getReason() {
        return reason;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public Integer getReviewedById() {
        return reviewedById;
    }
}
