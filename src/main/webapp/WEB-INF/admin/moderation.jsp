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
                <div style="display:flex; align-items:center; gap:0.75rem;">
                    <input type="number" id="promoteUserId" name="userId" min="1" required
                           oninput="lookupUser(this.value, 'promoteResult')"
                           style="max-width:200px;">
                    <span id="promoteResult" class="lookup-result"></span>
                </div>
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
                <div style="display:flex; align-items:center; gap:0.75rem;">
                    <input type="number" id="removeUserId" name="userId" min="1" required
                           oninput="lookupUser(this.value, 'removeResult')"
                           style="max-width:200px;">
                    <span id="removeResult" class="lookup-result"></span>
                </div>
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
                <div style="display:flex; align-items:center; gap:0.75rem;">
                    <input type="number" id="removeQuizId" name="quizId" min="1" required
                           oninput="lookupQuiz(this.value, 'removeQuizResult')"
                           style="max-width:200px;">
                    <span id="removeQuizResult" class="lookup-result"></span>
                </div>
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
                <div style="display:flex; align-items:center; gap:0.75rem;">
                    <input type="number" id="clearQuizId" name="quizId" min="1" required
                           oninput="lookupQuiz(this.value, 'clearQuizResult')"
                           style="max-width:200px;">
                    <span id="clearQuizResult" class="lookup-result"></span>
                </div>
            </div>
            <button type="submit" class="btn-danger"
                    onclick="return confirm('Clear all attempt history for this quiz?');">
                Clear History
            </button>
        </form>
    </section>
</div>

<style>
  .lookup-result { font-weight: 600; font-size: 0.92rem; }
  .lookup-result.found { color: var(--success, #2d8a56); }
  .lookup-result.admin-tag { color: var(--gold-dark, #b8860b); }
  .lookup-result.not-found { color: var(--incorrect, #c0392b); }
</style>

<script>
  var userTimer, quizTimer;
  function lookupUser(userId, resultId) {
    var el = document.getElementById(resultId);
    if (!userId || userId < 1) { el.textContent = ''; return; }
    clearTimeout(userTimer);
    userTimer = setTimeout(function() {
      fetch('${pageContext.request.contextPath}/admin/moderation?lookup=' + encodeURIComponent(userId))
        .then(function(r) { return r.json(); })
        .then(function(user) {
          if (user) {
            el.textContent = user.username + (user.isAdmin ? ' 👑' : '');
            el.className = 'lookup-result ' + (user.isAdmin ? 'admin-tag' : 'found');
          } else {
            el.textContent = '✗ Not found';
            el.className = 'lookup-result not-found';
          }
        })
        .catch(function() {
          el.textContent = '⚠ Error';
          el.className = 'lookup-result not-found';
        });
    }, 300);
  }

  function lookupQuiz(quizId, resultId) {
    var el = document.getElementById(resultId);
    if (!quizId || quizId < 1) { el.textContent = ''; return; }
    clearTimeout(quizTimer);
    quizTimer = setTimeout(function() {
      fetch('${pageContext.request.contextPath}/admin/moderation?qlookup=' + encodeURIComponent(quizId))
        .then(function(r) { return r.json(); })
        .then(function(quiz) {
          if (quiz) {
            el.textContent = quiz.title + ' (by ' + quiz.creator + ')';
            el.className = 'lookup-result found';
          } else {
            el.textContent = '✗ Not found';
            el.className = 'lookup-result not-found';
          }
        })
        .catch(function() {
          el.textContent = '⚠ Error';
          el.className = 'lookup-result not-found';
        });
    }, 300);
  }
</script>

</body>
</html>
