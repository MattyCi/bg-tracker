
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
		
		var inputContainer = document.getElementById("player-add-container-"+(this.addedPlayerCount-1));
		var clonedContainer = inputContainer.cloneNode(true);
		
		clonedContainer.id = 'player-add-container-'+this.addedPlayerCount;
		
		clonedContainer.children[0].children[0].name = 'roundPlayer'+this.addedPlayerCount;
			
		clonedContainer.children[1].children[0].name = 'playerPlace'+this.addedPlayerCount;
		
		// make the place one more than the prior value in the placeholder text
		clonedContainer.children[1].children[0].value = inputContainer.children[1].children[0].valueAsNumber+1;
		
		inputContainer.parentNode.insertBefore(clonedContainer, inputContainer.nextSibling);
		
		this.addedPlayerCount++;
	},
	
	confirmAddRound : function(form) {
		var roundResultsNames = [];
		var roundResultsPlaces = [];
		
		var roundResultCount = 0;
		for (var i = 0, element; element = form.elements[i++];) {
			if (element.getAttribute("data-round-info") === 'true') {
				
				if (element.type === "select-one") {
					roundResultsNames[roundResultCount] = element.selectedOptions[0].innerHTML;
				} else {
					roundResultsPlaces[roundResultCount] = element.value;
					roundResultCount++;
				}
		    }
		}
		
		this.buildRoundCreateConfirmPopup(roundResultsNames, roundResultsPlaces);
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

    var roundCreateConfirmPopup = document.getElementById("round-create-confirm-popup");
	window.onclick = function(event) {
    	if (event.target == roundCreateConfirmPopup) {
    		roundCreateConfirmPopup.classList.add("d-none");
    		SeasonController.clearRoundConfirmTable();
    	}
	}
    
}
