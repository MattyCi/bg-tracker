<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="e" uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="/WEB-INF/snippets/CommonJSTLIncludes.jspf" %>

<!DOCTYPE html>
<html>
<head>
	<title>Season GG: Redeem your Account</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<jsp:include page="/WEB-INF/snippets/CommonIncludes.jspf" />
</head>
<body>
	<%@ include file="/WEB-INF/snippets/Nav.jspf" %>

	<div class="container content">
		<%@ include file="/WEB-INF/snippets/Header.jspf" %>
		
		<%@ include file="../../snippets/PopupMessage.jspf" %>
		
		<div class="row">
			
			<div class="col-md-10 col-lg-6 mx-auto mt-4 text-center">
				<h2>Redeem your Account</h2>
				<img class="my-2" src="/resources/img/user.png" alt="User" width="140" height="140">
			</div>
			
			<div class="col-12">
				<hr class="my-4">
			</div>
			
			<shiro:authenticated>
				<div class="col-md-10 col-lg-6 mx-auto">
					<h3 class="text-danger text-center">Sorry, you must be logged out in order to redeem an account.</h3>
				</div>
			</shiro:authenticated>
			
			<shiro:notAuthenticated>
				<div class="col-md-10 col-lg-6 mx-auto">
					<form id="account-update-form" action="/attemptAccountRedeem" method="POST" autocomplete="off">
						
						<h1 class="h3 mb-3 font-weight-normal text-center">Submit your Token Below</h1>
						<p>
							Your secret token should have been provided to you by your friend if they 
							created an account for you on your behalf.
						</p>
						
						<input name="submittedToken" class="form-control my-2" placeholder="Account Redeem Token">
						
						<p>Please enter a password for your redeemed account below:</p>
						<input name="password" type="password" class="form-control  my-2" id="register-password" placeholder="New Password" autocomplete="new-password">
						<input name="passwordVerify" type="password" class="form-control  my-2" id="register-password-verify" placeholder="New Password Verify" autocomplete="new-password">
											
						<button type="submit" class="btn btn-success btn-lg my-2">Redeem Account</button>
					</form>
				</div>
			</shiro:notAuthenticated>
			
		</div>
		
	</div>
	
	<%@ include file="/WEB-INF/snippets/Footer.jspf" %>
	
</body>
</html>