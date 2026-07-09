<%@ page import="quizzy.web.WebEscape" %>
<%
  String pageTitle = "Log in \u00B7 Quizzy";
  String nextParam = request.getParameter("next");
  String errorMsg = (String) request.getAttribute("error");
  String prevUsername = (String) request.getAttribute("username");
  // AdminAuthorizationFilter sets this when blocking non-admin access
  if (errorMsg == null) {
    errorMsg = (String) session.getAttribute("loginError");
    if (errorMsg != null) session.removeAttribute("loginError");
  }
%>
<%@ include file="common/head.jsp" %>
<%@ include file="common/header.jsp" %>

<main class="auth-shell">
  <div class="ticket-card">
    <div class="ticket-card__stub">
      <p class="ticket-eyebrow">No. 001 &middot; Member Login</p>
      <h1 class="ticket-title">Welcome back</h1>
    </div>
    <div class="ticket-card__perforation"></div>
    <div class="ticket-card__body">

      <% if (errorMsg != null) { %>
      <div class="alert-error"><%= WebEscape.html(errorMsg) %></div>
      <% } %>

      <form method="post" action="<%= request.getContextPath() %>/login" class="stack-form">
        <input type="hidden" name="next" value="<%= WebEscape.html(nextParam) %>">

        <div class="field">
          <label for="username">Username</label>
          <input type="text" id="username" name="username" required autofocus
                 value="<%= WebEscape.html(prevUsername) %>">
        </div>

        <div class="field">
          <label for="password">Password</label>
          <input type="password" id="password" name="password" required>
        </div>

        <button type="submit" class="btn-stamp">Log in</button>
      </form>

      <p class="auth-switch">
        New to Quizzy? <a href="<%= request.getContextPath() %>/register">Create an account</a>
      </p>
    </div>
  </div>
</main>

<%@ include file="common/footer.jsp" %>
