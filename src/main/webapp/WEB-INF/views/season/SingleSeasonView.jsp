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
					<p class="h2">${season.getGame().getGameName()}</p>
				</div>
				<div class="col-12 col-md-4 mx-auto text-center mt-4">
					<p class="h3">${season.getName()}</p>
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
					
					<c:forEach var="round" items="${season.getRounds()}" varStatus="loop">
						<div class="accordion" id="round-accordion-${loop.index}">
							<div class="card">
								<div class="card-header" id="headingOne">
									<h2 class="mb-0">
										<button class="btn btn-link" type="button" data-toggle="collapse"
											data-target="#round-accordion-collapse-${loop.index}" aria-expanded="true" aria-controls="round-accordion-collapse-${loop.index}">
											<fmt:formatDate pattern = "MM/dd/yyyy" value="${round.getRoundDate()}" />
											Victor - ${listofVictors.get(loop.index)}
										</button>
									</h2>
								</div>
	
								<div id="round-accordion-collapse-${loop.index}" class="collapse" aria-labelledby="headingOne"
									data-parent="#round-accordion-${loop.index}">
									<div class="card-body no-padding">
										 <table class="table table-striped table-dark">
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
															<td>DEMO</td>
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
				<div id="player-add-container-0" class="form-row mb-2">
				    <div class="col">
					    <select name="roundPlayer0" class="form-control" id="round-player-select">
							<option value="" selected disabled hidden>Choose Player</option>
							<c:forEach items="${UserDAO.getAllUsers()}" var="user">
								<option value="${user.getUserId()}">${user.getFirstName()} ${user.getLastName()}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col">
						<input name="playerPlace0" type="number" class="form-control" id="round-player-place-input" placeholder="Place">
					</div>
				</div>
				<div class="form-row">
					<button type="button" class="btn btn-primary btn-lg mx-auto my-4" onClick="SeasonController.addPlayerInputToCreateRoundForm();">Add a Player</button>
				</div>
				<div class="form-row">
					<button type="submit" class="btn btn-success btn-lg btn-block mb-2">Create Round</button>
				</div>
			</form>
			
		</shiro:authenticated>

	</div>
</body>
</html>