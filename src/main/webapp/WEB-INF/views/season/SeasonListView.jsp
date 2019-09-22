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
					<div id="your-seasons" class="row pt-4">
						<div class="col-12 text-center">
							<h1 class="display-4">Your Seasons</h1>
						</div>
						<div class="col-12 text-right">
							<a href="/viewSeasonList?view=allSeasonsList" class="text-secondary">View All Seasons</a>
						</div>
					
						<c:set var="yourSeasonsList" value="${SeasonDAO.getAllSeasonsUserIsIn()}" />
						<c:choose>
							<c:when test="${empty yourSeasonsList}">
								<div class="col-12 col-md-6 mx-auto">
									<div class="alert alert-dismissible alert-danger">
									  	<p><strong>Uh oh!</strong>  You aren't apart of any seasons yet. Try to join one by clicking "All Seasons".</p>
									</div>
								</div>
							</c:when>
							<c:otherwise>
								<%@ include file="../season/YourSeasonView.jsp" %>
							</c:otherwise>
						</c:choose>
					</div>
				</c:when>
				<c:otherwise>
					<div id="all-seasons" class="row pt-4">
						<div class="col-12 text-center">
							<h1 class="display-4">All Seasons</h1>
						</div>
						<div class="col-12 text-right">
							<a href="/viewSeasonList?view=usersSeasonsList" class="text-secondary">View Your Seasons Only</a>
						</div>
						
						<c:set var="allSeasonsList" value="${SeasonDAO.getAllSeasons()}" />
						<div class="col-12 col-md-6 mx-auto">
							<c:if test="${empty allSeasonsList}">
								<div class="alert alert-dismissible alert-danger text-center">
									<p>
										<strong>Uh oh!</strong> There aren't any seasons yet. Try to create one first.
									</p>
								</div>
							</c:if>
						</div>
						<c:if test="${not empty allSeasonsList}">
							<%@ include file="../season/AllSeasonView.jsp" %>
						</c:if>
					</div>
				</c:otherwise>
			</c:choose>
		</shiro:authenticated>
		
	</div>
</body>
</html>