package quizzy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * An announcement created by an administrator and displayed on the homepage.
 */
@Entity
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "creator_id", nullable = false)
    private int creatorId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** Required by Hibernate. */
    protected Announcement() {
    }

    /**
     * Constructs an Announcement. The creation timestamp is set to now.
     *
     * @param id        the database-assigned ID (0 for new announcements)
     * @param creatorId the ID of the admin who created this announcement
     * @param title     the announcement headline
     * @param content   the announcement body text
     */
    public Announcement(int id, int creatorId, String title, String content) {
        this(id, creatorId, title, content, LocalDateTime.now());
    }

    /**
     * Constructs an Announcement with an explicit creation timestamp.
     * Used when updating an existing announcement to preserve the original time.
     *
     * @param id        the database-assigned ID
     * @param creatorId the ID of the admin who created this announcement
     * @param title     the announcement headline
     * @param content   the announcement body text
     * @param createdAt the original creation timestamp
     */
    public Announcement(int id, int creatorId, String title, String content, LocalDateTime createdAt) {
        this.id = id;
        this.creatorId = creatorId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
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

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
