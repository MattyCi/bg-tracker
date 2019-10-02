<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<c:choose>
	<c:when test="${not empty UserSeasonStats}">
		<c:set var="isSeasonStatPage" value="${true}"/>
		<c:set var="seasonStandingList" value="${UserSeasonStats.seasonStandingList}" />
		<c:set var="playerName" value="${seasonStandingList.get(0).getReguser().getFirstName()} ${seasonStandingList.get(0).getReguser().getLastName()}" />
		<c:set var="tableTitle" value="${playerName}'s Season Stats"/>
	</c:when>
	<c:otherwise>
		<c:set var="isSeasonStatPage" value="${false}"/>
		<c:set var="tableTitle" value="Current Standings"/>
		<c:set var="seasonStandingList" value="${season.getSeasonStandings()}" />
	</c:otherwise>
</c:choose>

<h3 class="text-center">${tableTitle}</h3>
<c:if test="${isSeasonStatPage eq false}">
	<small class="text-muted">Click a player's name to view a breakdown of their stats for
		this season.</small>
</c:if>
<table class="table table-hover table-primary text-center">
	<thead>
		<tr>
			<th scope="col">Place</th>
			<th scope="col">Player Name</th>
			<th scope="col">Points</th>
			<th scope="col">Games Played</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="seasonStanding" items="${seasonStandingList}">

			<c:choose>
				<c:when test="${seasonStanding.getPlace() eq 999}">
					<c:set var="finalPlayerPlace" value="N/A" />
					<c:set var="ineligibilityHighlighting" value="text-warning" />
				</c:when>
				<c:otherwise>
					<c:set var="finalPlayerPlace" value="${seasonStanding.getPlace()}" />
				</c:otherwise>
			</c:choose>

			<tr class="${ineligibilityHighlighting}">
				<th scope="row">${finalPlayerPlace}</th>
				<td><c:choose>
						<c:when test="${isSeasonStatPage eq false}">
							<a class="user-stat-breakdown-link ${ineligibilityHighlighting}"
								href="/viewPlayerSeasonStats?selectedSeasonId=${season.getSeasonId()}&selectedUserId=${seasonStanding.getReguser().getUserId()}">
								${seasonStanding.getReguser().getFirstName()} ${seasonStanding.getReguser().getLastName()}
							</a>
						</c:when>
						<c:otherwise>
						${seasonStanding.getReguser().getFirstName()} ${seasonStanding.getReguser().getLastName()}
					</c:otherwise>
					</c:choose></td>
				<td>${seasonStanding.getAveragedPoints()}</td>
				<td>${seasonStanding.getGamesPlayed()}</td>
			</tr>

			<c:if test="${isSeasonStatPage eq true}">
				<tr>
					<th scope="row" colspan="4">Win Ratio: ${UserSeasonStats.winRatio}</th>
				</tr>
			</c:if>

			<c:remove var="finalPlayerPlace" />
			<c:remove var="ineligibilityHighlighting" />

		</c:forEach>
	</tbody>
</table>
<p class="text-muted text-small pl-2">*Ineligible players will be highlighted with yellow text.</p>
