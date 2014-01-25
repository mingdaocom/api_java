<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.mingdao.sdk.*"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'receive.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body>
  	<%String code = request.getParameter("code");
  	  if(code!=null){//使用的code方式
  	  	String a=Config.getAccessTokenByCode(code);
  	  	System.out.print(a);
  	  	out.println("result="+a);
  	  }else{//使用的token方式
  	  	String access_token = request.getParameter("access_token");
  	  	String expires_in = request.getParameter("expires_in");
  	  	String refresh_token = request.getParameter("refresh_token");
  	  	out.println("access_token="+access_token);
  	  	out.println("<br>");
  	  	out.println("expires_in="+expires_in);
  	  	out.println("<br>");
  	  	out.println("refresh_token="+refresh_token);
  	  }
  
  
   %>
	</body>
</html>
