<%@ page import="quizzy.web.WebEscape" %>
<%@ page import="quizzy.web.DisplayUtil" %>
<%@ page import="quizzy.model.Announcement" %>
<%@ page import="quizzy.model.Quiz" %>
<%@ page import="quizzy.model.QuizAttempt" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="m4" uri="http://quizzy.freeuni.ge/tags/m4" %>
<%
  String pageTitle = "Home \u00B7 Quizzy";

  List<Announcement> announcements = (List<Announcement>) request.getAttribute("announcements");
  List<Quiz> popularQuizzes = (List<Quiz>) request.getAttribute("popularQuizzes");
  List<Quiz> recentQuizzes = (List<Quiz>) request.getAttribute("recentQuizzes");
  List<QuizAttempt> myRecentAttempts = (List<QuizAttempt>) request.getAttribute("myRecentAttempts");
  List<Quiz> myCreatedQuizzes = (List<Quiz>) request.getAttribute("myCreatedQuizzes");
  List<QuizAttempt> friendActivity = (List<QuizAttempt>) request.getAttribute("friendActivity");
  Integer unreadMessageCount = (Integer) request.getAttribute("unreadMessageCount");
  if (unreadMessageCount == null) { unreadMessageCount = 0; }

  DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, h:mm a");
%>
<%@ include file="common/head.jsp" %>
<%@ include file="common/header.jsp" %>

<main class="container home-grid">

  <div class="home-main">

    <%-- ---------------- announcements ---------------- --%>
    <% if (announcements != null && !announcements.isEmpty()) { %>
    <section class="panel">
      <h2>Announcements</h2>
      <% for (Announcement a : announcements) { %>
      <article class="announcement-card">
        <h3><%= WebEscape.html(a.getTitle()) %></h3>
        <p><%= WebEscape.html(a.getContent()) %></p>
        <time><%= a.getCreatedAt().format(fmt) %></time>
      </article>
      <% } %>
    </section>
    <% } %>

    <%-- ---------------- popular quizzes ---------------- --%>
    <section class="panel">
      <div class="panel-head">
        <h2>Popular quizzes</h2>
        <a class="section-link" href="<%= request.getContextPath() %>/quizzes">Browse all &rarr;</a>
      </div>
      <% if (popularQuizzes != null && !popularQuizzes.isEmpty()) { %>
      <div class="quiz-grid">
        <% for (Quiz q : popularQuizzes) { %>
        <a class="quiz-card" href="<%= request.getContextPath() %>/quiz/summary?id=<%= q.getId() %>">
          <h3><%= WebEscape.html(q.getTitle()) %></h3>
          <p><%= WebEscape.html(q.getDescription()) %></p>
        </a>
        <% } %>
      </div>
      <% } else { %>
      <p class="empty-state">No quizzes yet &mdash; be the first to create one.</p>
      <% } %>
    </section>

    <%-- ---------------- recently created quizzes ---------------- --%>
    <section class="panel">
      <h2>Recently created</h2>
      <% if (recentQuizzes != null && !recentQuizzes.isEmpty()) { %>
      <div class="quiz-grid">
        <% for (Quiz q : recentQuizzes) { %>
        <a class="quiz-card" href="<%= request.getContextPath() %>/quiz/summary?id=<%= q.getId() %>">
          <h3><%= WebEscape.html(q.getTitle()) %></h3>
          <p><%= WebEscape.html(q.getDescription()) %></p>
        </a>
        <% } %>
      </div>
      <% } else { %>
      <p class="empty-state">Nothing new yet. Check back soon.</p>
      <% } %>
    </section>

    <%-- ---------------- my recent quiz-taking activity ---------------- --%>
    <section class="panel">
      <h2>Your recent activity</h2>
      <% if (myRecentAttempts != null && !myRecentAttempts.isEmpty()) { %>
      <ul class="activity-list">
        <% for (QuizAttempt a : myRecentAttempts) { %>
        <li class="activity-item">
          <span>Took <strong><%= WebEscape.html(a.getQuiz().getTitle()) %></strong> &mdash; scored <%= a.getScore() %>/<%= a.getMaxScore() %></span>
          <span class="activity-meta"><%= DisplayUtil.formatTime(a.getTimeTakenSeconds()) %></span>
        </li>
        <% } %>
      </ul>
      <% } else { %>
      <p class="empty-state">You haven't taken a quiz yet. <a href="<%= request.getContextPath() %>/quizzes">Find one to try.</a></p>
      <% } %>
    </section>

    <%-- ---------------- my created quizzes ---------------- --%>
    <%-- Per spec: only show this section at all if the user has created something --%>
    <% if (myCreatedQuizzes != null && !myCreatedQuizzes.isEmpty()) { %>
    <section class="panel">
      <h2>Your quizzes</h2>
      <div class="quiz-grid">
        <% for (Quiz q : myCreatedQuizzes) { %>
        <a class="quiz-card" href="<%= request.getContextPath() %>/quiz/summary?id=<%= q.getId() %>">
          <h3><%= WebEscape.html(q.getTitle()) %></h3>
          <p><%= WebEscape.html(q.getDescription()) %></p>
        </a>
        <% } %>
      </div>
    </section>
    <% } %>

  </div>

  <aside class="home-sidebar">

    <%-- ---------------- messages ---------------- --%>
    <section class="panel">
      <div class="panel-head">
        <h2>Messages</h2>
        <a class="section-link" href="<%= request.getContextPath() %>/inbox">
          Inbox
          <% if (unreadMessageCount > 0) { %>
          <span class="badge-count"><%= unreadMessageCount %></span>
          <% } %>
        </a>
      </div>
      <%-- TODO(M2): show most-recent message previews (friend request / challenge / note) once mail system exists --%>
      <p class="empty-state">No new messages.</p>
    </section>

    <%-- ---------------- achievements ---------------- --%>
    <section class="panel">
      <div class="panel-head">
        <h2>Achievements</h2>
        <a class="section-link" href="<%= request.getContextPath() %>/profile">View profile &rarr;</a>
      </div>
      <% if (currentUser == null) { %>
      <p class="empty-state">Log in to start earning achievements.</p>
      <% } else { %>
      <div class="achievement-grid">
        <m4:achievements user="${currentUser}" />
      </div>
      <% } %>
    </section>

    <%-- ---------------- friends' recent activity ---------------- --%>
    <section class="panel">
      <h2>Friend activity</h2>
      <% if (friendActivity != null && !friendActivity.isEmpty()) { %>
      <ul class="activity-list">
        <% for (QuizAttempt a : friendActivity) { %>
        <li class="activity-item">
          <span><%= WebEscape.html(a.getUser().getUsername()) %> took <strong><%= WebEscape.html(a.getQuiz().getTitle()) %></strong></span>
          <span class="activity-meta"><%= a.getScore() %>/<%= a.getMaxScore() %></span>
        </li>
        <% } %>
      </ul>
      <% } else { %>
      <p class="empty-state">Add friends to see their activity here.</p>
      <% } %>
    </section>

  </aside>

</main>

<%@ include file="common/footer.jsp" %>
