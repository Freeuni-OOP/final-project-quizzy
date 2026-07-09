<%@ page import="quizzy.web.WebEscape" %>
<%@ page import="quizzy.model.Message" %>
<%@ page import="quizzy.model.MessageType" %>
<%@ page import="quizzy.web.InboxItem" %>
<%@ page import="java.util.List" %>
<%
    String pageTitle = "Inbox · Quizzy";
    List<InboxItem> items = (List<InboxItem>) request.getAttribute("items");
%>
<%@ include file="common/head.jsp" %>
<%@ include file="common/header.jsp" %>

<main class="container" style="max-width: 700px; margin: 0 auto; padding: 2rem 0 3rem;">
  <h1>Inbox</h1>
  <% if (items == null || items.isEmpty()) { %>
    <p class="empty-state">Your inbox is empty.</p>
  <% } else { %>
    <div class="panel" style="padding:0;">
      <% for (InboxItem item : items) {
           Message m = item.getMessage();
           String typeLabel;
           switch (m.getType()) {
               case FRIEND_REQUEST: typeLabel = "Friend request"; break;
               case CHALLENGE:      typeLabel = "Challenge"; break;
               default:             typeLabel = "Note";
           }
      %>
        <div style="display:flex; align-items:center; justify-content:space-between;
                    gap:1rem; padding:1rem 1.4rem; border-bottom:1px solid var(--line);">
          <div>
            <span class="option-tag" style="margin-right:0.5rem;"><%= typeLabel %></span>
            <strong>@<%= WebEscape.html(item.getSenderName()) %></strong>
            <div class="muted" style="font-size:0.88rem; margin-top:0.25rem;">
              <%= WebEscape.html(m.getBody()) %>
            </div>
            <% if (m.getType() == MessageType.CHALLENGE && m.getQuizId() != null) { %>
              <div style="margin-top:0.35rem;">
                <a href="<%= ctx %>/quiz/summary?id=<%= m.getQuizId() %>" class="btn-stamp"
                   style="display:inline-block; width:auto; padding:0.3rem 0.8rem; font-size:0.82rem; text-decoration:none;">
                  Take challenge #<%= m.getQuizId() %>
                </a>
              </div>
            <% } %>
          </div>
          <div style="display:flex; gap:0.4rem; flex-shrink:0;">
            <% if (m.getType() == MessageType.FRIEND_REQUEST) { %>
              <form method="post" action="<%= ctx %>/friends/respond" style="display:inline;">
                <input type="hidden" name="requesterId" value="<%= m.getSenderId() %>">
                <input type="hidden" name="action" value="accept">
                <button type="submit" class="btn-stamp"
                        style="width:auto; padding:0.3rem 0.8rem; font-size:0.82rem;">Accept</button>
              </form>
              <form method="post" action="<%= ctx %>/friends/respond" style="display:inline;">
                <input type="hidden" name="requesterId" value="<%= m.getSenderId() %>">
                <input type="hidden" name="action" value="decline">
                <button type="submit" class="btn-ghost"
                        style="font-size:0.82rem; padding:0.25rem 0.6rem;">Decline</button>
              </form>
            <% } %>
            <form method="post" action="<%= ctx %>/inbox" style="display:inline;">
              <input type="hidden" name="messageId" value="<%= m.getId() %>">
              <button type="submit" class="btn-remove"
                      style="width:24px;height:24px;border-radius:50%;border:1.5px solid var(--incorrect);color:var(--incorrect);background:none;cursor:pointer;font-size:1rem;line-height:1;"
                      title="Delete">&times;</button>
            </form>
          </div>
        </div>
      <% } %>
    </div>
  <% } %>
</main>

<%@ include file="common/footer.jsp" %>
