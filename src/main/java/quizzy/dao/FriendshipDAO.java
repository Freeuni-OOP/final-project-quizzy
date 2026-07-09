package quizzy.dao;

import quizzy.model.Friendship;
import quizzy.model.FriendshipStatus;
import quizzy.model.User;
import quizzy.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDAO {

    // a friendship is stored one way (requester -> receiver) but means the same both ways,
    // so lookups/deletes check both directions
    public Friendship findBetween(int userA, int userB) {
        String sql = "SELECT id, requester_id, receiver_id, status FROM friendships "
                + "WHERE (requester_id = ? AND receiver_id = ?) OR (requester_id = ? AND receiver_id = ?) "
                + "LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userA);
            ps.setInt(2, userB);
            ps.setInt(3, userB);
            ps.setInt(4, userA);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to look up friendship", e);
        }
    }

    public void insertRequest(int requesterId, int receiverId) {
        String sql = "INSERT INTO friendships (requester_id, receiver_id, status) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requesterId);
            ps.setInt(2, receiverId);
            ps.setString(3, FriendshipStatus.PENDING.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert friend request", e);
        }
    }

    // only touches rows that are still PENDING, so a stale accept/decline click can't flip a
    // request that was already resolved
    public boolean updatePendingStatus(int requesterId, int receiverId, FriendshipStatus status) {
        String sql = "UPDATE friendships SET status = ? "
                + "WHERE requester_id = ? AND receiver_id = ? AND status = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, requesterId);
            ps.setInt(3, receiverId);
            ps.setString(4, FriendshipStatus.PENDING.name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update friend request status", e);
        }
    }

    public boolean delete(int userA, int userB) {
        String sql = "DELETE FROM friendships "
                + "WHERE (requester_id = ? AND receiver_id = ?) OR (requester_id = ? AND receiver_id = ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userA);
            ps.setInt(2, userB);
            ps.setInt(3, userB);
            ps.setInt(4, userA);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to remove friendship", e);
        }
    }

    // accepted friends of userId, resolved to the other person in each row
    public List<User> getFriends(int userId) {
        String sql = "SELECT u.id, u.username, u.password_hash, u.salt, u.is_admin "
                + "FROM friendships f "
                + "JOIN users u ON u.id = CASE WHEN f.requester_id = ? THEN f.receiver_id ELSE f.requester_id END "
                + "WHERE (f.requester_id = ? OR f.receiver_id = ?) AND f.status = ? "
                + "ORDER BY u.username";
        List<User> friends = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ps.setString(4, FriendshipStatus.ACCEPTED.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    friends.add(new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("salt"),
                            rs.getBoolean("is_admin")));
                }
            }
            return friends;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load friends for user id: " + userId, e);
        }
    }

    private Friendship mapRow(ResultSet rs) throws SQLException {
        return new Friendship(
                rs.getInt("id"),
                rs.getInt("requester_id"),
                rs.getInt("receiver_id"),
                FriendshipStatus.valueOf(rs.getString("status")));
    }
}
