package quizzy.dao;

import quizzy.model.Message;
import quizzy.model.MessageType;
import quizzy.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public void insert(int senderId, int receiverId, MessageType type, Integer quizId, String body) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message_type, quiz_id, body) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setString(3, type.name());
            if (quizId == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, quizId);
            }
            ps.setString(5, body);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert message", e);
        }
    }

    public List<Message> getInbox(int receiverId) {
        String sql = "SELECT id, sender_id, receiver_id, message_type, quiz_id, body "
                + "FROM messages WHERE receiver_id = ? ORDER BY id DESC";
        List<Message> inbox = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, receiverId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    inbox.add(mapRow(rs));
                }
            }
            return inbox;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load inbox for user id: " + receiverId, e);
        }
    }

    public Message findById(int id) {
        String sql = "SELECT id, sender_id, receiver_id, message_type, quiz_id, body "
                + "FROM messages WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to look up message id: " + id, e);
        }
    }

    // scoped to receiverId so a user can only delete their own mail
    public boolean delete(int id, int receiverId) {
        String sql = "DELETE FROM messages WHERE id = ? AND receiver_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, receiverId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete message id: " + id, e);
        }
    }

    private Message mapRow(ResultSet rs) throws SQLException {
        int quizIdValue = rs.getInt("quiz_id");
        Integer quizId = rs.wasNull() ? null : quizIdValue;
        return new Message(
                rs.getInt("id"),
                rs.getInt("sender_id"),
                rs.getInt("receiver_id"),
                MessageType.valueOf(rs.getString("message_type")),
                quizId,
                rs.getString("body"));
    }
}
