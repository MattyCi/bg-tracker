<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="UserDAO" class="org.bgtrack.models.user.daos.UserDAO" scope="session"/>
<jsp:useBean id="SeasonDAO" class="org.bgtrack.models.daos.SeasonDAO" scope="session"/>

<!DOCTYPE html>
<html>
<head>
	<title>Season GG: View Season</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<jsp:include page="../../snippets/CommonIncludes.jspf" />
	<shiro:notAuthenticated>
		<meta http-equiv="Refresh" content="0; url=/">
	</shiro:notAuthenticated>
</head>
<body>

	<%@ include file="../../snippets/Nav.jspf" %>
	
	<div class="container">
		<%@ include file="../../snippets/Header.jspf" %>
		
		<shiro:authenticated>

			<div class="row mt-4">
				<div class="col-12 mx-auto text-center">
					<p class="h1">${season.getName()}</p>
					<p class="h3">
						<c:choose>
							<c:when test="${seasonStatus}">
								<span class="badge badge-success">Season Active</span>
							</c:when>
							<c:otherwise>
								<span class="badge badge-danger">Season Ended</span>
							</c:otherwise>
						</c:choose>
					</p>
				</div>
			</div>
						
			<div class="row">
				
				<hr style="width: 100%; color: #EEEEEE; height: 1px; background-color: #EEEEEE;">
				
				<div class="col-12 mx-auto text-center pb-3">
					<h3>Season Info</h3>
				</div>
				
				<div class="col-12 col-sm-6 mx-auto text-center verticle-line">
					<h4>Start Date: <fmt:formatDate pattern = "MM/dd/yyyy" value="${season.startDate}" /></h4>
					<h4>End Date: <fmt:formatDate pattern = "MM/dd/yyyy" value="${season.endDate}" /></h4>
					<h4>Season Game: ${season.game.gameName}</h4>
				</div>
				
				<div class="col-12 col-sm-6 mx-auto text-center">
					<h4>Total Rounds Played: ${season.rounds.size()}</h4>
					<h4>Total Players: ${SeasonDAO.getAllUsersInSeason(season.seasonId).size()}</h4>
					<h4>Season Creator: ${season.creator.firstName} ${season.creator.lastName}</h4>
				</div>
				
				<hr style="width: 100%; ; height: 1px; background-color: #EEEEEE;">
				
			</div>
			
			<div class="row">
				
				<div class="col-12 col-lg-6 mx-auto text-center mt-4">
					<%@ include file="../../views/season/SeasonStandingsSnippet.jspf" %>
				</div>
			
			
				<div class="col-12 col-lg-6 mx-auto text-center mt-4">
					<h3 class="mb-3">Add a Round</h3>
			
					<form id="round-create-form" action="/createRound" method="POST" class="my-auto">
						<input name="seasonId" type="hidden" id="season-id-input" value="${season.getSeasonId()}">
						<div id="player-add-container-0" class="form-row justify-content-center">
						    
						    <div class="col-sm-7 col-lg-8 float-left">
							    <select name="roundPlayer0" data-round-info="true" class="form-control" id="round-player-select">
									<option value="" selected disabled hidden>Choose Player</option>
									<c:forEach items="${UserDAO.getAllUsers()}" var="user">
										<option value="${user.getUserId()}">${user.getFirstName()} ${user.getLastName()}</option>
									</c:forEach>
								</select> 
							</div>
							<div class="col-sm-4 col-lg-3 mt-2 mt-sm-0 float-left">
								<input name="playerPlace0" data-round-info="true" type="number" class="form-control" id="round-player-place-input" placeholder="Place" value="1">
							</div>
							<div class="col-sm-1 col-lg-1">
								<i id="remove-player-button-0" class="text-danger fas fa-minus-circle fa-lg i-middle d-none" style="cursor: pointer;"></i>
							</div>
							
							<hr style="width: 100%; color: #EEEEEE; height: 1px; background-color: #EEEEEE;">
							
						</div>
						
						<div class="row">
							<div class="col-md-6 pt-3 ml-auto">
								
								<c:choose>
									<c:when test="${seasonStatus}">
										<button type="button" class="btn btn-primary btn-block mb-2" onClick="SeasonController.addPlayerInputToCreateRoundForm();">Add Player</button>
									</c:when>
									<c:otherwise>
										<button type="button" class="btn btn-primary btn-block mb-2 disabled" disabled>Add Player</button>
									</c:otherwise>
								</c:choose>
								
							</div>
						</div>
						<div class="row">
							<div class="col-md-6 py-4 ml-auto">
							
								<c:choose>
									<c:when test="${seasonStatus}">
										<button id="round-create-btn" type="button" class="btn btn-success btn-block mb-2">Create Round</button>
									</c:when>
									<c:otherwise>
										<button id="round-create-btn" type="button" class="btn btn-secondary btn-block mb-2 disabled" disabled>Create Round</button>
										<p class="text-muted text-small pl-2">Rounds can not be added for seasons which have ended.</p>
									</c:otherwise>
								</c:choose>
								
							</div>
						</div>
					</form>
				</div>
			</div>
			
			<div id="round-create-confirm-popup" class="popup d-none">
				<div class="popup-content">
    				<p class="text-center">Round Create Confirmation </br> <small class="text-muted">Please verify the below results are correct.</small></p>
					<table id="round-create-confirm-popup-table" class="table table-sm">
						<thead>
							<tr class="table-primary">
								<th scope="col">Player</th>
								<th scope="col">Place</th>
							</tr>
						</thead>
						<tbody id="round-create-confirm-popup-table-body">
						</tbody>
					</table>
					
					<div class="row">
						<div class="col-md-6 mx-auto pt-4">
							<button id="round-create-confirm-btn" type="button" class="btn btn-success btn-block mb-2">Submit Round Creation</button>
						</div>
						<div class="col-md-6 mx-auto pt-4">
							<button id="round-create-cancel-btn" type="button" class="btn btn-danger btn-block mb-2">Cancel</button>
						</div>
					</div>
					
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 mx-auto text-center mt-4">
					<h3>Round Results</h3>
					
					<c:if test="${empty season.getRounds()}">
						<div class="col-12 col-md-8 mx-auto text-center bg-warning mt-1">
							<p class="lead py-2">
								This season does not have any rounds created yet. Play a game and add the round results above!
							</p>
						</div>
					</c:if>
				</div>
			</div>
			
			<div class="row mb-5">
				<c:set var="snippetListOfRounds" value="${season.rounds}" />
				<c:set var="snippetListofVictors" value="${listofVictors}" />
				<c:set var="snippetSelectedUser" value="${regUser}" />
				<c:set var="pageName" value="SeasonView" />
				<%@ include file="./RoundResultsSnippet.jspf" %>
			</div>

		</shiro:authenticated>

	</div>
</body>
</html>