<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="quizzy.web.SessionUtil" %>
<%
    // logged in -> home, otherwise -> login
    String target = SessionUtil.isLoggedIn(request) ? "/home" : "/login";
    response.sendRedirect(request.getContextPath() + target);
%>
