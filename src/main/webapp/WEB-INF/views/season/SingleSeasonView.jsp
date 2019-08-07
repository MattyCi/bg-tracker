<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="UserDAO" class="org.bgtrack.models.user.daos.UserDAO" scope="session"/>

<!DOCTYPE html>
<html>
<head>
	<title>Board Game Tracker | View Season</title>
	<meta charset="UTF-8">
	<jsp:include page="../../snippets/CommonIncludes.jspf" />
	<shiro:notAuthenticated>
		<meta http-equiv="Refresh" content="0; url=/bgtracker">
	</shiro:notAuthenticated>
</head>
<body>
	<jsp:include page="../../snippets/Nav.jspf" />
	
	<div class="container">
		<%@ include file="../../snippets/Header.jspf" %>
		
		<shiro:authenticated>
			
			<jsp:include page="../../views/user/UserHome.jsp"></jsp:include>

			<div class="row">
				<div class="col-12 col-md-4 mx-auto text-center mt-4">
					<p class="h3">${season.getName()}</p>
				</div>
				<div class="col-12 col-md-4 mx-auto text-center mt-4">
					<p class="h2">Game: ${season.getGame().getGameName()}</p>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 mx-auto text-center mt-4">
					<h3>Standings</h3>
					<table class="table table-hover table-dark">
						<thead>
							<tr>
								<th scope="col">Player Name</th>
								<th scope="col">Total Points</th>
								<th scope="col">Games Played</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<th scope="row">1</th>
								<td>Mark</td>
								<td>Otto</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 mx-auto text-center mt-4">
					<h3>Rounds</h3>
					
					<c:if test="${empty season.getRounds()}">
						<div class="col-12 col-md-8 mx-auto text-center bg-warning mt-1">
							<p class="lead text-dark py-2">
								This season does not have any rounds created yet. Play a game and add the round results below!
							</p>
						</div>
					</c:if>
					
					<c:forEach var="round" items="${season.getRounds()}" varStatus="loop">
						<div class="accordion" id="round-accordion-${loop.index}">
							<div class="card bg-primary">
								<button class="btn btn-link no-underline-link" type="button" data-toggle="collapse"
									data-target="#round-accordion-collapse-${loop.index}" aria-expanded="true" aria-controls="round-accordion-collapse-${loop.index}">
									<div class="row">
										<div class="col text-center">
											<h5 class="text-white text-center my-2">
												<span class="float-left">${loop.index+1}.</span>
												<fmt:formatDate pattern = "MM/dd/yyyy" value="${round.getRoundDate()}" />
											</h5>
										</div>
									</div>
								</button>
								<div id="round-accordion-collapse-${loop.index}" class="collapse" aria-labelledby="headingOne"
									data-parent="#round-accordion-${loop.index}">
									<div class="card-body no-padding">
										<div class="row">
											<div class="col-12">
												<h5 class="text-center text-white bg-success height-full py-2">Victor: ${listofVictors.get(loop.index)}</h5>
											</div>
										</div>
										 <table class="table table-striped table-dark no-margin-bottom">
											 <thead>
											    <tr>
											    	<th scope="col">Place</th>
											    	<th scope="col">Points Earned</th>
											    	<th scope="col">Player Name</th>
												</tr>
											</thead>
											<tbody>
											    <c:forEach var="roundResult" items="${round.getRoundResults()}">
														<tr>
															<td>${roundResult.getPlace()}</td>
															<td>${roundResult.getPoints() + roundResult.getLayeredPoints()}</td>
														    <td>${roundResult.getReguser().getFirstName()} ${roundResult.getReguser().getLastName()}</td>
														</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</c:forEach>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 mx-auto text-center mt-4">
					<h3>Add a Round</h3>
				</div>
			</div>
				
			<form id="round-create-form" action="/bgtracker/createRound" method="POST">
				<input name="seasonId" type="hidden" id="season-id-input" value="${season.getSeasonId()}">
				<div id="player-add-container-0" class="form-row mb-2 justify-content-center pt-2">
				    <div class="col-sm-8 col-lg-6">
					    <select name="roundPlayer0" data-round-info="true" class="form-control" id="round-player-select">
							<option value="" selected disabled hidden>Choose Player</option>
							<c:forEach items="${UserDAO.getAllUsers()}" var="user">
								<option value="${user.getUserId()}">${user.getFirstName()} ${user.getLastName()}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-sm-4 col-lg-2">
						<input name="playerPlace0" data-round-info="true" type="number" class="form-control" id="round-player-place-input" placeholder="Place">
					</div>
				</div>
				<div class="row">
					<div class="col-md-6 mx-auto pt-4">
						<button type="button" class="btn btn-success btn-block mb-2" onClick="SeasonController.addPlayerInputToCreateRoundForm();">Add a Player</button>
					</div>
				</div>
				<div class="row">
					<div class="col-md-6 mx-auto pt-4">
						<button id="round-create-btn" type="button" class="btn btn-success btn-block mb-2">Create Round</button>
					</div>
				</div>
			</form>
			
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