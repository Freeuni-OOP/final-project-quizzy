package quizzy.dao;

import quizzy.model.Achievement;
import quizzy.model.AttemptSummary;
import quizzy.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// read-only queries for the profile page. these tables belong to M1/M4, we only read them here.
public class ProfileDAO {

    public List<AttemptSummary> getAttempts(int userId) {
        String sql = "SELECT a.quiz_id, q.title, a.score, a.max_score, a.time_taken_seconds "
                + "FROM quiz_attempts a JOIN quizzes q ON q.id = a.quiz_id "
                + "WHERE a.user_id = ? ORDER BY a.id DESC";
        List<AttemptSummary> attempts = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    attempts.add(new AttemptSummary(
                            rs.getInt("quiz_id"),
                            rs.getString("title"),
                            rs.getInt("score"),
                            rs.getInt("max_score"),
                            rs.getLong("time_taken_seconds")));
                }
            }
            return attempts;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load quiz history for user id: " + userId, e);
        }
    }

    public List<Achievement> getAchievements(int userId) {
        String sql = "SELECT ac.id, ac.name, ac.description "
                + "FROM user_achievements ua JOIN achievements ac ON ac.id = ua.achievement_id "
                + "WHERE ua.user_id = ? ORDER BY ac.name";
        List<Achievement> achievements = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    achievements.add(new Achievement(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description")));
                }
            }
            return achievements;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load achievements for user id: " + userId, e);
        }
    }
}
