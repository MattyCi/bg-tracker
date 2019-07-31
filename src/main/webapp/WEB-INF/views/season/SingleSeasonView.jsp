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

					<div class="accordion" id="accordionExample">
						<div class="card">
							<div class="card-header" id="headingOne">
								<h2 class="mb-0">
									<button class="btn btn-link" type="button" data-toggle="collapse"
										data-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
										Round Date - <fmt:formatDate pattern = "MM/dd/yyyy" value="${round.getRoundDate()}" />
									</button>
								</h2>
							</div>

							<div id="collapseOne" class="collapse show" aria-labelledby="headingOne"
								data-parent="#accordionExample">
								<div class="card-body">
								
								</div>
							</div>
						</div>
					</div>



					<table class="table table-hover table-dark no-bottom-margin">
						<thead>
							<tr>
								<th scope="col">Points Earned</th>
								<th scope="col">Round Date</th>
								<th scope="col">Victor</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="round" items="${season.getRounds()}" varStatus="loop">
							    <tr data-toggle="collapse" data-target="#round-analysis-header${loop.index},#round-analysis${loop.index}" class="accordion-toggle">
							    	<td>
							    		DEMO
							    	</td>
							    	<td>
										<fmt:formatDate pattern = "MM/dd/yyyy" value="${round.getRoundDate()}" />
									</td>
									<td>
							    		${listofVictors.get(loop.index)}
							    	</td>
							    </tr>
							    
							    <thead class="thead-light">
								    <tr>
								    	<th scope="col" class="hiddenRow">
									    	<div class="accordion-body collapse" id="round-analysis-header${loop.index}">
									        	Points Earned
								        	</div>
									    </th>
									    <th scope="col" class="hiddenRow">
									    	<div class="accordion-body collapse" id="round-analysis-header${loop.index}">
									        	Place
								        	</div>
									    </th>
									    <th scope="col" class="hiddenRow">
									    	<div class="accordion-body collapse" id="round-analysis-header${loop.index}">
									        	Player Name
								        	</div>
									    </th>
									</tr>
								</thead>
							    <c:forEach var="roundResult" items="${round.getRoundResults()}">
										<tr class="table-light text-secondary">
											<td class="hiddenRow">
										    	<div class="accordion-body collapse" id="round-analysis${loop.index}">
										        	DEMO
									        	</div>
										    </td>
										    <td class="hiddenRow">
										    	<div class="accordion-body collapse" id="round-analysis${loop.index}">
										        	${roundResult.getPlace()}
									        	</div>
										    </td>
										    <td class="hiddenRow">
										    	<div class="accordion-body collapse" id="round-analysis${loop.index}">
										        	${roundResult.getReguser().getFirstName()} ${roundResult.getReguser().getLastName()}
									        	</div>
										    </td>
										</tr>
								</c:forEach>
						    </c:forEach>
						</tbody>
					</table>
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