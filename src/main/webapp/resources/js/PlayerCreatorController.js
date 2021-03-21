
var PlayerCreatorController = {
	createPlayerForm : null,

	createPlayer : function() {
		
		this.createPlayerForm = $("#create-guest-player-form");
		
		this.hidePlayerCreateForm();
		
		this.showSpinner();
		
		var createPlayerParams = this.buildPlayerCreateParams(),
			createPlayerUrl = this.createPlayerForm.attr("action"),
			createPlayerPostRequest = jQuery.post( createPlayerUrl, createPlayerParams );
		
		createPlayerPostRequest.done(function(playerCreateResponse) {
			
			PlayerCreatorController.processCreatePlayerResponse(playerCreateResponse);
			
		});
	
		createPlayerPostRequest.fail(function() {
	
			PlayerCreatorController.showPlayerCreateForm();
	
		});
	
		createPlayerPostRequest.always(function() {
	
			PlayerCreatorController.removeSpinner();
	
		});
		
	},

	buildPlayerCreateParams : function() {
		
		let username = $("#player-create-username").val(),
			csrfToken = $("#csrf-token").val();
			
		return {
				"username" : username,
				"csrfToken"  : csrfToken
			}
		
	},
	
	processCreatePlayerResponse : function(playerCreateResponse) {

		if (playerCreateResponse == null) {
			this.showErrorMessage(null);
			return;
		}
		
		if (playerCreateResponse.statusCode == null || playerCreateResponse.statusCode == undefined || 
				playerCreateResponse.statusCode != 0) {
			
			this.showErrorMessage(playerCreateResponse.errorMessage);
			return;
			
		} else if ( !$("#player-create-err-msg").hasClass("d-none") ) {
			$("#player-create-err-msg").addClass("d-none");
		}
		
		this.buildSuccessMessages(playerCreateResponse);

		this.showSuccessContainer();
		
		this.showPlayerCreateForm();

		this.clearPlayerCreateForm();
		
	},
	
	showErrorMessage : function(errorMessage) {

		let errMsgElement = $("#player-create-err-msg");
		
		if (errorMessage == null || errorMessage == undefined) {
			errMsgElement.text("Sorry, an unexpected error occurred.");
		} else {
			errMsgElement.text(errorMessage);
		}
		
		errMsgElement.removeClass("d-none");
		
		this.showPlayerCreateForm();

	},
	
	buildSuccessMessages : function(playerCreateResponse) {
		
		let acctRedeemCodeElement = $("#player-create-redeem-code");
		
		acctRedeemCodeElement.text(playerCreateResponse.accountRedeemToken);
		
		let acctRedeemLinkElement = $("#player-create-redeem-link");
		
		acctRedeemLinkElement.attr("href", "/accountRedeem");
		
		this.addNewPlayerToSelectElements(playerCreateResponse);
		
		let createdPlayerUsernameElement = $("#player-create-username");
		
		createdPlayerUsernameElement.text(playerCreateResponse.username + "'s account redeem code:");
		
	},
	
	addNewPlayerToSelectElements : function(playerCreateResponse) {

		SeasonController.addPlayerInputToCreateRoundForm();
		
		let playerSelectElement = $("#recent-players-select-" + SeasonController.addedPlayerCount);
		
		playerSelectElement.addClass("is-valid");
		
		playerSelectElement.append(
			$("<option></option>")
				.attr("value",playerCreateResponse.userId)
				.text(playerCreateResponse.username)
				.prop("selected", true)
			);

	},
	
	showSuccessContainer : function() {

		$("#player-create-success-container").removeClass("d-none");

	},
	
	hidePlayerCreateForm : function() {
		
		this.createPlayerForm.addClass("d-none");
		
	},
	
	showPlayerCreateForm : function() {

		this.createPlayerForm.removeClass("d-none");

	},
	
	clearPlayerCreateForm : function() {
		
		$("#player-create-first-name").val(""),
		$("#player-create-last-name").val("");
		
	},
	
	showSpinner : function() {

		$("#create-player-spinner").removeClass("d-none");
		
		$("#create-player-spinner").addClass("d-flex");

	},
	
	removeSpinner : function() {

		$("#create-player-spinner").removeClass("d-flex");
		
		$("#create-player-spinner").addClass("d-none");

	}
	
}

$( document ).ready(function() {
	
	$("#create-player-btn").click(function() {
		
		PlayerCreatorController.createPlayer();
		
	});
	
	$("#player-create-username").keydown(function(event) {
		
		if(event.keyCode == 13) {
			
			event.preventDefault();
			PlayerCreatorController.createPlayer();
			
		}
		
	});
	
});
