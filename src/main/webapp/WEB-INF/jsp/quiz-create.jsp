<%@ page import="quizzy.web.WebEscape" %>
<%
    String pageTitle = "Create quiz · Quizzy";
    String errorMsg = (String) request.getAttribute("error");
%>
<%@ include file="common/head.jsp" %>
<%@ include file="common/header.jsp" %>

<main class="container" style="max-width: 760px; margin: 0 auto; padding: 2rem 0 3rem;">

  <h1>Create a quiz</h1>

  <% if (errorMsg != null) { %>
    <div class="alert-error" style="margin-bottom:1.5rem;"><%= WebEscape.html(errorMsg) %></div>
  <% } %>

  <form method="post" action="<%= ctx %>/quiz/create" class="stack-form" id="quizForm">

    <%-- --------------- quiz metadata --------------- --%>
    <div class="field">
      <label for="title">Title</label>
      <input type="text" id="title" name="title" required autofocus
             placeholder="e.g. World Capitals Challenge" maxlength="100">
    </div>

    <div class="field">
      <label for="description">Description</label>
      <textarea id="description" name="description" rows="3"
                placeholder="Tell people what this quiz is about..."></textarea>
    </div>

    <div class="field">
      <label>Options</label>
      <div class="toggle-row">
        <label class="toggle-chip">
          <input type="checkbox" name="randomQuestions"> Random order
        </label>
        <label class="toggle-chip">
          <input type="checkbox" name="onePage" checked> Single page
        </label>
        <label class="toggle-chip">
          <input type="checkbox" name="immediateCorrection"> Immediate correction
        </label>
<%--        <label class="toggle-chip">--%>
<%--          <input type="checkbox" name="practiceMode"> Practice mode available--%>
<%--        </label>--%>
      </div>
    </div>

    <%-- --------------- questions --------------- --%>
    <div class="field" style="margin-top: 1.5rem;">
      <label>Questions</label>
    </div>

    <div id="questionsContainer">
      <%-- JavaScript populates question cards here --%>
    </div>

    <button type="button" class="btn-add" onclick="addQuestion()" style="margin-bottom: 1.5rem;">
      + Add question
    </button>

    <button type="submit" class="btn-stamp">Create quiz</button>

  </form>
</main>

<script>
  var questionCount = 0;

  function addQuestion() {
    questionCount++;
    var container = document.getElementById('questionsContainer');

    var card = document.createElement('div');
    card.className = 'question-card';
    card.id = 'qCard' + questionCount;
    card.innerHTML =
      '<div class="question-card__head">'
        + '<h3>Question ' + questionCount + '</h3>'
        + '<button type="button" class="btn-remove" onclick="removeQuestion(\'qCard' + questionCount + '\')" title="Remove">&times;</button>'
      + '</div>'

      + '<div class="field">'
        + '<label>Type</label>'
        + '<select name="questionType" onchange="toggleQuestionFields(this, \'qBody' + questionCount + '\')">'
          + '<option value="TEXT">Text</option>'
          + '<option value="FILL_BLANK">Fill in the blank</option>'
          + '<option value="MULTIPLE_CHOICE">Multiple choice</option>'
          + '<option value="PICTURE_RESPONSE">Picture response</option>'
        + '</select>'
      + '</div>'

      + '<div class="field">'
        + '<label>Prompt</label>'
        + '<textarea name="questionPrompt" rows="2" placeholder="The question text..." required></textarea>'
      + '</div>'

      + '<div id="qBody' + questionCount + '">'
        + '<div class="field">'
          + '<label>Correct answer(s)</label>'
          + '<input type="text" name="correctAnswer" placeholder="For multiple answers, separate with |">'
          + '<span class="field-hint">Use | to separate multiple correct answers (e.g. "color|colour")</span>'
        + '</div>'
      + '</div>';

    container.appendChild(card);

    /* Add the image URL field for picture questions right away */
    var body = document.getElementById('qBody' + questionCount);
    var picDiv = document.createElement('div');
    picDiv.className = 'field';
    picDiv.id = 'qPic' + questionCount;
    picDiv.style.display = 'none';
    picDiv.innerHTML =
      '<label>Image URL</label>'
      + '<input type="text" name="imageUrl" placeholder="https://...">';
    body.appendChild(picDiv);

    /* Choice grid for multiple choice */
    var choiceDiv = document.createElement('div');
    choiceDiv.className = 'field';
    choiceDiv.id = 'qChoices' + questionCount;
    choiceDiv.style.display = 'none';
    choiceDiv.innerHTML =
      '<label>Choices</label>'
      + '<div class="choice-grid">'
        + '<input type="text" name="choiceA" placeholder="Option A">'
        + '<input type="text" name="choiceB" placeholder="Option B">'
        + '<input type="text" name="choiceC" placeholder="Option C">'
        + '<input type="text" name="choiceD" placeholder="Option D">'
      + '</div>';
    body.appendChild(choiceDiv);
  }

  function removeQuestion(cardId) {
    var card = document.getElementById(cardId);
    if (card) card.remove();
  }

  function toggleQuestionFields(select, bodyId) {
    var body = document.getElementById(bodyId);
    var type = select.value;
    var picDiv = body.querySelector('[id^="qPic"]');
    var choiceDiv = body.querySelector('[id^="qChoices"]');

    if (picDiv) picDiv.style.display = type === 'PICTURE_RESPONSE' ? '' : 'none';
    if (choiceDiv) choiceDiv.style.display = type === 'MULTIPLE_CHOICE' ? '' : 'none';
  }

  /* Start with one empty question card. */
  document.addEventListener('DOMContentLoaded', function() {
    addQuestion();
  });
</script>

<%@ include file="common/footer.jsp" %>
