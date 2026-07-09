<%@ page import="quizzy.web.WebEscape" %>
<%
    String pageTitle = "Create account \u00B7 Quizzy";
    String errorMsg = (String) request.getAttribute("error");
    String prevUsername = (String) request.getAttribute("username");
%>
<%@ include file="common/head.jsp" %>
<%@ include file="common/header.jsp" %>

<main class="auth-shell">
    <div class="ticket-card">
        <div class="ticket-card__stub">
            <p class="ticket-eyebrow">No. 002 &middot; New Member</p>
            <h1 class="ticket-title">Join Quizzy</h1>
        </div>
        <div class="ticket-card__perforation"></div>
        <div class="ticket-card__body">

            <% if (errorMsg != null) { %>
            <div class="alert-error"><%= WebEscape.html(errorMsg) %></div>
            <% } %>

            <form method="post" action="<%= request.getContextPath() %>/register" class="stack-form">
                <div class="field">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" required autofocus
                           value="<%= WebEscape.html(prevUsername) %>">
                </div>

                <div class="field">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required>
                    <span class="field-hint">Pick something you haven't used elsewhere.</span>
                </div>

                <div class="field">
                    <label for="confirmPassword">Confirm password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required>
                </div>

                <button type="submit" class="btn-stamp">Create account</button>
            </form>

            <p class="auth-switch">
                Already have an account? <a href="<%= request.getContextPath() %>/login">Log in</a>
            </p>
        </div>
    </div>
</main>

<%@ include file="common/footer.jsp" %>
