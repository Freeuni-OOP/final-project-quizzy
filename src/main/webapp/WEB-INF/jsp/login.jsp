<%@ include file="common/header.jspf" %>
<%
    String loginError = (String) request.getAttribute("error");
    String prevUsername = (String) request.getAttribute("username");
    String next = request.getParameter("next");
%>
<div class="card auth-card">
    <h1>Log in</h1>
    <% if (loginError != null) { %>
        <p class="error"><%= WebEscape.html(loginError) %></p>
    <% } %>
    <form method="post" action="<%= ctx %>/login" class="stack">
        <% if (next != null) { %>
            <input type="hidden" name="next" value="<%= WebEscape.html(next) %>">
        <% } %>
        <label for="username">Username</label>
        <input type="text" id="username" name="username" autofocus
               value="<%= WebEscape.html(prevUsername) %>" required>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" required>

        <button type="submit" class="btn" style="margin-top:1rem;">Log in</button>
    </form>
    <p class="muted center" style="margin-top:1rem;">
        No account? <a href="<%= ctx %>/register">Register</a>
    </p>
</div>
<%@ include file="common/footer.jspf" %>
