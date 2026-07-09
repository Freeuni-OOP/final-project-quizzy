<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="quizzy.model.User" %>
<%
    User adminUser = (User) request.getAttribute("adminUser");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard — Quizzy</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<div class="admin-container">
    <header class="admin-header">
        <h1>Admin Panel</h1>
        <p>Welcome, <strong><%= adminUser != null ? adminUser.getUsername() : "Admin" %></strong></p>
        <a href="${pageContext.request.contextPath}/" class="btn-link">Back to Site</a>
    </header>

    <nav class="admin-nav">
        <div class="admin-card">
            <h2>Announcements</h2>
            <p>Create, edit, and delete site-wide announcements.</p>
            <a href="${pageContext.request.contextPath}/admin/announcements" class="btn-primary">Manage Announcements</a>
        </div>

        <div class="admin-card">
            <h2>Moderation</h2>
            <p>Promote users to admin, remove users, remove quizzes, and clear quiz history.</p>
            <a href="${pageContext.request.contextPath}/admin/moderation" class="btn-primary">Moderation Tools</a>
        </div>

        <div class="admin-card">
            <h2>Statistics</h2>
            <p>View site-wide statistics: user counts, top quizzes, recent signups.</p>
            <a href="${pageContext.request.contextPath}/admin/stats" class="btn-primary">View Stats</a>
        </div>

        <div class="admin-card">
            <h2>Reported Quizzes</h2>
            <p>Review and act on user-reported quizzes.</p>
            <a href="${pageContext.request.contextPath}/admin/reports" class="btn-primary">Review Reports</a>
        </div>
    </nav>
</div>
</body>
</html>
