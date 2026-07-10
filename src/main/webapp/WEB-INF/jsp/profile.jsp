<%@ page import="quizzy.web.WebEscape" %>
<%@ page import="quizzy.model.User" %>
<%@ page import="quizzy.model.AttemptSummary" %>
<%@ page import="quizzy.model.Achievement" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="m4" uri="http://quizzy.freeuni.ge/tags/m4" %>
<%
    String pageTitle = "Profile · Quizzy";
    User profileUser = (User) request.getAttribute("profileUser");
    List<AttemptSummary> attempts = (List<AttemptSummary>) request.getAttribute("attempts");
    List<Achievement> achievements = (List<Achievement>) request.getAttribute("achievements");
    String friendState = (String) request.getAttribute("friendState");
%>
<%@ include file="common/head.jsp" %>
<%@ include file="common/header.jsp" %>

<main class="container" style="max-width: 680px; margin: 0 auto; padding: 2rem 0 3rem;">

  <section class="panel">
    <h1>@<%= WebEscape.html(profileUser.getUsername()) %></h1>

    <% if ("NONE".equals(friendState)) { %>
      <form method="post" action="<%= ctx %>/friends/request" style="margin-top:0.75rem;">
        <input type="hidden" name="targetId" value="<%= profileUser.getId() %>">
        <button type="submit" class="btn-stamp" style="width:auto; padding:0.4rem 1.2rem; font-size:0.9rem;">Add friend</button>
      </form>
    <% } else if ("PENDING_OUT".equals(friendState)) { %>
      <p class="muted" style="margin-top:0.5rem;">Friend request pending</p>
    <% } else if ("PENDING_IN".equals(friendState)) { %>
      <p style="margin-top:0.5rem;">
        <span class="option-tag">Wants to be your friend</span>
        <a href="<%= ctx %>/inbox" class="btn-stamp" style="display:inline-block; width:auto; padding:0.3rem 0.8rem; font-size:0.85rem; text-decoration:none; margin-left:0.5rem;">Respond in inbox</a>
      </p>
    <% } else if ("FRIENDS".equals(friendState)) { %>
      <div style="margin-top:0.75rem; display:flex; align-items:center; gap:0.75rem;">
        <span class="option-tag" style="background:var(--success); color:#fff; border-color:var(--success);">You are friends</span>
        <form method="post" action="<%= ctx %>/friends/remove" style="display:inline;">
          <input type="hidden" name="friendId" value="<%= profileUser.getId() %>">
          <button type="submit" class="btn-ghost"
                  style="color:var(--incorrect); border-color:var(--incorrect); font-size:0.85rem;"
                  onclick="return confirm('Remove this friend?');">Remove friend</button>
        </form>
      </div>
    <% } %>
  </section>

  <section class="panel">
    <h2>Quiz history</h2>
    <% if (attempts == null || attempts.isEmpty()) { %>
      <p class="empty-state">No quizzes taken yet.</p>
    <% } else { %>
      <table class="leaderboard-table" style="width:100%;">
        <tr><th>Quiz</th><th>Score</th><th>Time</th></tr>
        <% for (AttemptSummary a : attempts) { %>
          <tr>
            <td><%= WebEscape.html(a.getQuizTitle()) %></td>
            <td><%= a.getScore() %> / <%= a.getMaxScore() %></td>
            <td><%= a.getTimeTakenSeconds() %>s</td>
          </tr>
        <% } %>
      </table>
    <% } %>
  </section>

  <section class="panel">
    <h2>Achievements</h2>
    <% if (achievements == null || achievements.isEmpty()) { %>
      <p class="empty-state">No achievements earned yet.</p>
    <% } else { %>
      <div style="margin-bottom:0.5rem;">
        <m4:achievements user="${profileUser}" />
      </div>
      <ul style="list-style:none; padding:0; display:flex; flex-direction:column; gap:0.5rem;">
        <% for (Achievement ach : achievements) { %>
          <li>
            <strong><%= WebEscape.html(ach.getName()) %></strong>
            <span class="muted" style="font-size:0.85rem; margin-left:0.5rem;"><%= WebEscape.html(ach.getDescription()) %></span>
          </li>
        <% } %>
      </ul>
    <% } %>
  </section>

</main>

<%@ include file="common/footer.jsp" %>
