<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="quizzy.admin.StatsServlet.StatRow" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DecimalFormat" %>
<%
    long totalUsers = (Long) request.getAttribute("totalUsers");
    long totalQuizzes = (Long) request.getAttribute("totalQuizzes");
    long totalAttempts = (Long) request.getAttribute("totalAttempts");
    long totalAnnouncements = (Long) request.getAttribute("totalAnnouncements");
    List<StatRow> topByAttempts = (List<StatRow>) request.getAttribute("topByAttempts");
    List<StatRow> topByAvgScore = (List<StatRow>) request.getAttribute("topByAvgScore");
    DecimalFormat decFmt = new DecimalFormat("#.#");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Site Statistics — Quizzy Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<div class="admin-container">
    <header class="admin-header">
        <h1>Site Statistics</h1>
        <a href="${pageContext.request.contextPath}/admin/" class="btn-link">Back to Dashboard</a>
    </header>

    <!-- Summary Cards -->
    <section class="admin-section">
        <h2>Overview</h2>
        <div class="stats-grid">
            <div class="stat-card">
                <span class="stat-number"><%= totalUsers %></span>
                <span class="stat-label">Total Users</span>
            </div>
            <div class="stat-card">
                <span class="stat-number"><%= totalQuizzes %></span>
                <span class="stat-label">Total Quizzes</span>
            </div>
            <div class="stat-card">
                <span class="stat-number"><%= totalAttempts %></span>
                <span class="stat-label">Quiz Attempts</span>
            </div>
            <div class="stat-card">
                <span class="stat-number"><%= totalAnnouncements %></span>
                <span class="stat-label">Announcements</span>
            </div>
        </div>
    </section>

    <!-- Top Quizzes by Attempts -->
    <section class="admin-section">
        <h2>Top 10 Quizzes by Attempts</h2>
        <% if (topByAttempts == null || topByAttempts.isEmpty()) { %>
            <p class="empty-state">No quiz attempts recorded yet.</p>
        <% } else { %>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Quiz Title</th>
                        <th>Attempts</th>
                    </tr>
                </thead>
                <tbody>
                    <% int rank = 1;
                       for (StatRow row : topByAttempts) { %>
                        <tr>
                            <td><%= rank++ %></td>
                            <td><%= row.getLabel() %></td>
                            <td><%= (long) row.getValue() %></td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } %>
    </section>

    <!-- Top Quizzes by Average Score -->
    <section class="admin-section">
        <h2>Top 10 Quizzes by Average Score</h2>
        <% if (topByAvgScore == null || topByAvgScore.isEmpty()) { %>
            <p class="empty-state">No quiz attempts recorded yet.</p>
        <% } else { %>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Quiz Title</th>
                        <th>Avg Score</th>
                    </tr>
                </thead>
                <tbody>
                    <% int rank = 1;
                       for (StatRow row : topByAvgScore) { %>
                        <tr>
                            <td><%= rank++ %></td>
                            <td><%= row.getLabel() %></td>
                            <td><%= decFmt.format(row.getValue()) %></td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } %>
    </section>
</div>
</body>
</html>
