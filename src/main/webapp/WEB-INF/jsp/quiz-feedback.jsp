<%@ page import="quizzy.model.Quiz" %>
<%@ page import="quizzy.web.WebEscape" %>
<%
    String pageTitle = "Answer feedback \u00B7 Quizzy";

    Quiz quiz = (Quiz) request.getAttribute("quiz");
    Boolean wasCorrect = (Boolean) request.getAttribute("wasCorrect");
    String correctAnswerText = (String) request.getAttribute("correctAnswerText");
%>
<%@ include file="common/head.jsp" %>
<%@ include file="common/header.jsp" %>

<main class="container">
  <div class="feedback-card <%= (wasCorrect != null && wasCorrect) ? "feedback-card--correct" : "feedback-card--incorrect" %>">
    <div class="feedback-stamp"><%= (wasCorrect != null && wasCorrect) ? "CORRECT" : "INCORRECT" %></div>
    <% if (wasCorrect == null || !wasCorrect) { %>
      <p>Correct answer: <strong><%= WebEscape.html(correctAnswerText) %></strong></p>
    <% } %>
    <a class="btn-stamp" style="display:inline-block; width:auto; padding-left:1.6rem; padding-right:1.6rem; text-decoration:none; margin-top: 1rem;"
       href="<%= request.getContextPath() %>/quiz/take?id=<%= quiz.getId() %>">Continue</a>
  </div>
</main>

<%@ include file="common/footer.jsp" %>
