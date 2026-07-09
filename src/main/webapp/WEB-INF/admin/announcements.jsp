<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="quizzy.model.Announcement" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    List<Announcement> announcements = (List<Announcement>) request.getAttribute("announcements");
    String message = (String) request.getAttribute("message");
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Announcements — Quizzy Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<div class="admin-container">
    <header class="admin-header">
        <h1>Manage Announcements</h1>
        <a href="${pageContext.request.contextPath}/admin/" class="btn-link">Back to Dashboard</a>
    </header>

    <% if (message != null) { %>
        <div class="alert alert-info"><%= message %></div>
    <% } %>

    <!-- Create Form -->
    <section class="admin-section">
        <h2>Create Announcement</h2>
        <form method="POST" action="${pageContext.request.contextPath}/admin/announcements" class="admin-form">
            <input type="hidden" name="action" value="create">
            <div class="form-group">
                <label for="newTitle">Title</label>
                <input type="text" id="newTitle" name="title" maxlength="200" required>
            </div>
            <div class="form-group">
                <label for="newContent">Content</label>
                <textarea id="newContent" name="content" rows="4" required></textarea>
            </div>
            <button type="submit" class="btn-primary">Create Announcement</button>
        </form>
    </section>

    <!-- Existing Announcements -->
    <section class="admin-section">
        <h2>All Announcements</h2>
        <% if (announcements == null || announcements.isEmpty()) { %>
            <p class="empty-state">No announcements yet.</p>
        <% } else { %>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Content</th>
                        <th>Created</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Announcement a : announcements) { %>
                        <tr>
                            <td><%= a.getId() %></td>
                            <td><%= a.getTitle() %></td>
                            <td><%= a.getContent() %></td>
                            <td><%= a.getCreatedAt().format(fmt) %></td>
                            <td class="actions-cell">
                                <button type="button" class="btn-edit"
                                        onclick="editAnnouncement(<%= a.getId() %>, '<%= a.getTitle().replace("'", "\\'") %>', '<%= a.getContent().replace("'", "\\'") %>')">
                                    Edit
                                </button>
                                <form method="POST" action="${pageContext.request.contextPath}/admin/announcements"
                                      style="display:inline"
                                      onsubmit="return confirm('Delete this announcement?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="<%= a.getId() %>">
                                    <button type="submit" class="btn-danger">Delete</button>
                                </form>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } %>
    </section>

    <!-- Hidden Edit Form (shown via JavaScript when Edit is clicked) -->
    <section id="editSection" class="admin-section" style="display:none;">
        <h2>Edit Announcement</h2>
        <form method="POST" action="${pageContext.request.contextPath}/admin/announcements" class="admin-form">
            <input type="hidden" name="action" value="update">
            <input type="hidden" id="editId" name="id" value="">
            <div class="form-group">
                <label for="editTitle">Title</label>
                <input type="text" id="editTitle" name="title" maxlength="200" required>
            </div>
            <div class="form-group">
                <label for="editContent">Content</label>
                <textarea id="editContent" name="content" rows="4" required></textarea>
            </div>
            <button type="submit" class="btn-primary">Update Announcement</button>
            <button type="button" class="btn-cancel" onclick="cancelEdit()">Cancel</button>
        </form>
    </section>
</div>

<script>
function editAnnouncement(id, title, content) {
    document.getElementById('editId').value = id;
    document.getElementById('editTitle').value = title;
    document.getElementById('editContent').value = content;
    document.getElementById('editSection').style.display = 'block';
    document.getElementById('editSection').scrollIntoView({ behavior: 'smooth' });
}
function cancelEdit() {
    document.getElementById('editSection').style.display = 'none';
    document.getElementById('editId').value = '';
    document.getElementById('editTitle').value = '';
    document.getElementById('editContent').value = '';
}
</script>
</body>
</html>
