<%@ page import="quizzy.web.WebEscape" %>
<%@ page import="quizzy.web.DisplayUtil" %>
<%@ page import="quizzy.model.Quiz" %>
<%@ page import="quizzy.model.User" %>
<%@ page import="quizzy.service.AttemptView" %>
<%@ page import="java.util.List" %>
<%--
    "quiz" request attribute is the full Quiz entity.
    topAttempts / recentTopAttempts / userAttempts are List<AttemptView>
    (quizzy.service.AttemptView — built by M1, confirmed shape from QuizAttemptDAO).
    "averageScore" is the raw average points score (Double, null if no attempts) —
    NOT a percentage, since maxScore varies per quiz. "maxScore" is a single
    representative value borrowed from the top attempt, used only for display
    (e.g. "3.4 / 5").
--%>
<%
    String pageTitle = "Quiz \u00B7 Quizzy";

    Quiz quiz = (Quiz) request.getAttribute("quiz");
    List<AttemptView> topAttempts = (List<AttemptView>) request.getAttribute("topAttempts");
    List<AttemptView> recentTopAttempts = (List<AttemptView>) request.getAttribute("recentTopAttempts");
    List<AttemptView> userAttempts = (List<AttemptView>) request.getAttribute("userAttempts");
    Double averageScore = (Double) request.getAttribute("averageScore");
    Integer maxScoreForDisplay = (Integer) request.getAttribute("maxScore");
%>
<%@ include file="common/head.jsp" %>
<%@ include file="common/header.jsp" %>

<%
    // currentUser already declared by header.jsp's include, above
    boolean isOwner = currentUser != null && quiz != null && currentUser.getId() == quiz.getCreatorId();
%>

<main class="container" style="max-width: 820px; margin: 0 auto;">

  <% if (quiz == null) { %>
    <p class="empty-state" style="padding: 2rem 0;">This quiz couldn't be found.</p>
  <% } else { %>

    <section class="quiz-header">
      <h1><%= WebEscape.html(quiz.getTitle()) %></h1>
      <p class="creator-line">
        by <a href="<%= ctx %>/profile?user=<%= WebEscape.html(quiz.getCreator().getUsername()) %>">
          <%= WebEscape.html(quiz.getCreator().getUsername()) %>
        </a>
      </p>
      <p><%= WebEscape.html(quiz.getDescription()) %></p>

      <div class="option-tags">
        <% if (quiz.isRandomQuestions()) { %><span class="option-tag">Random order</span><% } %>
        <span class="option-tag"><%= quiz.isOnePage() ? "Single page" : "One question per page" %></span>
        <% if (quiz.isImmediateCorrection()) { %><span class="option-tag">Immediate correction</span><% } %>
        <% if (quiz.isPracticeMode()) { %><span class="option-tag">Practice mode available</span><% } %>
      </div>

      <div class="cta-row">
        <a class="btn-stamp" href="<%= ctx %>/quiz/take?id=<%= quiz.getId() %>" style="text-decoration:none;">Take quiz</a>
        <% if (quiz.isPracticeMode()) { %>
          <a class="btn-stamp btn-stamp--secondary" href="<%= ctx %>/quiz/take?id=<%= quiz.getId() %>&practice=true" style="text-decoration:none;">Practice mode</a>
        <% } %>
      </div>

      <% if (currentUser != null) { %>
        <p style="margin-top: -0.75rem; margin-bottom: 1.5rem;">
          <a class="section-link" style="color: var(--incorrect);"
             href="<%= ctx %>/report-quiz?quizId=<%= quiz.getId() %>">Report this quiz</a>
        </p>
      <% } %>
    </section>

    <div class="stat-strip">
      <div>
        <span class="stat-value"><%= averageScore != null
              ? String.format("%.1f", averageScore) + (maxScoreForDisplay != null ? " / " + maxScoreForDisplay : "")
              : "\u2014" %></span>
        <span class="stat-label">Average score</span>
      </div>
      <div>
        <span class="stat-value"><%= topAttempts != null ? topAttempts.size() : 0 %></span>
        <span class="stat-label">Attempts</span>
      </div>
    </div>

    <%-- ---------------- top scorers (all time) ---------------- --%>
    <section class="panel">
      <h2>Top scorers</h2>
      <% if (topAttempts != null && !topAttempts.isEmpty()) { %>
        <table class="leaderboard-table">
          <tr><th>#</th><th>User</th><th>Score</th><th>Time</th></tr>
          <% for (int i = 0; i < topAttempts.size(); i++) {
               AttemptView a = topAttempts.get(i);
               int rank = i + 1;
          %>
            <tr>
              <td><span class="rank-badge <%= rank <= 3 ? "rank-badge--" + rank : "" %>"><%= rank %></span></td>
              <td><%= WebEscape.html(a.getUsername()) %></td>
              <td><%= a.getScore() %>/<%= a.getMaxScore() %></td>
              <td><%= DisplayUtil.formatTime(a.getTimeTakenSeconds()) %></td>
            </tr>
          <% } %>
        </table>
      <% } else { %>
        <p class="empty-state">No one has taken this quiz yet &mdash; be the first.</p>
      <% } %>
    </section>

    <%-- ---------------- top performers, last 24h ---------------- --%>
    <section class="panel">
      <h2>Top performers &middot; last 24 hours</h2>
      <% if (recentTopAttempts != null && !recentTopAttempts.isEmpty()) { %>
        <table class="leaderboard-table">
          <tr><th>#</th><th>User</th><th>Score</th><th>Time</th></tr>
          <% for (int i = 0; i < recentTopAttempts.size(); i++) {
               AttemptView a = recentTopAttempts.get(i);
               int rank = i + 1;
          %>
            <tr>
              <td><span class="rank-badge <%= rank <= 3 ? "rank-badge--" + rank : "" %>"><%= rank %></span></td>
              <td><%= WebEscape.html(a.getUsername()) %></td>
              <td><%= a.getScore() %>/<%= a.getMaxScore() %></td>
              <td><%= DisplayUtil.formatTime(a.getTimeTakenSeconds()) %></td>
            </tr>
          <% } %>
        </table>
      <% } else { %>
        <p class="empty-state">No attempts in the last 24 hours.</p>
      <% } %>
    </section>

    <%-- ---------------- your history on this quiz ---------------- --%>
    <% if (currentUser != null) { %>
      <section class="panel">
        <h2>Your history on this quiz</h2>
        <% if (userAttempts != null && !userAttempts.isEmpty()) { %>
          <table class="leaderboard-table">
            <tr><th>Score</th><th>Time</th></tr>
            <% for (AttemptView a : userAttempts) { %>
              <tr>
                <td><%= a.getScore() %>/<%= a.getMaxScore() %></td>
                <td><%= DisplayUtil.formatTime(a.getTimeTakenSeconds()) %></td>
              </tr>
            <% } %>
          </table>
        <% } else { %>
          <p class="empty-state">You haven't taken this quiz yet.</p>
        <% } %>
      </section>
    <% } %>

  <% } %>

</main>

<%@ include file="common/footer.jsp" %>
