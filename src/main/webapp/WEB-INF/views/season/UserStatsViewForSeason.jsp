<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="e" uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project" %>

<%@ include file="/WEB-INF/snippets/CommonJSTLIncludes.jspf" %>

<jsp:useBean id="UserDAO" class="org.bgtrack.models.user.daos.UserDAO" scope="session"/>

<%@ page import="org.bgtrack.models.SeasonStatus" %>

<!DOCTYPE html>
<html>
<head>
	<title>Season GG: Player Season Stats</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<jsp:include page="../../snippets/CommonIncludes.jspf" />

</head>
<body>
	
	<%@ include file="../../snippets/Nav.jspf" %>
	
	<div class="container content">
		<%@ include file="../../snippets/Header.jspf" %>

		<div class="row">
			<div class="col-12 mt-4">
				<ol class="breadcrumb">
					<li class="breadcrumb-item"><a href="viewSeason?seasonId=${UserSeasonStats.season.seasonId}"><e:forHtml value="${UserSeasonStats.season.name}" /></a></li>
					<li class="breadcrumb-item active">User Season Stats</li>
				</ol>
			</div>
			<div class="col-12 mx-auto text-center">
				<p class="h2">
					<e:forHtml value="${UserSeasonStats.season.name}" />
				</p>
			</div>
			<div class="col-12 col-lg-7 mx-auto mt-4">
				<%@ include file="../../views/season/SeasonStandingsSnippet.jspf" %>
			</div>
			<div class="col-12 col-lg-5 mx-auto text-center mt-4">
				<p class="h4">Breakdown By Place</p>

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
				<p class="h4 text-center">${playerName}'s Round Results</p>
			</div>
			
			<c:if test="${empty UserSeasonStats.season.getRounds()}">
				<div class="col-12 col-md-8 mx-auto text-center bg-warning mt-1">
					<p class="lead py-2">This user hasn't played any rounds yet.</p>
				</div>
			</c:if>
		</div>
			
		<div class="row mb-5">
			<div class="col-12">
				<p class="small mb-0">
					<i class="fas fa-exclamation-circle text-warning"></i> Only showing round results that ${playerName} participated in for this season.
				</p>
			</div>
			
			<c:set var="snippetListOfRounds" value="${UserSeasonStats.paginatedRounds}" />
			<c:set var="snippetListofVictors" value="${UserSeasonStats.listofVictors}" />
			<c:set var="snippetSelectedUser" value="${UserSeasonStats.selectedUser}" />
			<%@ include file="./RoundResultsSnippet.jspf" %>
			
		</div>
		
		<c:choose>
			<c:when test="${empty param.roundPage}">
				<c:set var="roundPage" value="0" />
			</c:when>
			<c:otherwise>
				<c:set var="roundPage" value="${param.roundPage}" />
			</c:otherwise>
		</c:choose>
		
		<fmt:bundle basename="round">
			<fmt:message var="numElementsPerPage" key="NUM_ROUNDS_PER_PAGE" />
		</fmt:bundle>
		
		<c:set var="singleUserSeasonStatsListLink" value="/viewPlayerSeasonStats?selectedSeasonId=${UserSeasonStats.season.seasonId}&selectedUserId=${UserSeasonStats.selectedUser.userId}&roundPage=" />
		
		<div class="row mb-5">
			<c:import url="../../snippets/PaginationSnippet.jsp">
				<c:param name="numElements" value="${UserSeasonStats.seasonStandingList[0].getGamesPlayed()}"/>
				<c:param name="numElementsPerPage" value="${numElementsPerPage}"/>
				<c:param name="currentPage" value="${roundPage}"/>
				<c:param name="pageNumberLink" value="${singleUserSeasonStatsListLink}"/>
			</c:import>
		</div>	
		
	</div>
	
	<%@ include file="/WEB-INF/snippets/Footer.jspf" %>
	
</body>
</html>