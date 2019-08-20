<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html>
<html>
<head>
	<title>Board Game Tracker</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<jsp:include page="WEB-INF/snippets/CommonIncludes.jspf" />
</head>
<body>
	<jsp:include page="WEB-INF/snippets/Nav.jspf" />
	
	<div class="container">
		<%@ include file="WEB-INF/snippets/Header.jspf" %>
		
		<shiro:authenticated>
			<div class="row">
				<div class="col-12 col-md-8 mx-auto text-center mt-4">
					<h3>Welcome, <small class="text-muted">${regUser.firstName}</small></h3>
					<form id="logoutForm" action="/userLogOut">
						<button type="submit" class="btn btn-danger btn-lg btn-block my-2">Log Out</button>
					</form>
				</div>
			</div>
			
			<jsp:include page="WEB-INF/views/user/UserHome.jsp"></jsp:include>
		</shiro:authenticated>
		
		<shiro:notAuthenticated>
			<div class="row">
				<div class="col-12 col-md-8 mx-auto text-center mt-4">
					<a href="#" role="button" class="btn btn-primary btn-lg btn-block" onclick="UserController.showLoginForm()">Log In</a>
				</div>
				<div class="col-12 col-md-8 mx-auto text-center mt-4">
					<a href="#" role="button" class="btn btn-primary btn-lg btn-block" onclick="UserController.showRegisterForm()">Register</a>
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
</body>
</html>