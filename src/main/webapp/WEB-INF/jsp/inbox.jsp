<%@ include file="common/header.jspf" %>
<%@ page import="java.util.List" %>
<%@ page import="quizzy.model.Message" %>
<%@ page import="quizzy.model.MessageType" %>
<%@ page import="quizzy.web.InboxItem" %>
<%
    List<InboxItem> items = (List<InboxItem>) request.getAttribute("items");
%>
<div class="card">
    <h1>Inbox</h1>
    <% if (items == null || items.isEmpty()) { %>
        <p class="muted">Your inbox is empty.</p>
    <% } else { %>
        <ul class="list">
            <% for (InboxItem item : items) {
                   Message m = item.getMessage();
                   String typeLabel;
                   switch (m.getType()) {
                       case FRIEND_REQUEST: typeLabel = "Friend request"; break;
                       case CHALLENGE:      typeLabel = "Challenge"; break;
                       default:             typeLabel = "Note";
                   }
            %>
                <li>
                    <div class="grow">
                        <div>
                            <span class="tag"><%= typeLabel %></span>
                            <strong>@<%= WebEscape.html(item.getSenderName()) %></strong>
                        </div>
                        <div class="muted"><%= WebEscape.html(m.getBody()) %></div>
                        <% if (m.getType() == MessageType.CHALLENGE && m.getQuizId() != null) { %>
                            <div class="muted">Challenge quiz #<%= m.getQuizId() %></div>
                        <% } %>
                    </div>

                    <% if (m.getType() == MessageType.FRIEND_REQUEST) { %>
                        <form class="inline-form" method="post" action="<%= ctx %>/friends/respond">
                            <input type="hidden" name="requesterId" value="<%= m.getSenderId() %>">
                            <input type="hidden" name="action" value="accept">
                            <button type="submit" class="btn btn-small">Accept</button>
                        </form>
                        <form class="inline-form" method="post" action="<%= ctx %>/friends/respond">
                            <input type="hidden" name="requesterId" value="<%= m.getSenderId() %>">
                            <input type="hidden" name="action" value="decline">
                            <button type="submit" class="btn btn-small btn-secondary">Decline</button>
                        </form>
                    <% } %>

                    <form class="inline-form" method="post" action="<%= ctx %>/inbox">
                        <input type="hidden" name="messageId" value="<%= m.getId() %>">
                        <button type="submit" class="btn btn-small btn-danger">Delete</button>
                    </form>
                </li>
            <% } %>
        </ul>
    <% } %>
</div>
<%@ include file="common/footer.jspf" %>
