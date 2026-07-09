<%@ page import="quizzy.web.WebEscape" %>
<%@ page import="quizzy.web.DisplayUtil" %>
<%@ page import="quizzy.model.QuizAttempt" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%--
    ASSUMPTION — same as QuizSubmitServlet: QuizAttempt's exact post-Hibernate
    getters are inferred from QuizAttemptDAO's HQL, not yet confirmed directly:
    getUser(), getQuiz(), getScore(), getMaxScore(), getTimeTakenSeconds(), getCompletedAt().
--%>
<%
    String pageTitle = "Results \u00B7 Quizzy";
    QuizAttempt attempt = (QuizAttempt) request.getAttribute("attempt");
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy \u00B7 h:mm a");
%>
<%@ include file="common/head.jsp" %>
<%@ include file="common/header.jsp" %>

<main class="auth-shell">
  <% if (attempt == null) { %>
    <p class="empty-state">Those results couldn't be found.</p>
  <% } else {
       double pct = attempt.getMaxScore() == 0 ? 0 : (100.0 * attempt.getScore() / attempt.getMaxScore());
  %>
    <div class="ticket-card" style="max-width: 460px;">
      <div class="ticket-card__stub">
        <p class="ticket-eyebrow">Result &middot; <%= WebEscape.html(attempt.getQuiz().getTitle()) %></p>
        <h1 class="ticket-title"><%= WebEscape.html(attempt.getUser().getUsername()) %></h1>
      </div>
      <div class="ticket-card__perforation"></div>
      <div class="ticket-card__body">
        <p class="results-score"><%= attempt.getScore() %> / <%= attempt.getMaxScore() %></p>
        <p class="results-meta">
          <%= String.format("%.0f%%", pct) %> correct &middot; <%= DisplayUtil.formatTime(attempt.getTimeTakenSeconds()) %>
          <% if (attempt.getCompletedAt() != null) { %>
            <br><%= attempt.getCompletedAt().format(fmt) %>
          <% } %>
        </p>
        <div class="results-actions">
          <a class="btn-stamp" style="text-decoration:none;" href="<%= request.getContextPath() %>/quiz/summary?id=<%= attempt.getQuiz().getId() %>">Leaderboard</a>
          <a class="btn-stamp btn-stamp--secondary" style="text-decoration:none;" href="<%= request.getContextPath() %>/quiz/take?id=<%= attempt.getQuiz().getId() %>">Take again</a>
        </div>
      </div>
    </div>
  <% } %>
</main>

<%@ include file="common/footer.jsp" %>
