<%@ include file="common/header.jspf" %>
<%@ page import="java.util.List" %>
<%@ page import="quizzy.model.User" %>
<%
    String query = (String) request.getAttribute("query");
    List<User> results = (List<User>) request.getAttribute("results");
%>
<div class="card">
    <h1>Find users</h1>
    <form method="get" action="<%= ctx %>/search">
        <input type="search" name="q" placeholder="Search by username"
               value="<%= WebEscape.html(query) %>" autofocus>
        <button type="submit" class="btn" style="margin-top:0.75rem;">Search</button>
    </form>
</div>

<% if (query != null && !query.trim().isEmpty()) { %>
<div class="card">
    <h2>Results for &ldquo;<%= WebEscape.html(query) %>&rdquo;</h2>
    <% if (results == null || results.isEmpty()) { %>
        <p class="muted">No users matched.</p>
    <% } else { %>
        <ul class="list">
            <% for (User u : results) { %>
                <li>
                    <span class="grow">@<%= WebEscape.html(u.getUsername()) %></span>
                    <a class="btn btn-small btn-secondary" href="<%= ctx %>/profile?id=<%= u.getId() %>">Profile</a>
                    <form class="inline-form" method="post" action="<%= ctx %>/friends/request">
                        <input type="hidden" name="targetId" value="<%= u.getId() %>">
                        <button type="submit" class="btn btn-small">Add friend</button>
                    </form>
                </li>
            <% } %>
        </ul>
    <% } %>
</div>
<% } %>
<%@ include file="common/footer.jspf" %>
