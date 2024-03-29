<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="e" uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project" %>

<%@ include file="/WEB-INF/snippets/CommonJSTLIncludes.jspf" %>

<jsp:useBean id="UserDAO" class="org.bgtrack.models.user.daos.UserDAO" scope="session"/>
<jsp:useBean id="SeasonDAO" class="org.bgtrack.models.daos.SeasonDAO" scope="session"/>
<%@ page import="org.bgtrack.models.daos.RoundDAO" %>

<%@ page import="org.bgtrack.models.SeasonStatus" %>

<c:set var="usersInSeason" value="${SeasonDAO.getAllUsersInSeason(season.seasonId)}" />

<!DOCTYPE html>
<html>
<head>
	<title>Season GG: View Season</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<jsp:include page="../../snippets/CommonIncludes.jspf" />
	<script src="./resources/js/RoundController.js"></script>
	<script src="./resources/js/BGGAPIWrapper.js"></script>
	<script src="./resources/js/SeasonGameUtil.js"></script>
	<script src="./resources/js/PlayerCreatorController.js"></script>
</head>
<body> 
	
	<%@ include file="../../snippets/Nav.jspf" %>
	
	<div class="container content">
		<%@ include file="../../snippets/Header.jspf" %>
		
		<%@ include file="../../snippets/PopupMessage.jspf" %>

		<div class="row mt-2">
		
			<div class="col-12 mx-auto text-center">
				<p class="h3">
					<e:forHtml value="${season.name}" />
				</p>
			</div>
			
			<div class="col-12 mx-auto text-center">
				<p class="h4">
					<c:choose>
						<c:when test="${season.status == SeasonStatus.ACTIVE.toString()}">
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
			
			<hr class="horizontal-spacer">
			
			<div class="col-12 col-sm-6 mx-auto text-center verticle-line">
				<h5 id="season-game-name" data-bgg-id="${season.game.gameId}">${season.game.gameName}</h5>
				<img id="season-game-image" alt="season-game-${season.game.gameName}">
			</div>
			
			<div class="col-12 col-sm-6 mt-4 mt-sm-0 text-center mx-auto">
				<h5>Start Date: <fmt:formatDate pattern = "MM/dd/yyyy" value="${season.startDate}" /></h5>
				<h5>End Date: <fmt:formatDate pattern = "MM/dd/yyyy" value="${season.endDate}" /></h5>
				<h5>Total Rounds Played: ${season.rounds.size()}</h5>
				<h5>Total Players: ${usersInSeason.size()}</h5>
				<h5>
					Season Creator: <e:forHtml value="${season.creator.username}" />
				</h5>
				<h5>
					${scoringTypeFullText} Scoring
					<small>
						<a href="#;" data-toggle="modal" data-target="#scoringDescriptionModal">
							(What is this?)</a>
					</small>
				</h5>
				
				<div class="modal fade" id="scoringDescriptionModal" tabindex="-1" role="dialog"
					aria-labelledby="scoringDescriptionModalTitle" aria-hidden="true">
					<div class="modal-dialog modal-dialog-centered" role="document">
						<div class="modal-content">
							<div class="modal-header">
								<h5 class="modal-title text-primary">
									This season is using <strong>${scoringTypeFullText}</strong> scoring
								</h5>
								<button type="button" class="close" data-dismiss="modal" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
							</div>
							<div class="modal-body">
								<%@ include file="../../snippets/ScoringDescriptions.jspf" %>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
							</div>
						</div>
					</div>
				</div>

			</div>
			
			<div class="col-12 pt-xs-0 pt-sm-4">
				<div class="d-flex justify-content-center">
					<div class="btn-group">
						<button type="button" class="btn btn-secondary dropdown-toggle btn-sm" data-toggle="dropdown"
							aria-haspopup="true" aria-expanded="false">Season Options</button>
						<div class="dropdown-menu">
							<a class="dropdown-item" href="/viewPlayerSeasonStats?selectedSeasonId=${season.seasonId}&selectedUserId=${regUser.userId}">View my Stats for Season</a>
							<shiro:hasPermission name="season:seasonedit:${season.seasonId}">
								<a class="dropdown-item" data-toggle="modal" data-target="#edit-season-modal" href="#">Edit Season</a>
							</shiro:hasPermission>
							<shiro:hasPermission name="season:seasondelete:${season.seasonId}">
								<a class="dropdown-item" data-toggle="modal" data-target="#delete-season-modal" href="#">Delete Season</a>
							</shiro:hasPermission>
						</div>
					</div>
		
					<div class="modal" id="edit-season-modal" tabindex="-1" role="dialog" aria-labelledby="edit-season-modal" aria-hidden="true">
						<div class="modal-dialog modal-dialog" role="document">
							<div class="modal-content">
								<div class="modal-header">
									<h5 class="modal-title" id="exampleModalLabel">Season Edit</h5>
									<button type="button" class="close" data-dismiss="modal" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
								</div>
								<div class="modal-body">
									<label for="datepicker">New Season End Date</label>
									<form id="season-edit-form" action="/editSeason" method="POST">
										<input name="seasonId" value="${season.seasonId}" type="hidden">
										<input id="csrf-token" name="csrfToken" type="hidden" value="${csrfToken}">
										<input name="seasonEndDate" type="text" class="form-control my-2" id="datepicker" placeholder="Season End Date" autocomplete="off">
										<small id="season-end-date-help" class="form-text text-muted">
											Note that your current season end date cannot be before 
											this season's start date of <fmt:formatDate pattern = "MM/dd/yyyy" value="${season.startDate}" />.
										</small>
										<label for="new-season-name" class="mt-2">New Season Name</label>
										<input name="seasonName" type="text" class="form-control my-2" id="new-season-name" placeholder="New Season Name" autocomplete="off">
									</form>
								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
									<button type="submit" form="season-edit-form" class="btn btn-primary">Submit Edit(s)</button>
								</div>
							</div>
						</div>
					</div>
		
					<div class="modal" id="delete-season-modal" tabindex="-1" role="dialog" aria-labelledby="delete-season-modal" aria-hidden="true">
						<div class="modal-dialog modal-dialog-centered" role="document">
							<div class="modal-content">
								<div class="modal-header">
									<h5 class="modal-title" id="exampleModalLabel">Season Delete Confirm</h5>
									<button type="button" class="close" data-dismiss="modal" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
								</div>
								<div class="modal-body">Are you absolutely sure you wish to delete this season? This action <i>cannot</i> be undone.</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
									<form id="delete-season-form" action="deleteSeason">
										<input type="hidden" name="seasonId" value="${season.seasonId}">
										<input name="csrfToken" type="hidden" value="${csrfToken}">
										<button type="submit" class="btn btn-danger">Delete Season</button>
									</form>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<hr class="horizontal-spacer">
			
		</div>
		
		<div class="row">
			
			<c:if test="${!empty season.getRounds()}">
				<div class="col-12 col-lg-6 mx-auto text-center">
					<%@ include file="../../views/season/SeasonStandingsSnippet.jspf" %>
				</div>
			</c:if>

			<div class="col-12 col-lg-6 mx-auto text-center mt-4">
				<p class="h4">Add a Round</p>
		
				<form id="round-create-form" action="/createRound" method="POST" class="my-auto">
					<input name="seasonId" type="hidden" id="season-id-input" value="${season.getSeasonId()}">
					<input name="csrfToken" type="hidden" value="${csrfToken}">
					<div id="player-add-container-0" class="form-row justify-content-center">
					    
					    <div class="col-xs-7 col-lg-12 float-left">
						    <select id="recent-players-select-0" name="roundPlayer0" data-round-info="true" class="form-control">
								
								<c:choose>
									<c:when test="${empty usersInSeason}">
										
										<option value="default-text" selected disabled hidden>Search to Add More Players Below</option>
										<option value="${season.creator.userId}">
											<e:forHtml value="${season.creator.username}" />
										</option>
									
									</c:when>
									<c:otherwise>
										
										<option value="default-text" selected disabled hidden>Choose from Recent Players</option>
										<c:forEach items="${usersInSeason}" var="user">
											<option value="${user.getUserId()}">
												<e:forHtml value="${user.username}" />
											</option>
										</c:forEach>
										
									</c:otherwise>
								</c:choose>
								
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
							<span id="remove-player-button-0" class="d-none">
								<i class="text-danger clickable fas fa-minus-circle fa-lg"></i>
							</span>
						</div>

						<hr class="my-4 horizontal-spacer">
						
					</div>
					
					<div class="row">
					
						<div class="col-12">
							<p>
								Need to create a new player account for a friend? <a id="create-user-link" href="#;"
								data-toggle="modal" data-target="#create-user-modal"> Click here!</a>
							</p>
						</div>
					
						<shiro:notAuthenticated>
							<c:set var="isGuest" value="true" />
						</shiro:notAuthenticated>
						
						<shiro:lacksPermission name="season:createround:${season.seasonId}">
							<c:set var="lacksRoundCreatePermissions" value="true" />
						</shiro:lacksPermission>
						
						<c:choose>
							<c:when test="${season.status == SeasonStatus.INACTIVE.toString()}">
								<c:set var="roundCreateDisabled" value="true" />
								<c:set var="roundCreateDisabledText" value="Rounds can not be added for seasons which have ended." />
							</c:when>
							<c:when test="${isGuest}">
								<c:set var="roundCreateDisabled" value="true" />
								<c:set var="roundCreateDisabledText" value="You must be logged in to create a round." />
							</c:when>
							<c:when test="${lacksRoundCreatePermissions}">
								<c:set var="roundCreateDisabled" value="true" />
								<c:set var="roundCreateDisabledText" value="You must be the round creator or have played 
									in a round this season in order to create a round." />
							</c:when>
							<c:otherwise>
								<c:set var="roundCreateDisabled" value="false" />
							</c:otherwise>
						</c:choose>
					
						<div class="col-md-6 pt-3 ml-auto">
							
							<c:choose>
								<c:when test="${roundCreateDisabled eq false}">
									<button type="button" id="add-player-btn" class="btn btn-primary btn-block mb-2">Add Player</button>
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
								<c:when test="${roundCreateDisabled eq false}">
									<button id="round-create-btn" type="button" class="btn btn-success btn-block mb-2">Create Round</button>
								</c:when>
								<c:otherwise>
									<button id="round-create-btn" type="button" class="btn btn-secondary btn-block mb-2 disabled" disabled>Create Round</button>
									<p class="text-muted pl-2">${roundCreateDisabledText}</p>
								</c:otherwise>
							</c:choose>
							
						</div>
					</div>
				</form>
			</div>
			
		</div>

		<div class="modal fade" id="create-user-modal" tabindex="-1" role="dialog"
			aria-labelledby="exampleModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-dialog-centered" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="exampleModalLabel">Create a Player</h5>
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<div class="modal-body">
						
						<div id="player-create-success-container" class="d-none">
							
							<div class="alert alert-dismissible alert-success">
								<strong>Success!</strong> 
								Have your friend redeem their SeasonGG account so they can record rounds, 
								build their stats, and start seasons of their own. Provide your friend 
								with their account redemption code, where they can use the code 
								at the link below to redeem their account.
							</div>
							
							<p class="text-primary">
								Note: player accounts that you've created will always be listed under
								your <a href="/viewMyAccount" target="_blank">account</a> page. You can
								retrieve their account redemption code at any time.
							</p>
							
							<p id="player-create-name" class="text-primary mb-0"></p>
							
							<p id="player-create-redeem-code" class="text-info word-wrap"></p>
							
							<p>
								Have your friend redeem their account 
								<a id="player-create-redeem-link" href="" target="_blank">
									here
								</a>.
							</p>
							
							<p class="text-primary">You can also add another player below:</p>
							
							<hr class="horizontal-spacer">
							
						</div>
						
						<form id="create-guest-player-form" action="/createGuestPlayer" method="POST">
							<p>Enter the new player's information below:</p>
							<p id="player-create-err-msg" class="d-none text-danger"></p>
							<input name="username" type="text" class="form-control  my-2"
								id="player-create-username" placeholder="Username">
							<button id="create-player-btn" type="button" class="btn btn-primary">Create Player</button>
						</form>
						
						<div id="create-player-spinner" class="d-none justify-content-center">
							<div class="spinner-border" role="status">
								<span class="sr-only">Loading...</span>
							</div>
						</div>
						
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
					</div>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="col-12 mx-auto text-center">
				<p id="round-results-section" class="h4">Round Results</p>
				
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
		
			<c:set var="snippetListOfRounds" value="${paginatedRounds}" />
			<c:set var="snippetListofVictors" value="${listofVictors}" />
			<c:set var="snippetSelectedUser" value="${regUser}" />
			<c:set var="pageName" value="SeasonView" />
			<%@ include file="./RoundResultsSnippet.jspf" %>
						
			<c:set var="singleSeasonListLink" value="/viewSeason?seasonId=${season.seasonId}&roundPage=" />
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
		
		<div class="row mb-5">
			<c:import url="../../snippets/PaginationSnippet.jsp">
				<c:param name="numElements" value="${season.rounds.size()}"/>
				<c:param name="numElementsPerPage" value="${numElementsPerPage}"/>
				<c:param name="currentPage" value="${roundPage}"/>
				<c:param name="pageNumberLink" value="${singleSeasonListLink}"/>
			</c:import>
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
								<th scope="col">Username</th>
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

	</div>

	<%@ include file="/WEB-INF/snippets/Footer.jspf" %>

</body>
</html>