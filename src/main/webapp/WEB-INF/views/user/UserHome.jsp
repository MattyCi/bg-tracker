<jsp:useBean id="GameDAO" class="org.bgtrack.models.daos.GameDAO" scope="session"/>
<jsp:useBean id="SeasonDAO" class="org.bgtrack.models.daos.SeasonDAO" scope="session"/>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="e" uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project" %>

<%@ include file="../../snippets/PopupMessage.jspf" %>

<div class="row pt-2">

	<div class="col-12 col-md-6 mx-auto">
		<a href="/viewSeasonList?view=usersSeasonsList" class="list-group-item list-group-item-action flex-column align-items-start active full-height">
			<div class="d-flex w-100 justify-content-between">
				<h5 class="mb-1">Your Seasons</h5>
			</div>
			<p class="mb-1">See the seasons which you are a part of only.</p>
		</a>
	</div>
	<div class="col-12 col-md-6 mx-auto">
		<a href="/viewSeasonList?view=allSeasonsList" class="list-group-item list-group-item-action flex-column align-items-start">
			<div class="d-flex w-100 justify-content-between">
				<h5 class="mb-1">All Seasons</h5>
			</div>
			<p class="mb-1">See all seasons being tracked currently. This includes seasons you are
				currently not a part of.</p>
		</a>
	</div>
	
	<div class="col-12 col-md-6 mx-auto my-2">
		<h3 class="text-center">Start a new Season</h3>
		<form id="seasonCreateForm" action="/createSeason" method="POST">
			<input name="seasonName" type="text" class="form-control my-2" id="register-first-name" placeholder="Season Name" autocomplete="off"> 
			<select name="seasonGameId" class="form-control" id="seasonGameSelect">
				<c:forEach items="${GameDAO.getAllGames()}" var="game">
					<option value="${game.getGameId()}">${game.getGameName()}</option>
				</c:forEach>
			</select> 
			<input name="seasonEndDate" type="text" class="form-control my-2" id="datepicker" placeholder="Season End Date" autocomplete="off">
			<select name="seasonScoringType" class="form-control" id="seasonScoringTypeSelect">
				<option value="A">Averaged Scoring</option>
				<option value="L">Layered Scoring</option>
			</select>
			<button type="submit" class="btn btn-success btn-lg btn-block my-4">Create Season</button>
		</form>
	</div>
	
</div>

<script src="pikaday.js"></script>
<script>
    var picker = new Pikaday({
    	field: document.getElementById('datepicker'),
    	minDate: new Date()
    });
</script>
