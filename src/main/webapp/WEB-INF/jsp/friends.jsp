<%@ include file="common/header.jspf" %>
<%@ page import="java.util.List" %>
<%@ page import="quizzy.model.User" %>
<%
    List<User> friends = (List<User>) request.getAttribute("friends");
%>
<div class="card">
    <h1>Friends</h1>
    <% if (friends == null || friends.isEmpty()) { %>
        <p class="muted">You have no friends yet. Try <a href="<%= ctx %>/search">finding some users</a>.</p>
    <% } else { %>
        <ul class="list">
            <% for (User friend : friends) { %>
                <li>
                    <span class="grow">@<%= WebEscape.html(friend.getUsername()) %></span>
                    <a class="btn btn-small btn-secondary" href="<%= ctx %>/profile?id=<%= friend.getId() %>">Profile</a>
                    <form class="inline-form" method="post" action="<%= ctx %>/friends/remove">
                        <input type="hidden" name="friendId" value="<%= friend.getId() %>">
                        <button type="submit" class="btn btn-small btn-danger">Remove</button>
                    </form>
                </li>
            <% } %>
        </ul>
    <% } %>
</div>
<%@ include file="common/footer.jspf" %>
