<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="UserDAO" class="org.bgtrack.models.user.daos.UserDAO" scope="session"/>
<jsp:useBean id="SeasonDAO" class="org.bgtrack.models.daos.SeasonDAO" scope="session"/>

<c:set var="usersInSeason" value="${SeasonDAO.getAllUsersInSeason(season.seasonId)}" />

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
				</div>

			</div>
				
				<div class="d-flex justify-content-center">
				
				<div class="p-2">
					<div class="btn-group">
						<button type="button" class="btn btn-secondary dropdown-toggle" data-toggle="dropdown"
							aria-haspopup="true" aria-expanded="false">Season Options</button>
						<div class="dropdown-menu">
							<a class="dropdown-item" href="deleteSeason?seasonId=${season.seasonId}">Delete Season</a> 
						</div>
					</div>
				</div>
				
			</div>

			<div class="row">
				
				<hr style="width: 100%; color: #EEEEEE; height: 1px; background-color: #EEEEEE;">
				
				<div class="col-12 mx-auto text-center pb-3">
					<h3>Season Info</h3>
					<p class="h3">
						<c:choose>
							<c:when test="${season.status == 'A'}">
								<span class="badge badge-success">Season Active</span>
							</c:when>
							<c:otherwise>
								<span class="badge badge-danger">Season Ended</span>
							</c:otherwise>
						</c:choose>
					</p>
				</div>
				
				<div class="col-12 col-sm-6 mx-auto text-center verticle-line">
					<h4>Start Date: <fmt:formatDate pattern = "MM/dd/yyyy" value="${season.startDate}" /></h4>
					<h4>End Date: <fmt:formatDate pattern = "MM/dd/yyyy" value="${season.endDate}" /></h4>
					<h4>Season Game: ${season.game.gameName}</h4>
				</div>
				
				<div class="col-12 col-sm-6 mx-auto text-center">
					<h4>Total Rounds Played: ${season.rounds.size()}</h4>
					<h4>Total Players: ${usersInSeason.size()}</h4>
					<h4>Season Creator: ${season.creator.firstName} ${season.creator.lastName}</h4>
				</div>
				
				<hr style="width: 100%; ; height: 1px; background-color: #EEEEEE;">
				
			</div>
			
			<div class="row">
				
				<c:if test="${!empty season.getRounds()}">
					<div class="col-12 col-lg-6 mx-auto text-center mt-4">
						<%@ include file="../../views/season/SeasonStandingsSnippet.jspf" %>
					</div>
				</c:if>

				<div class="col-12 col-lg-6 mx-auto text-center mt-4">
					<h3 class="mb-3">Add a Round</h3>
			
					<form id="round-create-form" action="/createRound" method="POST" class="my-auto">
						<input name="seasonId" type="hidden" id="season-id-input" value="${season.getSeasonId()}">
						<div id="player-add-container-0" class="form-row justify-content-center">
						    
						    <div class="col-xs-7 col-lg-12 float-left">
							    <select id="recent-players-select-0" name="roundPlayer0" data-round-info="true" class="form-control">
									<option value="" selected disabled hidden>Choose from Recent Players</option>
									<c:forEach items="${usersInSeason}" var="user">
										<option value="${user.getUserId()}">${user.getFirstName()} ${user.getLastName()}</option>
									</c:forEach>
								</select> 
							</div>

							<div class="col-8 col-lg-8 my-3 float-left">
								<input id="player-search-input-0" class="form-control" type="text" placeholder="Search for Player">
							</div>
							<div class="col-4 col-lg-4 my-3 float-left">
								<button id="player-search-btn-0" class="btn btn-block btn-secondary" type="button">
									<i class="fas fa-search"></i>
								</button>
							</div>
							
							<div class="col-xs-4 col-lg-12 mt-2 mt-sm-0 text-left">
								<label for="round-place-select" class="float-left">Choose Player's Place:</label>
								<select id="player-place-select-0" name="playerPlace0" data-round-info="true" class="form-control">
									<c:forEach begin="1" end="12" varStatus="loop">
										<option value="${loop.index}">${loop.index}</option>
									</c:forEach>
								</select> 
							</div>
							<div class="col pt-4">
								<i id="remove-player-button-0" class="text-danger fas fa-minus-circle fa-lg d-none" style="cursor: pointer;"></i>
							</div>

							<hr class="my-4" style="width: 100%; color: #EEEEEE; height: 1px; background-color: #EEEEEE;">
							
						</div>
						
						<div class="row">
							<div class="col-md-6 pt-3 ml-auto">
								
								<c:choose>
									<c:when test="${season.status == 'A'}">
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
									<c:when test="${season.status == 'A'}">
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

			<div id="player-search-results-popup" class="popup d-none">

				<div class="popup-content">
					
					<button id="player-search-results-popup-close-btn" type="button" class="close">&times;</button>
					
					<p class="text-center">Player Search Results </br> <small id="player-search-results-popup-status-text" class="text-muted">Loading player search results...</small></p>

					<div id="player-search-results-popup-spinner" class="d-flex justify-content-center">
						<div class="spinner-border text-center" role="status">
							<span class="sr-only">Loading player search results...</span>
						</div>
					</div>
					
					<div id="player-search-results-list" class="d-none">

						<table id="player-search-results-list-table" class="table table-striped table-hover">
							<thead>
								<tr class="table-primary">
									<th scope="col">First Name</th>
									<th scope="col">Last Name</th>
								</tr>
							</thead>
							<tbody id="player-search-results-list-table-body">
							</tbody>
						</table>

					</div>
					
					<div id="player-search-error" class="row d-none">
						<div id="player-search-error-text" class="col-10 alert alert-danger mx-auto"></div>
					</div>

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

		</shiro:authenticated>

	</div>
</body>
</html>