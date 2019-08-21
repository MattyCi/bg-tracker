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

					<c:choose>
						<c:when test="${seasonStanding.getPlace() eq 999}">
							<c:set var="finalPlayerPlace" value="INELIGIBLE" />
							<c:set var="ineligibilityHighlighting" value="text-warning" />
						</c:when>
						<c:otherwise>
							<c:set var="finalPlayerPlace" value="${seasonStanding.getPlace()}" />
						</c:otherwise>
					</c:choose>

					<tr class="${ineligibilityHighlighting}">
						<th scope="row">${finalPlayerPlace}</th>
						<td>${seasonStanding.getReguser().getFirstName()} ${seasonStanding.getReguser().getLastName()}</td>
						<td>${seasonStanding.getAveragedPoints()}</td>
						<td>${seasonStanding.getGamesPlayed()}</td>
					</tr>

					<c:remove var="finalPlayerPlace" />
					<c:remove var="ineligibilityHighlighting" />

				</c:forEach>
			</tbody>
		</table>
	</div>
</div>

</body>
</html>