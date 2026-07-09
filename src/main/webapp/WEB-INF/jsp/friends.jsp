<%@ page import="quizzy.web.WebEscape" %>
<%@ page import="quizzy.model.User" %>
<%@ page import="java.util.List" %>
<%
    String pageTitle = "Friends · Quizzy";
    List<User> friends = (List<User>) request.getAttribute("friends");
%>
<%@ include file="common/head.jsp" %>
<%@ include file="common/header.jsp" %>

<main class="container" style="max-width: 600px; margin: 0 auto; padding: 2rem 0 3rem;">
  <h1>Friends</h1>
  <% if (friends == null || friends.isEmpty()) { %>
    <p class="empty-state">
      You have no friends yet. <a href="<%= ctx %>/search">Find some users</a>.
    </p>
  <% } else { %>
    <table class="leaderboard-table" style="width:100%;">
      <tr><th>User</th><th style="text-align:right;">Actions</th></tr>
      <% for (User friend : friends) { %>
        <tr>
          <td>
            <a href="<%= ctx %>/profile?id=<%= friend.getId() %>">
              @<%= WebEscape.html(friend.getUsername()) %>
            </a>
          </td>
          <td style="text-align:right;">
            <form method="post" action="<%= ctx %>/friends/remove" style="display:inline;">
              <input type="hidden" name="friendId" value="<%= friend.getId() %>">
              <button type="submit" class="btn-ghost"
                      style="color:var(--incorrect); border-color:var(--incorrect);"
                      onclick="return confirm('Remove this friend?');">Remove</button>
            </form>
          </td>
        </tr>
      <% } %>
    </table>
  <% } %>
</main>

<%@ include file="common/footer.jsp" %>
