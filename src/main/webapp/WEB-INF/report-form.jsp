<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String quizId = (String) request.getAttribute("quizId");
    String message = (String) request.getAttribute("message");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Report Quiz — Quizzy</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<div class="admin-container">
    <header class="admin-header">
        <h1>Report Quiz</h1>
        <a href="${pageContext.request.contextPath}/" class="btn-link">Back to Site</a>
    </header>

    <% if (message != null) { %>
        <div class="alert alert-info"><%= message %></div>
    <% } %>

    <section class="admin-section">
        <h2>Report Quiz #<%= quizId %></h2>
        <p class="section-desc">Please describe why you believe this quiz violates our guidelines.
           Your report will be reviewed by an administrator.</p>

        <form method="POST" action="${pageContext.request.contextPath}/report-quiz" class="admin-form">
            <input type="hidden" name="quizId" value="<%= quizId %>">
            <div class="form-group">
                <label for="reason">Reason</label>
                <textarea id="reason" name="reason" rows="5" required
                    placeholder="Describe the issue with this quiz..."></textarea>
            </div>
            <button type="submit" class="btn-primary">Submit Report</button>
        </form>
    </section>
</div>
</body>
</html>
