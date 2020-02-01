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
	<jsp:include page="WEB-INF/snippets/CommonIncludes.jspf" />
	<script src="./resources/js/BGGAPIWrapper.js"></script>
	<script src="./resources/js/SeasonCreateUtil.js"></script>
</head>
<body>
	<%@ include file="WEB-INF/snippets/Nav.jspf" %>
	
	<shiro:notAuthenticated>
	
		<c:set var = "isGuestHomePage" value="${true}"/>
		
		<div id="carouselExampleSlidesOnly" class="carousel slide" data-ride="carousel">
			<div class="carousel-inner">
				<div class="carousel-item active">
					<img class="d-block img-fluid mx-auto" src="/resources/img/dice-catch.jpg" alt="Playing with Dice">
					<div class="carousel-caption d-none d-md-block text-left">
						<h1>Season GG</h1>
						<p class="lead">Become the board game champion amongst your friends</p>
					</div>
				</div>
			</div>
		</div>
		
	</shiro:notAuthenticated>

	<div class="container content">
		<%@ include file="WEB-INF/snippets/Header.jspf" %>
		
		<%@ include file="./WEB-INF/snippets/PopupMessage.jspf" %>
		
		<shiro:authenticated>
			<jsp:include page="WEB-INF/views/user/UserHome.jsp"></jsp:include>
		</shiro:authenticated>
		
		<shiro:notAuthenticated>

			<div class="row text-center mt-4">
				
				<div class="col d-md-none mb-4">
					<img class="logo-img" src="/resources/img/logo.png" alt="logo">
				</div>
				
				<div class="col-lg-4">
					<img
						src="/resources/img/trophy.png"
						alt="Trophy" width="140" height="140">
					<h2>Compete</h2>
					<p>
						One game isn't enough to determine who is the best. A true champion can
						only be proven throughout an entire season of games. Season GG allows you to track scores over
						an entire season, so you can finally prove to your friends that you're better than them.
					</p>
				</div>
				
				<div class="col-lg-4">
					<img class="rounded-circle"
						src="/resources/img/question.png"
						alt="Question Mark" width="140" height="140">
					<h2>How does it Work?</h2>
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
				
				<div class="col-lg-4">
					<img src="/resources/img/power-button.png" alt="Power Button" width="140" height="140">
					<h2>Get Started</h2>
					<p>
						To get started, create a free account below. Once registered, you can create your 
						first season.
					</p>
				</div>
				
			</div>
		
			<div class="row pb-4">
	
				<div class="col-md-6">
					<form id="loginForm" action="/userLogIn" method="POST">
						<h1 class="h3 mb-3 font-weight-normal text-center">Please sign in.</h1>
						<input name="username" type="text" class="form-control my-2" id="login-username" placeholder="Enter Username">
						<input name="password" type="password" class="form-control my-2" id="login-password" placeholder="Password">
						<button type="submit" class="btn btn-success btn-rspnsv btn-lg my-2">Log In</button>
					</form>
				</div>
				
				<div class="col-md-6">
					<form id="registerForm" action="/userRegister" method="POST">
						<h1 class="h3 mb-3 font-weight-normal text-center">Or, register below.</h1>
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