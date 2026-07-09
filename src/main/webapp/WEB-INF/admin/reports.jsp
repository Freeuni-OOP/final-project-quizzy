<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="quizzy.model.ReportedQuiz" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    List<ReportedQuiz> reports = (List<ReportedQuiz>) request.getAttribute("reports");
    String message = (String) request.getAttribute("message");
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reported Quizzes — Quizzy Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<div class="admin-container">
    <header class="admin-header">
        <h1>Reported Quizzes</h1>
        <a href="${pageContext.request.contextPath}/admin/" class="btn-link">Back to Dashboard</a>
    </header>

    <% if (message != null) { %>
        <div class="alert alert-info"><%= message %></div>
    <% } %>

    <section class="admin-section">
        <h2>Pending Reports</h2>
        <% if (reports == null || reports.isEmpty()) { %>
            <p class="empty-state">No pending reports. All clear!</p>
        <% } else { %>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Report ID</th>
                        <th>Quiz ID</th>
                        <th>Reporter</th>
                        <th>Reason</th>
                        <th>Reported At</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (ReportedQuiz r : reports) { %>
                        <tr>
                            <td><%= r.getId() %></td>
                            <td><%= r.getQuizId() %></td>
                            <td><%= r.getReporter().getUsername() %></td>
                            <td class="reason-cell">
                                <%= r.getReason().length() > 100
                                    ? r.getReason().substring(0, 100) + "..."
                                    : r.getReason() %>
                            </td>
                            <td><%= r.getCreatedAt().format(fmt) %></td>
                            <td class="actions-cell">
                                <form method="POST" action="${pageContext.request.contextPath}/admin/reports"
                                      style="display:inline"
                                      onsubmit="return confirm('Approve this report? The quiz will be permanently deleted.');">
                                    <input type="hidden" name="action" value="approve">
                                    <input type="hidden" name="reportId" value="<%= r.getId() %>">
                                    <button type="submit" class="btn-approve">Approve</button>
                                </form>
                                <form method="POST" action="${pageContext.request.contextPath}/admin/reports"
                                      style="display:inline"
                                      onsubmit="return confirm('Reject this report? The quiz will remain.');">
                                    <input type="hidden" name="action" value="reject">
                                    <input type="hidden" name="reportId" value="<%= r.getId() %>">
                                    <button type="submit" class="btn-danger">Reject</button>
                                </form>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } %>
    </section>
</div>
</body>
</html>
