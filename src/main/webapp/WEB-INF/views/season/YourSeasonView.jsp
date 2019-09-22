
<table class="table">
  <thead class="thead-light">
    <tr>
      <th scope="col">Name</th>
      <th scope="col">Start Date</th>
      <th scope="col">End Date</th>
    </tr>
  </thead>
  <tbody>
  	<c:forEach var = "yourSeason" items="${yourSeasonsList}" >
	    <tr>
	    	<td>
	    		<a href="/viewSeason?seasonId=${yourSeason.getSeasonId()}">${yourSeason.getName()}</a></td>
	    	<td>
				<fmt:formatDate pattern = "MM/dd/yyyy" value="${yourSeason.getStartDate()}" />
			</td>
	    	<td>
	      		<fmt:formatDate pattern = "MM/dd/yyyy" value="${yourSeason.getEndDate()}" />
      		</td>
	    </tr>
    </c:forEach>
  </tbody>
</table>