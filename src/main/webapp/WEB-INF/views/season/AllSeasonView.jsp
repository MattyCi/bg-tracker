
<table class="table">
  <thead class="thead-light">
    <tr>
      <th scope="col">Name</th>
      <th scope="col">Start Date</th>
      <th scope="col">End Date</th>
    </tr>
  </thead>
  <tbody>
  	<c:forEach var = "season" items="${allSeasonsList}" >
	    <tr>
	    	<td>
	    		<a href="/bgtracker/viewSeason?seasonId=${season.getSeasonId()}">${season.getName()}</a></td>
	    	<td>
				<fmt:formatDate pattern = "MM/dd/yyyy" value="${season.getStartDate()}" />
			</td>
	    	<td>
	      		<fmt:formatDate pattern = "MM/dd/yyyy" value="${season.getEndDate()}" />
      		</td>
	    </tr>
    </c:forEach>
  </tbody>
</table>