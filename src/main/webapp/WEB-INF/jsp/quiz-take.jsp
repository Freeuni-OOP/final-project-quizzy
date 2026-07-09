<%@ page import="quizzy.web.WebEscape" %>
<%@ page import="quizzy.model.Quiz" %>
<%@ page import="quizzy.model.question.Question" %>
<%@ page import="quizzy.model.question.QuestionType" %>
<%@ page import="quizzy.model.question.PictureQuestion" %>
<%@ page import="quizzy.model.question.MultipleChoiceQuestion" %>
<%@ page import="java.util.List" %>
<%
    String pageTitle = "Take quiz \u00B7 Quizzy";

    Quiz quiz = (Quiz) request.getAttribute("quiz");
    Boolean noQuestions = (Boolean) request.getAttribute("noQuestions");
    Boolean practiceObj = (Boolean) request.getAttribute("practice");
    boolean practice = practiceObj != null && practiceObj;

    List<Question> orderedQuestions = (List<Question>) request.getAttribute("orderedQuestions");
    Question currentQuestion = (Question) request.getAttribute("currentQuestion");
    Integer questionNumber = (Integer) request.getAttribute("questionNumber");
    Integer totalQuestions = (Integer) request.getAttribute("totalQuestions");
%>
<%@ include file="common/head.jsp" %>
<%@ include file="common/header.jsp" %>

<main class="container" style="max-width: 700px; margin: 0 auto; padding: 2rem 0 3rem;">

  <% if (quiz == null) { %>
    <p class="empty-state">This quiz couldn't be found.</p>

  <% } else if (noQuestions != null && noQuestions) { %>
    <p class="empty-state">This quiz doesn't have any questions yet.</p>

  <% } else { %>

    <h1><%= WebEscape.html(quiz.getTitle()) %><% if (practice) { %> <span class="option-tag">Practice mode</span><% } %></h1>

    <form method="post" action="<%= request.getContextPath() %>/quiz/submit">
      <input type="hidden" name="quizId" value="<%= quiz.getId() %>">
      <input type="hidden" name="practice" value="<%= practice %>">

      <% if (quiz.isOnePage()) { %>
        <%-- ---------------- one-page mode: every question on one form ---------------- --%>
        <% for (Question q : orderedQuestions) {
             QuestionType qType = q.getType();
        %>
          <div class="one-page-question">
            <% if (qType == QuestionType.PICTURE_RESPONSE) { %>
              <img class="question-image" src="<%= WebEscape.html(((PictureQuestion) q).getImageUrl()) %>" alt="">
            <% } %>
            <p class="question-prompt"><%= WebEscape.html(q.getPrompt()) %></p>

            <% if (qType == QuestionType.MULTIPLE_CHOICE) { %>
              <div class="option-list">
                <% for (String choice : ((MultipleChoiceQuestion) q).getChoices()) { %>
                  <label class="option-row">
                    <input type="radio" name="answer_<%= q.getId() %>" value="<%= WebEscape.html(choice) %>" required>
                    <span><%= WebEscape.html(choice) %></span>
                  </label>
                <% } %>
              </div>
            <% } else { %>
              <input type="text" name="answer_<%= q.getId() %>" required placeholder="Your answer">
            <% } %>
          </div>
        <% } %>

        <button type="submit" class="btn-stamp">Submit quiz</button>

      <% } else { %>
        <%-- ---------------- multi-page mode: one question per page ---------------- --%>
        <p class="progress-line">Question <%= questionNumber %> of <%= totalQuestions %></p>
        <div class="progress-track">
          <div class="progress-fill" style="width: <%= (int) (100.0 * questionNumber / totalQuestions) %>%;"></div>
        </div>

        <%
             QuestionType qType = currentQuestion.getType();
        %>
        <% if (qType == QuestionType.PICTURE_RESPONSE) { %>
          <img class="question-image" src="<%= WebEscape.html(((PictureQuestion) currentQuestion).getImageUrl()) %>" alt="">
        <% } %>
        <p class="question-prompt"><%= WebEscape.html(currentQuestion.getPrompt()) %></p>

        <% if (qType == QuestionType.MULTIPLE_CHOICE) { %>
          <div class="option-list">
            <% for (String choice : ((MultipleChoiceQuestion) currentQuestion).getChoices()) { %>
              <label class="option-row">
                <input type="radio" name="answer_<%= currentQuestion.getId() %>" value="<%= WebEscape.html(choice) %>" required>
                <span><%= WebEscape.html(choice) %></span>
              </label>
            <% } %>
          </div>
        <% } else { %>
          <input type="text" name="answer_<%= currentQuestion.getId() %>" required placeholder="Your answer" style="margin-bottom: 1.5rem;">
        <% } %>

        <button type="submit" class="btn-stamp">
          <%= (questionNumber != null && totalQuestions != null && questionNumber.intValue() == totalQuestions.intValue())
                ? "Finish quiz" : "Next question" %>
        </button>
      <% } %>

    </form>

  <% } %>

</main>

<%@ include file="common/footer.jsp" %>
