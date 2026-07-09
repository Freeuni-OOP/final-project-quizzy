<%-- Static include, goes right after common/head.jsp --%>
<%@ page import="quizzy.web.SessionUtils" %>
<%@ page import="quizzy.web.WebEscape" %>
<%@ page import="quizzy.model.User" %>
<%
    User currentUser = SessionUtil.current(request);
    String ctx = request.getContextPath();
    String uri = request.getRequestURI();
%>
<header class="site-header">
  <div class="container site-header__row">
    <a class="brand" href="<%= ctx %>/home">
      <svg class="brand-seal" width="34" height="34" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
        <circle cx="20" cy="20" r="18" stroke="currentColor" stroke-width="2.5" stroke-dasharray="4 3"/>
        <path d="M12 20.5L17.5 26L28 14" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <span class="brand-word">Quizzy</span>
    </a>

    <nav class="folder-tabs" aria-label="Primary">
      <a href="<%= ctx %>/home" class="<%= uri.endsWith("/home") ? "active" : "" %>">Home</a>
      <%-- TODO(M3/M1): update once the browse/create/take-quiz servlets exist --%>
      <a href="<%= ctx %>/quizzes" class="<%= uri.endsWith("/quizzes") ? "active" : "" %>">Browse</a>
      <a href="<%= ctx %>/quiz/create" class="<%= uri.contains("/quiz/create") ? "active" : "" %>">Create</a>
      <a href="<%= ctx %>/friends" class="<%= uri.contains("/friends") ? "active" : "" %>">Friends</a>
      <a href="<%= ctx %>/inbox" class="<%= uri.endsWith("/inbox") ? "active" : "" %>">Inbox</a>
      <% if (currentUser != null && currentUser.isAdmin()) { %>
        <a href="<%= ctx %>/admin/" class="<%= uri.contains("/admin") ? "active" : "" %>">Admin</a>
      <% } %>
    </nav>

    <div class="site-header__account">
      <% if (currentUser != null) { %>
        <span class="account-name"><%= WebEscape.html(currentUser.getUsername()) %></span>
        <a class="btn-ghost" href="<%= ctx %>/logout">Log out</a>
      <% } else { %>
        <a class="btn-ghost" href="<%= ctx %>/login">Log in</a>
        <a class="btn-ghost btn-ghost--accent" href="<%= ctx %>/register">Register</a>
      <% } %>
    </div>
  </div>
</header>

<%
    String flashMessage = SessionUtils.consumeFlash(request);
%>
<% if (flashMessage != null) { %>
  <div class="container">
    <div class="flash-banner"><%= WebEscape.html(flashMessage) %></div>
  </div>
<% } %>
