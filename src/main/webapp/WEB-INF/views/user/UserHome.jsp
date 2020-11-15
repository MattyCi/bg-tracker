<jsp:useBean id="GameDAO" class="org.bgtrack.models.daos.GameDAO" scope="session"/>
<jsp:useBean id="SeasonDAO" class="org.bgtrack.models.daos.SeasonDAO" scope="session"/>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="e" uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project" %>

<%@ include file="../../snippets/PopupMessage.jspf" %>

<%@ page import="org.bgtrack.models.ScoringType" %>

<div class="row pt-4">

	<div class="col-12 col-md-10 col-lg-8 bg-primary mx-auto py-2 mb-4 text-center text-white">
		<h1 class="display-4">View Seasons</h1>
		<p class="lead">
			View seasons which are currently in progress or have already finished.
		</p>
	</div>

	<div class="col-12 col-md-6 py-2 mx-auto">
		<a href="/viewSeasonList?view=usersSeasonsList" class="list-group-item list-group-item-action flex-column align-items-start full-height
			bg-dark text-white">
			<div class="d-flex w-100 justify-content-between">
				<h5 class="mb-1">Your Seasons</h5>
			</div>
			<p class="mb-1">See the seasons which you are a part of.</p>
		</a>
	</div>
	<div class="col-12 col-md-6 py-2 mx-auto">
		<a href="/viewSeasonList?view=allSeasonsList" class="list-group-item list-group-item-action flex-column align-items-start h-100
			bg-light">
			<div class="d-flex w-100 justify-content-between">
				<h5 class="mb-1">All Seasons</h5>
			</div>
		</a>
	</div>
	
</div>

<div class="row pt-4">
	
	<div class="col-12 col-md-10 col-lg-8 bg-primary mx-auto py-2 text-center text-white">
		<h1 class="display-4">Start a New Season</h1>
		<p class="lead">
			Create a season below to begin competing against your friends.
		</p>
	</div>
	
	<div class="col-12 col-md-10 col-lg-8 mx-auto my-2">
		<form id="seasonCreateForm" action="/createSeason" method="POST">
			<input name="csrfToken" type="hidden" value="${csrfToken}">
			
			<div class="form-group">
				<label for="season-create-name" class="lead">Start off by giving your season a name.</label>
				<input name="seasonName" type="text" class="form-control my-2" id="season-create-name" placeholder="Season Name" autocomplete="off"> 

				<label for="game-search-input" class="lead">
					Next, search for a game to be played throughout this season.
				</label>
				<small class="form-text text-muted">Please note that you cannot change this later.</small>

				<div class="input-group mb-3">
					<input id="game-search-input" class="form-control" type="text" placeholder="Search for a Game">
					<div class="input-group-append">
						<button id="game-search-button" class="btn btn-block btn-secondary" type="button">
							<i class="fas fa-search"></i>
						</button>
					</div>
				</div>

				<p class="small p-0 m-0">
					<i class="fas fa-power-off text-danger"></i> <a href="https://boardgamegeek.com/" target="_blank">Powered by Board Game Geek</a>
				</p>
				<div id="bgg-container" class="bg-light p-4 text-center border border-primary scrollable">
					<div id="bgg-spinner" class="spinner-border text-primary" role="status">
						<span class="sr-only">Loading...</span>
					</div>
					<div id="no-results-container" class="col-12 d-none">
						<h3 class="text-danger">No results found...</h3>
					</div>
					<div id="bgg-search-results" class="row d-none">
						<!-- BGG Results -->
					</div>
				</div>
				<div id="selected-game-container" class="col-12 m-0 py-4 d-none text-center border border-primary">
					<h4>Selected Game</h4>
					<h5 id="selected-game-name"></h5>
					<img id="selected-game-img">
				</div>
				
				<input id="selected-bgg-game-id" name="seasonGameId" type="hidden">
				<input id="selected-bgg-game-name" name="seasonGameName" type="hidden">
			
				<label for="datepicker" class="lead">
					Now, select a date for the season to end. Once the season ends, the player
					in first place becomes the winning champion for that season.
				</label>
				<small class="form-text text-muted">The season end date CAN be changed later.</small>
				<input name="seasonEndDate" type="text" class="form-control my-2" id="datepicker" placeholder="Season End Date" autocomplete="off">

				<label for="season-scoring-select" class="lead">
					Lastly, choose a season scoring type.
				</label>
				
				<%@ include file="../../snippets/ScoringDescriptions.jspf" %>
				
				<label for="season-scoring-select" class="lead">
					Choose scoring type below.
				</label>
				<select name="seasonScoringType" class="form-control" id="season-scoring-select">
					<option value="${ScoringType.AVERAGED.toString()}">Averaged Scoring</option>
					<option value="${ScoringType.HANDICAPPED.toString()}">Handicapped Scoring</option>
				</select>
			</div>
			
			<button type="submit" class="btn btn-success btn-lg btn-block my-4">Create Season</button>
		</form>
	</div>
	
</div>

<script>
    var picker = new Pikaday({
    	field: document.getElementById('datepicker'),
    	minDate: new Date()
    });
</script>
