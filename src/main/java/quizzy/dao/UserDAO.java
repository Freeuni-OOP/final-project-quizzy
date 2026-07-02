package quizzy.dao;

import quizzy.model.User;
import quizzy.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User createUser(String username, String passwordHash, String salt) {
        String sql = "INSERT INTO users (username, password_hash, salt, is_admin) VALUES (?, ?, ?, FALSE)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, salt);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                int id = keys.next() ? keys.getInt(1) : 0;
                return new User(id, username, passwordHash, salt, false);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create user: " + username, e);
        }
    }

    public User findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, salt, is_admin FROM users WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to look up user: " + username, e);
        }
    }

    public User findById(int id) {
        String sql = "SELECT id, username, password_hash, salt, is_admin FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to look up user id: " + id, e);
        }
    }

    public boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }

    // username search for the friends feature; skips the searching user and caps the result size
    public List<User> searchByUsername(String query, int excludeId) {
        String sql = "SELECT id, username, password_hash, salt, is_admin FROM users "
                + "WHERE username LIKE ? AND id <> ? ORDER BY username LIMIT 50";
        List<User> results = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
            return results;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to search users for: " + query, e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("salt"),
                rs.getBoolean("is_admin"));
    }
}
