<%@ include file="common/header.jspf" %>
<div class="card">
    <h1>Welcome, <%= WebEscape.html(headerUser.getUsername()) %></h1>
    <p class="muted">What would you like to do?</p>
    <div class="stack" style="margin-top:1rem;">
        <a class="btn" href="<%= ctx %>/search">Find users</a>
        <a class="btn btn-secondary" href="<%= ctx %>/friends">My friends</a>
        <a class="btn btn-secondary" href="<%= ctx %>/inbox">Inbox</a>
        <a class="btn btn-secondary" href="<%= ctx %>/profile">My profile</a>
    </div>
</div>
<%@ include file="common/footer.jspf" %>
