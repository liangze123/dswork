<%@page language="java" contentType="text/html;charset=UTF-8" import="dswork.web.*,
common.auth.*"%><%
String path = request.getContextPath();
Auth model = AuthLogin.getLoginUser(request, response);
String url = "login.html";//-1后台用户
AuthLogin.logout(request);
if(model != null)
{
	if(model.isEnterprise())
	{
		url = "loginEp.html";
	}
	else if(model.isUser())
	{
		url = "loginPerson.html";
	}
}
response.sendRedirect(url);
%>