<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
	<head>
		<title>Season GG: Compete in Seasons Against your Friends</title>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		
		<jsp:include page="WEB-INF/snippets/CommonIncludes.jspf" />
		
	</head>
	<body>
		
		<%@ include file="WEB-INF/snippets/Nav.jspf" %>
	
		<div class="container content">
			
			<c:set var="servletErrorPage" value="true" />
			<%@ include file="WEB-INF/snippets/Header.jspf" %>
			
			<div class="col text-center mt-4">
			
				<h5 class="text-muted">Please click <a href="/" alt="Home Page">here</a> to return to the homepage.</h5>
				
			</div>
		</div>
		
		<%@ include file="/WEB-INF/snippets/Footer.jspf" %>
		
	</body>
</html>