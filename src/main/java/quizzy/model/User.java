package quizzy.model;

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
        return admin;
    }
}
