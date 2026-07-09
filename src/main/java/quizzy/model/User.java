package quizzy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a registered user of the Quizzy website.
 * M4 owns this entity; M2 uses it for authentication.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String salt;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    /** Required by Hibernate. */
    protected User() {
    }

    /**
     * Constructs a User with the given properties.
     *
     * @param id           the database-assigned ID (0 for new users)
     * @param username     unique login name
     * @param passwordHash SHA-256 hash of (salt + password)
     * @param salt         random salt used during password hashing
     * @param isAdmin      whether the user has administrative privileges
     */
    public User(int id, String username, String passwordHash, String salt, boolean isAdmin) {
public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String salt;
    private boolean admin;

    public User(int id, String username, String passwordHash, String salt, boolean admin) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.isAdmin = isAdmin;
        this.admin = admin;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    // only used by the auth layer, don't expose these to the JSPs
    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public boolean isAdmin() {
        return isAdmin;
        return admin;
    }
}
