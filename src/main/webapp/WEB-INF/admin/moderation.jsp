<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String message = (String) request.getAttribute("message");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Moderation Tools — Quizzy Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<div class="admin-container">
    <header class="admin-header">
        <h1>Moderation Tools</h1>
        <a href="${pageContext.request.contextPath}/admin/" class="btn-link">Back to Dashboard</a>
    </header>

    <% if (message != null) { %>
        <div class="alert alert-info"><%= message %></div>
    <% } %>

    <!-- Promote User -->
    <section class="admin-section">
        <h2>Promote User to Admin</h2>
        <p class="section-desc">Grant administrative privileges to an existing user.</p>
        <form method="POST" action="${pageContext.request.contextPath}/admin/moderation" class="admin-form">
            <input type="hidden" name="action" value="promote">
            <div class="form-group">
                <label for="promoteUserId">User ID</label>
                <input type="number" id="promoteUserId" name="userId" min="1" required>
            </div>
            <button type="submit" class="btn-primary"
                    onclick="return confirm('Promote this user to admin?');">
                Promote to Admin
            </button>
        </form>
    </section>

    <!-- Remove User -->
    <section class="admin-section admin-section-danger">
        <h2>Remove User</h2>
        <p class="section-desc">Permanently delete a user account. This action cannot be undone.</p>
        <form method="POST" action="${pageContext.request.contextPath}/admin/moderation" class="admin-form">
            <input type="hidden" name="action" value="remove-user">
            <div class="form-group">
                <label for="removeUserId">User ID</label>
                <input type="number" id="removeUserId" name="userId" min="1" required>
            </div>
            <button type="submit" class="btn-danger"
                    onclick="return confirm('WARNING: This will permanently delete the user. Continue?');">
                Remove User
            </button>
        </form>
    </section>

    <!-- Remove Quiz -->
    <section class="admin-section admin-section-danger">
        <h2>Remove Quiz</h2>
        <p class="section-desc">Delete a quiz and all of its attempts. This action cannot be undone.</p>
        <form method="POST" action="${pageContext.request.contextPath}/admin/moderation" class="admin-form">
            <input type="hidden" name="action" value="remove-quiz">
            <div class="form-group">
                <label for="removeQuizId">Quiz ID</label>
                <input type="number" id="removeQuizId" name="quizId" min="1" required>
            </div>
            <button type="submit" class="btn-danger"
                    onclick="return confirm('WARNING: This will delete the quiz and all its attempts. Continue?');">
                Remove Quiz
            </button>
        </form>
    </section>

    <!-- Clear Quiz History -->
    <section class="admin-section admin-section-danger">
        <h2>Clear Quiz History</h2>
        <p class="section-desc">Delete all attempt records for a quiz, but keep the quiz itself.</p>
        <form method="POST" action="${pageContext.request.contextPath}/admin/moderation" class="admin-form">
            <input type="hidden" name="action" value="clear-history">
            <div class="form-group">
                <label for="clearQuizId">Quiz ID</label>
                <input type="number" id="clearQuizId" name="quizId" min="1" required>
            </div>
            <button type="submit" class="btn-danger"
                    onclick="return confirm('Clear all attempt history for this quiz?');">
                Clear History
            </button>
        </form>
    </section>
</div>
</body>
</html>
