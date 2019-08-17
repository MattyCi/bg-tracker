<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<div class="row">
	<div class="col-12 mx-auto text-center mt-4">
		<h3>Current Standings</h3>
		<table class="table table-hover table-dark">
			<thead>
				<tr>
					<th scope="col">Place</th>
					<th scope="col">Player Name</th>
					<th scope="col">Points</th>
					<th scope="col">Games Played</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="seasonStanding" items="${season.getSeasonStandings()}">
					<tr>
						<th scope="row">${seasonStanding.getPlace()}</th>
						<td>${seasonStanding.getReguser().getFirstName()} ${seasonStanding.getReguser().getLastName()}</td>
						<td>${seasonStanding.getAveragedPoints()}</td>
						<td>${seasonStanding.getGamesPlayed()}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>

</body>
</html>