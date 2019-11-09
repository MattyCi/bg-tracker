
var SeasonController = {
	isYourSeasonsVisible : false,
	isAllSeasonsVisible : false,
	addedPlayerCount : 0,
	
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
				
		if (this.addedPlayerCount >= 11) {
			alert("Sorry, no more than 12 players to a round are allowed.");
			return;
		}

		var inputContainer = document.getElementById("player-add-container-"+(this.addedPlayerCount));
		var clonedContainer = inputContainer.cloneNode(true);
		
		this.addedPlayerCount++;
		
		clonedContainer.id = 'player-add-container-'+this.addedPlayerCount;
		
		inputContainer.parentNode.insertBefore(clonedContainer, inputContainer.nextSibling);
		
		this.incrementAllChildrenIds(clonedContainer);
		
		this.clearClonedInputs();
		
		this.updateNameAttributes();
		
		this.incremenetClonedPlayerPlaceInput();
		
		this.updatePlayerRemoveButtons();
		
		this.addPlayerSearchOnClickFunctions();
		
	},
	
	clearClonedInputs : function() {

		document.getElementById('player-search-input-'+this.addedPlayerCount).value = '';
		
		document.getElementById('player-search-input-'+this.addedPlayerCount).classList.remove('is-valid');
		
		document.getElementById('player-search-input-'+this.addedPlayerCount).classList.remove('is-invalid');

	},
	
	incrementAllChildrenIds : function(elementToIncrement) {

		var childrenElements = elementToIncrement.children;
		
		for (var i = 0; i < childrenElements.length; i++) {
			
			this.incrementChildId(childrenElements[i]);
			
			if (childrenElements[i].hasChildNodes()) {
				this.incrementAllChildrenIds(childrenElements[i]);
			}
			
		}

	},
	
	incrementChildId : function(elementToIncrement) {

		var currentElementId = elementToIncrement.id;
		
		if (currentElementId.includes("-"+(this.addedPlayerCount-1))) {
			
			var newElementId = currentElementId.replace("-"+(this.addedPlayerCount-1), "-"+this.addedPlayerCount);
			
			elementToIncrement.id = newElementId;
			
		}

	},
	
	updateNameAttributes : function() {

		document.getElementById("recent-players-select-"+(this.addedPlayerCount)).name = 'roundPlayer'+this.addedPlayerCount;

		document.getElementById("player-place-select-"+this.addedPlayerCount).name = 'playerPlace'+this.addedPlayerCount;
		
	},
	
	incremenetClonedPlayerPlaceInput : function() {
				
		var previouslySelectedPlace = parseInt(document.getElementById("player-place-select-"+(this.addedPlayerCount-1)).value, 10);
		
		document.getElementById("player-place-select-"+this.addedPlayerCount).value = previouslySelectedPlace + 1;

	},
	
	updatePlayerRemoveButtons : function() {
		
		var removeButtonElement = document.getElementById("remove-player-button-"+this.addedPlayerCount);
		
		this.showRemovePlayerButton(removeButtonElement);
		
		this.initializeClickEventForNewRemoveButton(removeButtonElement);
		
		this.removePreviousPlayerRemoveButton();
		
	},
	
	showRemovePlayerButton : function(removeButtonElement) {
				
		if (this.addedPlayerCount != 0) {
			
			removeButtonElement.classList.remove("d-none");
			
			removeButtonElement.id = "remove-player-button-"+this.addedPlayerCount;
			
		}

	},
	
	initializeClickEventForNewRemoveButton : function(removeButtonElement) {
		removeButtonElement.addEventListener("click", this.removeLastAddedPlayerFromForm);
	},
	
	removePreviousPlayerRemoveButton : function() {
		var previousPlayerRemoveButton = document.getElementById("remove-player-button-"+(this.addedPlayerCount-1));
		previousPlayerRemoveButton.classList.add("d-none");
	},
	
	removeLastAddedPlayerFromForm : function() {
		var lastAddedPlayerAddContainer = document.getElementById("player-add-container-"+(SeasonController.addedPlayerCount));
		lastAddedPlayerAddContainer.remove();
		
		var previousRemoveButtonElement = document.getElementById("player-add-container-"+(SeasonController.addedPlayerCount-1)).children[4].children[0];
		
		SeasonController.addedPlayerCount--;
		
		SeasonController.showRemovePlayerButton(previousRemoveButtonElement);

	},
	
	addPlayerSearchOnClickFunctions : function() {

		var currentlyAddedPlayerCount = SeasonController.addedPlayerCount;
		
	    document.getElementById('player-search-btn-'+currentlyAddedPlayerCount).onclick = function(){
	    	PlayerSearchUtil.playerSearch(currentlyAddedPlayerCount);
	    };

	},
	
	confirmAddRound : function(form) {
		var roundResultsNames = [];
		var roundResultsPlaces = [];
		
		var roundResultCount = 0;
		for (var i = 0, element; element = form.elements[i++];) {
			
			if (element.getAttribute("data-round-info") === 'true') {

				if (element.id.includes("recent-players-select")) {
					roundResultsNames[roundResultCount] = element.selectedOptions[0].innerHTML;
					
					if (!this.isValidPlayer(roundResultsNames[roundResultCount])) {
						alert("Sorry, one or more of the entered player values is not valid.");
						return;
					}
					
				} else {
					roundResultsPlaces[roundResultCount] = element.value;
					
					if (!this.isValidPlace(roundResultsPlaces[roundResultCount])) {
						alert("Sorry, one or more of the entered place values is not valid.");
						return;
					}
					
					roundResultCount++;
				}
		    }
			
		}
		
		this.buildRoundCreateConfirmPopup(roundResultsNames, roundResultsPlaces);
	},
	
	isValidPlayer : function(playerName) {
		
		if(playerName && playerName != "Choose Player") {
			return true;
		} else {
			return false;
		}
		
	},
	
	isValidPlace : function(place) {
		var placeNumber = +place;
		
		if (!isNaN(placeNumber) && placeNumber < 13 && placeNumber > 0) {
			return true;
		} else {
			return false;
		}

	},
	
	buildRoundCreateConfirmPopup : function(roundResultsNames, roundResultsPlaces) {
		var roundCreateConfirmTable = document.getElementById("round-create-confirm-popup-table-body");
		
		for (var i = 0; i < roundResultsNames.length; i++) {
			var newRow = roundCreateConfirmTable.insertRow(i),
				playerNameCell = newRow.insertCell(0),
				playerPlaceCell = newRow.insertCell(1);
			
			playerNameCell.innerHTML = roundResultsNames[i];
			playerPlaceCell.innerHTML = roundResultsPlaces[i]; 
		}
		
		document.getElementById("round-create-confirm-popup").classList.remove("d-none");
	},
	
	clearRoundConfirmTable : function() {
		var emptyTableData = document.createElement('tbody');
		var oldTableData = document.getElementById('round-create-confirm-popup-table-body');
		emptyTableData.id = 'round-create-confirm-popup-table-body';
		
		oldTableData.parentNode.replaceChild(emptyTableData, oldTableData);
	}

};

window.onload = function(){
	
    var roundCreateConfirmPopup = document.getElementById("round-create-confirm-popup");
    var playerSearchResultsPopup = document.getElementById("player-search-results-popup");
	
    document.getElementById('round-create-btn').onclick = function(){
    	SeasonController.confirmAddRound(document.getElementById('round-create-form'));
    };
    
    document.getElementById('round-create-confirm-btn').onclick = function(){
    	document.getElementById('round-create-form').submit();
    };
    
    document.getElementById('round-create-cancel-btn').onclick = function(){
    	roundCreateConfirmPopup.classList.add("d-none");
    	SeasonController.clearRoundConfirmTable();
    };
    
	window.onclick = function(event) {
    	if (event.target == roundCreateConfirmPopup) {
    		roundCreateConfirmPopup.classList.add("d-none");
    		SeasonController.clearRoundConfirmTable();
    	}
	}
	
	$("#round-create-form").bind("keypress", function(e) {
        if (e.keyCode == 13) {
            return false;
        }
    });
	
    document.getElementById('player-search-btn-0').onclick = function(){
    	PlayerSearchUtil.playerSearch(0);
    };
	
    document.getElementById('player-search-results-popup-close-btn').onclick = function(){
    	playerSearchResultsPopup.classList.add("d-none");
    };
    
}
