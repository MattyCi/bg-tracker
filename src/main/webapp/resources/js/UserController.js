
var UserController = {
	isLoginFormVisible : false,
	isRegisterFormVisible : false,

	showLoginForm : function() {
		if (this.isLoginFormVisible) {
			document.getElementById("login-form").classList.add("d-none");
			this.isLoginFormVisible = false;
			return;
		}
		
		document.getElementById("register-form").classList.add("d-none");
		this.isRegisterFormVisible = false;
		document.getElementById("login-form").classList.remove("d-none");
		this.isLoginFormVisible = true;
	},

	showRegisterForm : function() {
		if (this.isRegisterFormVisible) {
			document.getElementById("register-form").classList.add("d-none");
			this.isRegisterFormVisible = false;
			return;
		}
		
		document.getElementById("login-form").classList.add("d-none");
		this.isLoginFormVisible = false;
		document.getElementById("register-form").classList.remove("d-none");
		this.isRegisterFormVisible = true;
	}

};