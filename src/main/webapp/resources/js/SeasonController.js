
var SeasonController = {
	isYourSeasonsVisible : false,
	isAllSeasonsVisible : false,
	addedPlayerCount : 1,
	
	showAllSeasons : function() {
		if (this.isAllSeasonsVisible) {
			document.getElementById("all-seasons").classList.add("d-none");
			this.isAllSeasonsVisible = false;
			return;
		}
		
		document.getElementById("your-seasons").classList.add("d-none");
		this.isYourSeasonsVisible = false;
		document.getElementById("all-seasons").classList.remove("d-none");
		this.isAllSeasonsVisible = true;
	},
	
	showYourSeasons : function() {
		if (this.isYourSeasonsVisible) {
			document.getElementById("your-seasons").classList.add("d-none");
			this.isYourSeasonsVisible = false;
			return;
		}
		
		document.getElementById("all-seasons").classList.add("d-none");
		this.isAllSeasonsVisible = false;
		document.getElementById("your-seasons").classList.remove("d-none");
		this.isYourSeasonsVisible = true;
	},
	
	addPlayerInputToCreateRoundForm : function() {
		
		if (this.addedPlayerCount > 12) {
			alert("Sorry, no more than 12 players to a round are allowed.");
		}
		
		var inputContainer = document.getElementById("player-add-container-0");
		var clonedContainer = inputContainer.cloneNode(true);
		
		clonedContainer.id = 'player-add-container-'+this.addedPlayerCount;
		
		clonedContainer.children[0].children[0].name = 'roundPlayer'+this.addedPlayerCount;
			
		clonedContainer.children[1].children[0].name = 'playerPlace'+this.addedPlayerCount;
		
		inputContainer.parentNode.insertBefore(clonedContainer, inputContainer.nextSibling);
		
		this.addedPlayerCount++;
	}

};