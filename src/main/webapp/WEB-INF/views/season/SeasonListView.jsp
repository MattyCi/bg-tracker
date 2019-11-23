<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
	<title>Season GG: Compete in Seasons against your Friends</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<jsp:include page="/WEB-INF/snippets/CommonIncludes.jspf" />
	<shiro:notAuthenticated>
		<meta http-equiv="Refresh" content="0; url=/">
	</shiro:notAuthenticated>
</head>
<body>
	<%@ include file="/WEB-INF/snippets/Nav.jspf" %>
	
	<div class="container">
		<%@ include file="/WEB-INF/snippets/Header.jspf" %>
		
		<shiro:authenticated>
			
			<c:choose>
				<c:when test="${param.view eq 'usersSeasonsList'}">
					<c:set var="seasonListHeaderText" value="Your Seasons" />
					<c:set var="seasonListLinkText" value="View All Seasons" />
					<c:set var="seasonListLink" value="/viewSeasonList?view=allSeasonsList" />
					<c:set var="seasonsList" value="${SeasonDAO.getAllSeasonsUserIsIn()}" />
					<c:set var="noSeasonsText" value="You aren't apart of any seasons yet. Start your own by completing the form below." />
				</c:when>
				<c:otherwise>
					<c:set var="seasonListHeaderText" value="All Seasons" />
					<c:set var="seasonListLinkText" value="View Your Seasons Only" />
					<c:set var="seasonListLink" value="/viewSeasonList?view=usersSeasonsList" />
					<c:set var="seasonsList" value="${SeasonDAO.getAllSeasons()}" />
					<c:set var="noSeasonsText" value="No seasons have been created yet, start your own by completing the form below." />
				</c:otherwise>
			</c:choose>
			
			<div id="your-seasons" class="row pt-4">
				<div class="col-12 text-center">
					<h1 class="display-4">${seasonListHeaderText}</h1>
				</div>
				<div class="col-12 text-right">
					<a href="${seasonListLink}" class="text-secondary">${seasonListLinkText}</a>
				</div>
			
				<c:choose>
					<c:when test="${empty seasonsList}">
						<div class="col-12 col-md-6 mx-auto pt-2">
							<div class="alert alert-danger">
							  	<p><strong>Uh oh!</strong> ${noSeasonsText}</p>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<%@ include file="../season/SeasonListSnippet.jspf" %>
					</c:otherwise>
				</c:choose>
			</div>
					
			</shiro:authenticated>
		
	</div>
</body>
</html>