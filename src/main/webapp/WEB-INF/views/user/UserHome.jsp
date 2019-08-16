<jsp:useBean id="GameDAO" class="org.bgtrack.models.daos.GameDAO" scope="session"/>
<jsp:useBean id="SeasonDAO" class="org.bgtrack.models.daos.SeasonDAO" scope="session"/>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<div class="row pt-2">
	<div class="col-12 col-md-6 mx-auto">
		<a href="#" class="list-group-item list-group-item-action flex-column align-items-start active full-height" onclick="SeasonController.showYourSeasons()">
			<div class="d-flex w-100 justify-content-between">
				<h5 class="mb-1">Your Seasons</h5>
			</div>
			<p class="mb-1">See the seasons which you are a part of only.</p>
		</a>
	</div>
	<div class="col-12 col-md-6 mx-auto">
		<a href="#" class="list-group-item list-group-item-action flex-column align-items-start" onclick="SeasonController.showAllSeasons()">
			<div class="d-flex w-100 justify-content-between">
				<h5 class="mb-1">All Seasons</h5>
			</div>
			<p class="mb-1">See all seasons being tracked currently. This includes seasons you are
				currently not a part of.</p>
		</a>
	</div>
</div>

<div id="your-seasons" class="row pt-4 d-none">
	<c:set var="yourSeasonsList" value="${SeasonDAO.getAllSeasons()}" />
	<c:choose>
		<c:when test="${empty seasonsList}">
			<div class="col-12 col-md-6 mx-auto">
				<div class="alert alert-dismissible alert-danger">
				  	<p><strong>Uh oh!</strong>  You aren't apart of any seasons yet. Try to join one by clicking "All Seasons".</p>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<%@ include file="../season/AllSeasonView.jsp" %>
		</c:otherwise>
	</c:choose>
</div>

<div id="all-seasons" class="row pt-4 d-none">
	<c:set var="allSeasonsList" value="${SeasonDAO.getAllSeasons()}" />

	<div class="col-12 col-md-6 mx-auto">
		<c:if test="${empty allSeasonsList}">
			<div class="alert alert-dismissible alert-danger text-center">
				<p>
					<strong>Uh oh!</strong> There aren't any seasons yet. Try to create one first.
				</p>
			</div>
		</c:if>
		
		<form id="seasonCreateForm" action="/bgtracker/createSeason" method="POST">
			<input name="seasonName" type="text" class="form-control my-2" id="register-first-name" placeholder="Season Name"> 
			<select name="seasonGameId" class="form-control" id="seasonGameSelect">
				<c:forEach items="${GameDAO.getAllGames()}" var="game">
					<option value="${game.getGameId()}">${game.getGameName()}</option>
				</c:forEach>
			</select> 
			<input name="seasonEndDate" type="text" class="form-control my-2" id="datepicker" placeholder="Season End Date">
			<select name="seasonScoringType" class="form-control" id="seasonScoringTypeSelect">
				<option value="A">Averaged Scoring</option>
				<option value="L">Layered Scoring</option>
			</select>
			<button type="submit" class="btn btn-success btn-lg btn-block my-4">Create Season</button>
		</form>
	</div>

	<c:if test="${not empty allSeasonsList}">
		<%@ include file="../season/AllSeasonView.jsp" %>
	</c:if>

</div>

<script src="pikaday.js"></script>
<script>
    var picker = new Pikaday({ field: document.getElementById('datepicker') });
</script>
