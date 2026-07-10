<%@ page import="quizzy.web.WebEscape" %>
<%@ page import="quizzy.model.Quiz" %>
<%@ page import="java.util.List" %>
<%
    String pageTitle = "Browse quizzes · Quizzy";
    List<Quiz> quizzes = (List<Quiz>) request.getAttribute("quizzes");
%>
<%@ include file="common/head.jsp" %>
<%@ include file="common/header.jsp" %>

<main class="container" style="max-width: 900px; margin: 0 auto; padding: 2rem 0 3rem;">

  <section style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 1rem; margin-bottom: 1.5rem;">
    <h1 style="margin: 0;">Browse quizzes</h1>
    <% if (currentUser != null) { %>
      <a href="<%= ctx %>/quiz/create" class="btn-stamp" style="text-decoration:none; width:auto; padding-left:1.4rem; padding-right:1.4rem;">Create a quiz</a>
    <% } %>
  </section>

  <%-- simple client-side search --%>
  <input type="text" id="quizSearch" placeholder="Search by title..." autocomplete="off"
         style="display:block; width:100%; max-width:400px; margin-bottom:2rem;"
         oninput="filterQuizzes()">

  <% if (quizzes == null || quizzes.isEmpty()) { %>
    <p class="empty-state">
      No quizzes yet &mdash;
      <% if (currentUser != null) { %>
        <a href="<%= ctx %>/quiz/create">be the first to create one.</a>
      <% } else { %>
        check back soon.
      <% } %>
    </p>
  <% } else { %>
    <div class="quiz-grid" id="quizGrid">
      <% for (Quiz q : quizzes) { %>
        <a class="quiz-card" href="<%= ctx %>/quiz/summary?id=<%= q.getId() %>"
           data-title="<%= WebEscape.html(q.getTitle()).toLowerCase() %>"
           style="text-decoration:none; color:inherit;">
          <h3><%= WebEscape.html(q.getTitle()) %></h3>
          <p>
            by <%= WebEscape.html(q.getCreator().getUsername()) %>
            <% if (q.getDescription() != null && !q.getDescription().isEmpty()) { %>
              &middot; <%= WebEscape.html(q.getDescription().length() > 80 ? q.getDescription().substring(0, 80) + "…" : q.getDescription()) %>
            <% } %>
          </p>
          <div class="option-tags">
            <% if (q.isRandomQuestions()) { %><span class="option-tag">Random order</span><% } %>
            <% if (q.isOnePage()) { %><span class="option-tag">Single page</span><% } else { %><span class="option-tag">Multi-page</span><% } %>
            <% if (q.isImmediateCorrection()) { %><span class="option-tag">Immediate correction</span><% } %>
<%--            <% if (q.isPracticeMode()) { %><span class="option-tag">Practice mode</span><% } %>--%>
          </div>
        </a>
      <% } %>
    </div>
  <% } %>

</main>

<script>
  function filterQuizzes() {
    var query = document.getElementById('quizSearch').value.toLowerCase();
    var cards = document.querySelectorAll('#quizGrid .quiz-card');
    cards.forEach(function(card) {
      var title = card.getAttribute('data-title') || '';
      card.style.display = title.indexOf(query) !== -1 ? '' : 'none';
    });
  }
</script>

<%@ include file="common/footer.jsp" %>
