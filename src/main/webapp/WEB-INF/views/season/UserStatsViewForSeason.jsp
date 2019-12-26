<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="e" uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project" %>
<jsp:useBean id="UserDAO" class="org.bgtrack.models.user.daos.UserDAO" scope="session"/>

<%@ page import="org.bgtrack.models.SeasonStatus" %>

<!DOCTYPE html>
<html>
<head>
	<title>Season GG: Player Season Stats</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<jsp:include page="../../snippets/CommonIncludes.jspf" />
	<shiro:notAuthenticated>
		<meta http-equiv="Refresh" content="0; url=/">
	</shiro:notAuthenticated>
</head>
<body>
	
	<%@ include file="../../snippets/Nav.jspf" %>
	
	<div class="container content">
		<%@ include file="../../snippets/Header.jspf" %>

		<shiro:authenticated>

			<div class="row">
				<div class="col-12 mx-auto text-center mt-4">
					<p class="h1">
						<e:forHtml value="${UserSeasonStats.season.name}" />
					</p>
				</div>
				<div class="col-12 col-lg-7 mx-auto mt-4">
					<%@ include file="../../views/season/SeasonStandingsSnippet.jspf" %>
				</div>
				<div class="col-12 col-lg-5 mx-auto text-center mt-4">
					<h3>Breakdown By Place</h3>

					<table class="table table-hover table-primary text-center table-sm">
						<thead>
							<tr>
								<th scope="col">Place</th>
								<th scope="col">Finishes</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="placeOccurance" items="${UserSeasonStats.placeOccurances}" varStatus="index">
								<c:set var="index" value="${index}" />
								<tr>
									<td>${placeOccurance.key}</td>
									<td>${placeOccurance.value}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>

			<div class="row mt-3">
				<div class="col-12">
					<h3 class="text-center">${playerName}'s Round Results</h3>
				</div>
				
				<c:if test="${empty UserSeasonStats.season.getRounds()}">
					<div class="col-12 col-md-8 mx-auto text-center bg-warning mt-1">
						<p class="lead py-2">This user hasn't played any rounds yet.</p>
					</div>
				</c:if>
			</div>
				
			<div class="row mb-5">
				
				<c:set var="snippetListOfRounds" value="${UserSeasonStats.roundsUserParticipatedIn}" />
				<c:set var="snippetListofVictors" value="${UserSeasonStats.listofVictors}" />
				<c:set var="snippetSelectedUser" value="${UserSeasonStats.selectedUser}" />
				<%@ include file="./RoundResultsSnippet.jspf" %>
				
			</div>

		</shiro:authenticated>
		
	</div>
	
	<%@ include file="/WEB-INF/snippets/Footer.jspf" %>
	
</body>
</html>