<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html>
<html>
<head>
	<title>Season GG: Compete in Seasons Against your Friends</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<jsp:include page="WEB-INF/snippets/CommonIncludes.jspf" />
</head>
<body>
	<%@ include file="WEB-INF/snippets/Nav.jspf" %>
	
	<div class="container content">
		<%@ include file="WEB-INF/snippets/Header.jspf" %>
		
		<shiro:authenticated>
			<jsp:include page="WEB-INF/views/user/UserHome.jsp"></jsp:include>
		</shiro:authenticated>
		
		<shiro:notAuthenticated>
		
			<div class="row col-12 col-md-8 mx-auto my-4">
				<div class="season-gg-jumbotron">
					<p class="lead">One game isn't enough to determine who is the best. A true champion can
						only be proven throughout an entire season of games. Season GG allows you to track scores over
						an entire season, so you can finally prove to your friends that you're better than them.
					</p>
					<hr class="my-4">
					<p>Get started today by either logging in below, or registering if you haven't already!</p>
				</div>
			</div>
		
			<div class="row">
				<div class="col-12 col-md-8 mx-auto text-center">
					<button class="btn btn-primary btn-lg btn-block" onclick="UserController.showLoginForm()">Log In</button>
				</div>
				<div class="col-12 col-md-8 mx-auto text-center mt-4">
					<button href="#" role="button" class="btn btn-primary btn-lg btn-block" onclick="UserController.showRegisterForm()">Register</button>
				</div>
					
					<div id="login-form" class="col-12 col-md-8 mx-auto d-none">
					<form id="loginForm" action="/userLogIn" method="POST">
						<input name="username" type="email" class="form-control my-2" id="login-username" aria-describedby="emailHelp" placeholder="Enter email">
						<input name="password" type="password" class="form-control my-2" id="login-password" placeholder="Password">
						<button type="submit" class="btn btn-success btn-lg btn-block my-2">Log In</button>
					</form>
				</div>
				
				<div id="register-form" class="col-12 col-md-8 mx-auto d-none">
					<form id="registerForm" action="/userRegister" method="POST">
						<input name="username" type="email" class="form-control  my-2" id=register-username aria-describedby="emailHelp" placeholder="Enter email">
						<input name="password" type="password" class="form-control  my-2" id="register-password" placeholder="Password">
						<input name="passwordVerify" type="password" class="form-control  my-2" id="register-password-verify" placeholder="Password">
						<input name="firstName" type="text" class="form-control  my-2" id="register-first-name" placeholder="First Name">
						<input name="lastName" type="text" class="form-control  my-2" id="register-last-name" placeholder="Last Name">
						<button type="submit" class="btn btn-success btn-lg btn-block  my-2">Register</button>
					</form>
				</div>
				
			</div>

		</shiro:notAuthenticated>
	</div>
	
	<%@ include file="/WEB-INF/snippets/Footer.jspf" %>
	
</body>
</html>