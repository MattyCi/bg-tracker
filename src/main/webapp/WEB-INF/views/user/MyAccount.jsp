<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="e" uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project" %>

<!DOCTYPE html>
<html>
<head>
	<title>Season GG: Compete in Seasons Against your Friends</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<jsp:include page="/WEB-INF/snippets/CommonIncludes.jspf" />
</head>
<body>
	<%@ include file="/WEB-INF/snippets/Nav.jspf" %>

	<div class="container content">
		<%@ include file="/WEB-INF/snippets/Header.jspf" %>
		
		<%@ include file="../../snippets/PopupMessage.jspf" %>
		
		<shiro:authenticated>
			<div class="row">
				
				<div class="col-md-6 mt-4 text-center">
					<h2>My Account</h2>
					<img src="/resources/img/user.png" alt="User" width="140" height="140">
				</div>
				
				<div class="col-md-6 mt-4 text-center">
					<h1 class="h3 mb-3 font-weight-normal text-center">Current Account Info</h1>
					<h5>
						<e:forHtml value="${regUser.firstName}" /> <e:forHtml value="${regUser.lastName}" />
					</h5>
					<h5>
						<e:forHtml value="${shiroUser.principal}" />
					</h5>
				</div>
				
				<div class="col-md-6 mx-auto mt-4">
					<form id="account-update-form" action="/userUpdate" method="POST" autocomplete="off">
						
						<h1 class="h3 mb-3 font-weight-normal text-center">Update Account Below</h1>
						<p>
							<i class="fas fa-exclamation-circle text-warning"></i> Any field below left blank will NOT be updated.
						</p>
						
						<input name="csrfToken" type="hidden" value="${csrfToken}">
						<input name="username" type="email" class="form-control  my-2" id=register-username aria-describedby="emailHelp" placeholder="Enter New Email">
						<input name="password" type="password" class="form-control  my-2" id="register-password" placeholder="New Password" autocomplete="new-password">
						<input name="passwordVerify" type="password" class="form-control  my-2" id="register-password-verify" placeholder="New Password Verify" autocomplete="new-password">
						<input name="firstName" type="text" class="form-control  my-2" id="register-first-name" placeholder="New First Name">
						<input name="lastName" type="text" class="form-control  my-2" id="register-last-name" placeholder="New Last Name">
						
						<hr class="my-4">
						
						<p>You must provide your current password below for any account changes to take effect.</p>
						<input name="currentPassword" type="password" class="form-control  my-2" placeholder="Current Password" autocomplete="new-password">
						
						<button type="submit" class="btn btn-success btn-lg my-2">Update Account</button>
					</form>
				</div>
				
				<div class="col-12 mt-4">
					<p class="small text-muted text-left">
						Icons made by <a href="https://www.flaticon.com/authors/vectors-market" title="Vectors Market">Vectors
							Market</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a>.
					</p>
				</div>
				
			</div>
		</shiro:authenticated>
		
	</div>
	
	<%@ include file="/WEB-INF/snippets/Footer.jspf" %>
	
</body>
</html>