<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="e" uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project" %>

<%@ page import="org.bgtrack.models.daos.SeasonDAO" %>

<!DOCTYPE html>
<html>
<head>
	<title>Season GG: Compete in Seasons Against your Friends</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<jsp:include page="/WEB-INF/snippets/CommonIncludes.jspf" />
</head>
<body>
	<%@ include file="/WEB-INF/snippets/Nav.jspf" %>
	
	<div class="container content">
		<%@ include file="/WEB-INF/snippets/Header.jspf" %>
		
		<c:choose>
			<c:when test="${empty param.seasonPage}">
				<c:set var="seasonPage" value="0" />
			</c:when>
			<c:otherwise>
				<c:set var="seasonPage" value="${param.seasonPage}" />
			</c:otherwise>
		</c:choose>

		<c:choose>
			<c:when test="${param.view eq 'usersSeasonsList'}">
				<c:set var="seasonListHeaderText" value="Your Seasons" />
				<c:set var="seasonListLinkText" value="View All Seasons" />
				<c:set var="seasonListLink" value="/viewSeasonList?view=usersSeasonsList" />
				<c:set var="seasonsList" value="${SeasonDAO.getPaginatedSeasonList(seasonPage, true)}" />
				<c:set var="numSeasons" value="${SeasonDAO.getCountOfAllSeasonsUserIsIn()}" />
				<c:set var="noSeasonsText" value="You aren't apart of any seasons yet." />
				<c:set var="viewParam" value="usersSeasonsList" />
			</c:when>
			<c:otherwise>
				<c:set var="seasonListHeaderText" value="All Seasons" />
				<c:set var="seasonListLinkText" value="View Your Seasons Only" />
				<c:set var="seasonListLink" value="/viewSeasonList?view=allSeasonsList" />
				<c:set var="seasonsList" value="${SeasonDAO.getPaginatedSeasonList(seasonPage, false)}" />
				<c:set var="numSeasons" value="${SeasonDAO.getCountOfAllSeasons()}" />
				<c:set var="noSeasonsText" value="No seasons have been created yet." />
				<c:set var="viewParam" value="allSeasonsList" />
			</c:otherwise>
		</c:choose>
		
		<div id="your-seasons" class="row pt-4">
			<div class="col-12 text-center">
				<h1 class="display-4">${seasonListHeaderText}</h1>
			</div>
			<shiro:authenticated>
				<div class="col-12 text-right">
					<a href="${seasonListLink}" class="text-secondary">${seasonListLinkText}</a>
				</div>
			</shiro:authenticated>
		
			<c:choose>
				<c:when test="${empty seasonsList}">
					<div class="col-12 col-md-6 mx-auto pt-2">
						<div class="alert alert-danger">
						  	<p><strong>Uh oh!</strong> ${noSeasonsText} Start your own by clicking <a href="/">here</a>.</p>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<%@ include file="../season/SeasonListSnippet.jspf" %>
				</c:otherwise>
			</c:choose>
		</div>
		
	</div>
	
	<%@ include file="/WEB-INF/snippets/Footer.jspf" %>
	
</body>
</html>