<%@ include file="common/header.jspf" %>
<%@ page import="java.util.List" %>
<%@ page import="quizzy.model.User" %>
<%@ page import="quizzy.model.AttemptSummary" %>
<%@ page import="quizzy.model.Achievement" %>
<%
    User profileUser = (User) request.getAttribute("profileUser");
    List<AttemptSummary> attempts = (List<AttemptSummary>) request.getAttribute("attempts");
    List<Achievement> achievements = (List<Achievement>) request.getAttribute("achievements");
    String friendState = (String) request.getAttribute("friendState");
%>
<div class="card">
    <h1>@<%= WebEscape.html(profileUser.getUsername()) %></h1>

    <% if ("NONE".equals(friendState)) { %>
        <form class="inline-form" method="post" action="<%= ctx %>/friends/request">
            <input type="hidden" name="targetId" value="<%= profileUser.getId() %>">
            <button type="submit" class="btn">Add friend</button>
        </form>
    <% } else if ("PENDING_OUT".equals(friendState)) { %>
        <span class="tag">Friend request pending</span>
    <% } else if ("PENDING_IN".equals(friendState)) { %>
        <span class="tag">Wants to be your friend</span>
        <a class="btn btn-small" href="<%= ctx %>/inbox">Respond in inbox</a>
    <% } else if ("FRIENDS".equals(friendState)) { %>
        <span class="tag">You are friends</span>
        <form class="inline-form" method="post" action="<%= ctx %>/friends/remove">
            <input type="hidden" name="friendId" value="<%= profileUser.getId() %>">
            <button type="submit" class="btn btn-small btn-danger">Remove friend</button>
        </form>
    <% } %>
</div>

<div class="card">
    <h2>Quiz history</h2>
    <% if (attempts == null || attempts.isEmpty()) { %>
        <p class="muted">No quizzes taken yet.</p>
    <% } else { %>
        <table>
            <thead>
                <tr><th>Quiz</th><th>Score</th><th>Time</th></tr>
            </thead>
            <tbody>
                <% for (AttemptSummary a : attempts) { %>
                    <tr>
                        <td><%= WebEscape.html(a.getQuizTitle()) %></td>
                        <td><%= a.getScore() %> / <%= a.getMaxScore() %></td>
                        <td><%= a.getTimeTakenSeconds() %>s</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    <% } %>
</div>

<div class="card">
    <h2>Achievements</h2>
    <% if (achievements == null || achievements.isEmpty()) { %>
        <p class="muted">No achievements earned yet.</p>
    <% } else { %>
        <ul class="list">
            <% for (Achievement ach : achievements) { %>
                <li>
                    <span class="grow">
                        <strong><%= WebEscape.html(ach.getName()) %></strong>
                        <div class="muted"><%= WebEscape.html(ach.getDescription()) %></div>
                    </span>
                </li>
            <% } %>
        </ul>
    <% } %>
</div>
<%@ include file="common/footer.jspf" %>
