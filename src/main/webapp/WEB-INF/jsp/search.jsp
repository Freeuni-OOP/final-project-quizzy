<%@ page import="quizzy.web.WebEscape" %>
<%@ page import="quizzy.model.User" %>
<%@ page import="java.util.List" %>
<%
    String pageTitle = "Find users · Quizzy";
    String query = (String) request.getAttribute("query");
    List<User> results = (List<User>) request.getAttribute("results");
%>
<%@ include file="common/head.jsp" %>
<%@ include file="common/header.jsp" %>

<main class="container" style="max-width: 600px; margin: 0 auto; padding: 2rem 0 3rem;">

  <section class="panel">
    <h1>Find users</h1>
    <form method="get" action="<%= ctx %>/search" style="display:flex; gap:0.75rem; align-items:flex-end;">
      <div class="field" style="flex:1; margin:0;">
        <label for="q">Username</label>
        <input type="search" id="q" name="q" placeholder="Search by username"
               value="<%= WebEscape.html(query) %>" autofocus>
      </div>
      <button type="submit" class="btn-stamp" style="width:auto; padding:0.5rem 1.2rem;">Search</button>
    </form>
  </section>

  <% if (query != null && !query.trim().isEmpty()) { %>
    <section class="panel">
      <h2>Results for &ldquo;<%= WebEscape.html(query) %>&rdquo;</h2>
      <% if (results == null || results.isEmpty()) { %>
        <p class="empty-state">No users matched.</p>
      <% } else { %>
        <table class="leaderboard-table" style="width:100%;">
          <% for (User u : results) { %>
            <tr>
              <td>
                <a href="<%= ctx %>/profile?id=<%= u.getId() %>">
                  @<%= WebEscape.html(u.getUsername()) %>
                </a>
              </td>
              <td style="text-align:right;">
                <form method="post" action="<%= ctx %>/friends/request" style="display:inline;">
                  <input type="hidden" name="targetId" value="<%= u.getId() %>">
                  <button type="submit" class="btn-stamp"
                          style="width:auto; padding:0.3rem 0.8rem; font-size:0.85rem;">Add friend</button>
                </form>
              </td>
            </tr>
          <% } %>
        </table>
      <% } %>
    </section>
  <% } %>

</main>

<%@ include file="common/footer.jsp" %>
