<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="e" uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<%@ page errorPage="/ErrorPage.jsp" %>

<!DOCTYPE html>
<html>
<head>
	<title>Season GG: Compete in Seasons Against your Friends</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<jsp:include page="WEB-INF/snippets/CommonIncludes.jspf" />
	<script src="./resources/js/BGGAPIWrapper.js"></script>
	<script src="./resources/js/SeasonCreateUtil.js"></script>
	<script src="./resources/js/Carousel.js"></script>
</head>
<body>
	<%@ include file="WEB-INF/snippets/Nav.jspf" %>

	<div class="container content">
		<%@ include file="WEB-INF/snippets/Header.jspf" %>
		
		<%@ include file="./WEB-INF/snippets/PopupMessage.jspf" %>
		
		<shiro:authenticated>
			<jsp:include page="WEB-INF/views/user/UserHome.jsp"></jsp:include>
		</shiro:authenticated>
		
		<shiro:notAuthenticated>
			
			<div class="row d-md-none mt-3">	
				<div class="col">
					<img class="logo-img" src="/resources/img/logo.png" alt="logo">
				</div>
			</div>
			
			<div class="row mt-3 d-none d-md-block">	
				<div class="col text-center">
					<h1 class="display-4">
					  <span class="text-success font-weight-bold">Season GG</span>
					  <small class="text-secondary">take board game rivalry to the next level.</small>
					</h1>
				</div>
			</div>

			<%@ include file="/WEB-INF/snippets/Carousel.jspf" %>

			<div class="row text-center mt-3">
				
				<div class="col-lg-4 bg-light border pt-4 mt-lg-0">
					<img
						src="/resources/img/trophy.png"
						alt="Trophy" width="140" height="140">
					<p class="h2 pt-1">Compete</p>
					<p>
						One game isn't enough to determine who is the best. A true champion can
						only be proven throughout an entire season of games. Season GG allows you to track scores over
						an entire season, so you can finally prove to your friends that you're better than them.
					</p>
				</div>
				
				<div class="col-lg-4 bg-light border pt-4 mt-4 mt-lg-0">
					<img class="rounded-circle"
						src="/resources/img/question.png"
						alt="Question Mark" width="140" height="140">
					<p class="h2 pt-1">How does it Work?</p>
					<p>
						Create a season to get started for free, determine when the season will end,
						and choose a game to play for the duration of the competition.
					</p>
					<p>
						Track the results of each round you play every time your friends get together to play.
						The better you play, the more points are awarded to you for the round.
					</p>
					<p>
						At the end of the season, whoever has scored the most points (on average) becomes the 
						season champion.
					</p>
				</div>
				
				<div class="col-lg-4 bg-light border pt-4 mt-4 mt-lg-0">
					<img src="/resources/img/power-button.png" alt="Power Button" width="140" height="140">
					<p class="h2 pt-1">Get Started</p>
					<p>
						To get started, create a free account below. Once registered, you can create your 
						first season.
					</p>
				</div>
				
			</div>
		
			<div class="row my-3">
	
				<div class="col-md-6 my-2 my-lg-0">
					<form id="loginForm" action="/userLogIn" method="POST">
						<h1 class="h3 font-weight-normal text-center">Please sign in.</h1>
						<input name="username" type="text" class="form-control my-2" id="login-username" placeholder="Enter Username">
						<input name="password" type="password" class="form-control my-2" id="login-password" placeholder="Password">
						<button type="submit" class="btn btn-success btn-rspnsv btn-lg my-2">Log In</button>
					</form>
				</div>
				
				<div class="col-md-6 my-2 my-lg-0">
					<form id="registerForm" action="/userRegister" method="POST">
						<h1 class="h3 font-weight-normal text-center">Or, register below.</h1>
						<input name="username" type="text" class="form-control  my-2" id="register-username" placeholder="Enter Username">
						<input name="password" type="password" class="form-control  my-2" id="register-password" placeholder="Password" autocomplete="new-password">
						<input name="passwordVerify" type="password" class="form-control  my-2" id="register-password-verify" placeholder="Verify Password">
						<button type="submit" class="btn btn-success btn-rspnsv btn-lg my-2">Register</button>
					</form>
				</div>

			</div>

		</shiro:notAuthenticated>
	</div>
	
	<%@ include file="/WEB-INF/snippets/Footer.jspf" %>
	
</body>
</html>