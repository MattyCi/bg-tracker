<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="numPagesDecimal" value="${(param.numElements / param.numElementsPerPage)}" />

<fmt:formatNumber var="numPages" 
	value="${numPagesDecimal + (numPagesDecimal % 1 == 0 ? 0 : 0.5)}" maxFractionDigits="0" />

<c:if test="${numPages > 1}">
	<div class="mx-auto mt-3">
		<ul class="pagination">
		
			<li class="page-item <c:if test="${param.currentPage == 0}">disabled</c:if>">
				<a class="page-link" href="${param.pageNumberLink}${param.currentPage - 1}">&laquo;</a>
			</li>
			
			<c:set var="counter" value="0" scope="page" />
			<c:set var="beginPage" value="1" scope="page" />
			
			<c:if test="${param.currentPage > 4}">
				<c:set var="beginPage" value="${param.currentPage - 3}" scope="page" />
			</c:if>
			
			<c:forEach var="pageNumber" begin="${beginPage}" end="${numPages}">
				<c:if test="${counter < 9}"> 
					
					<li class="page-item <c:if test="${pageNumber == (param.currentPage + 1)}">active</c:if>">
						<a class="page-link" href="${param.pageNumberLink}${pageNumber - 1}">${pageNumber}</a>
					</li>
					
					<c:set var="counter" value="${counter + 1}" scope="page"/>
					
				</c:if>
			</c:forEach>
			
			<li class="page-item <c:if test="${(param.currentPage + 1) == numPages}">disabled</c:if>">
				<a class="page-link" href="${param.pageNumberLink}${param.currentPage + 1}">&raquo;</a>
			</li>
			
		</ul>
	</div>
</c:if>
