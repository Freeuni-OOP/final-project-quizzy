<%@ include file="common/header.jspf" %>
<%
    String regError = (String) request.getAttribute("error");
    String prevUsername = (String) request.getAttribute("username");
%>
<div class="card auth-card">
    <h1>Create account</h1>
    <% if (regError != null) { %>
        <p class="error"><%= WebEscape.html(regError) %></p>
    <% } %>
    <form method="post" action="<%= ctx %>/register" class="stack">
        <label for="username">Username</label>
        <input type="text" id="username" name="username" autofocus
               value="<%= WebEscape.html(prevUsername) %>" required>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" required>

        <label for="confirmPassword">Confirm password</label>
        <input type="password" id="confirmPassword" name="confirmPassword" required>

        <button type="submit" class="btn" style="margin-top:1rem;">Register</button>
    </form>
    <p class="muted center" style="margin-top:1rem;">
        Already have an account? <a href="<%= ctx %>/login">Log in</a>
    </p>
</div>
<%@ include file="common/footer.jspf" %>
