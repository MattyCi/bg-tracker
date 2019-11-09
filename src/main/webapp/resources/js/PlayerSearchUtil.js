
var PlayerSearchUtil = {

	playerSearchResultsStatusTextElement : null,
	playerSearchResultsSpinner : null,
	playerSearchResultsList: null,

	recentPlayersSelectElement : null,

	playerSearch : function(addedPlayerIndex) {

		this.setRecentPlayersSelectElement(addedPlayerIndex);

		var playerSearchInput = document.getElementById('player-search-input-'+addedPlayerIndex);

		if (this.checkPlayerSearchInput(playerSearchInput)) {

			this.showPlayerSearchPopup();

			this.initiatePlayerSearch(playerSearchInput.value);

		}

	},

	setRecentPlayersSelectElement : function(addedPlayerIndex) {

		this.recentPlayersSelectElement = document.getElementById('recent-players-select-'+addedPlayerIndex);

	},

	checkPlayerSearchInput : function(playerSearchInput) {

		// TODO: create a generic helpers module for this kind of thing...
		if (playerSearchInput == null || playerSearchInput.value.length === 0) {

			playerSearchInput.classList.add('is-invalid');

			alert("Player name must not be empty.");

			return false;
		}

		playerSearchInput.classList.remove('is-invalid');

		return true;

	},

	showPlayerSearchPopup : function() {
		document.getElementById("player-search-results-popup").classList.remove("d-none");
	},

	hidePlayerSearchPopup : function() {
		document.getElementById("player-search-results-popup").classList.add("d-none");
	},

	initiatePlayerSearch : function(playerSearchParameter) {

		this.playerSearchResultsList.classList.add('d-none');

		this.hideNoResultsErrorMessage();

		this.showSpinner();

		var playerSearchParams = this.buildPlayerSearchParams(playerSearchParameter),
			searchForPlayerUrl = "searchForPlayer",
			playerSearchPostRequest = jQuery.post( searchForPlayerUrl, playerSearchParams );

		playerSearchPostRequest.done(function(playerSearchResults) {

			PlayerSearchUtil.processSearchResults(playerSearchResults);

		});

		playerSearchPostRequest.fail(function() {

			PlayerSearchUtil.playerSearchResultsStatusTextElement.innerHTML = "Sorry... something went wrong on our end!";

		});

		playerSearchPostRequest.always(function() {

			PlayerSearchUtil.removeSpinner();

		});

	},

	buildPlayerSearchParams : function(playerSearchParameter) {

		var fullname = playerSearchParameter.split(' ');
			firstName = fullname[0];
			lastName = this.buildLastName(fullname);

		playerSearchParams = {
			"firstName" : firstName,
			"lastName"  : lastName
		}

		return playerSearchParams;

	},

	buildLastName : function(fullname) {

		var lastName = "";

		for (var i = 1; i < fullname.length; i++) {
			lastName += fullname[i];

			if (fullname[i+1] != null) {
				lastName += ' ';
			}

		}

		return lastName;

	},

	processSearchResults : function(playerSearchResults) {

		if (PlayerSearchUtil.searchReturnedErrors(playerSearchResults)) {
			return;
		}

		PlayerSearchUtil.clearPlayerSearchResultsTable();

		PlayerSearchUtil.playerSearchResultsStatusTextElement.innerHTML = "Select a player below.";

		PlayerSearchUtil.playerSearchResultsList.classList.remove('d-none');

		for (var i = 0; i < playerSearchResults.length; i++) {

			PlayerSearchUtil.addPlayerSearchResultToTable(playerSearchResults[i]);

		}


	},

	searchReturnedErrors : function(playerSearchResults) {

		if (playerSearchResults.length === 0) {

			this.displayNoResultsFound();

			return true;

		} else if (playerSearchResults.errorMessage != null) {

			this.displaySearchError(playerSearchResults.errorMessage);

			return true;

		}

		return false;

	},

	displayNoResultsFound : function() {

		this.removeSpinner();

		this.showSearchErrorMessage('<strong>No results found...</strong> Try searching again.');

	},

	displaySearchError : function(errorMessage) {

		this.removeSpinner();

		this.showSearchErrorMessage(errorMessage);

	},

	removeSpinner : function() {

		document.getElementById("player-search-results-popup-spinner").classList.remove("d-flex");

		document.getElementById("player-search-results-popup-spinner").classList.add("d-none");

	},

	showSpinner : function() {

		this.playerSearchResultsSpinner.classList.remove('d-none');

		this.playerSearchResultsSpinner.classList.add('d-flex');

	},

	showSearchErrorMessage : function(errorMessage) {

		document.getElementById('player-search-error').classList.remove('d-none');

		document.getElementById('player-search-error-text').innerHTML = errorMessage;

	},

	hideNoResultsErrorMessage : function() {
		document.getElementById('player-search-error').classList.add('d-none');
	},

	clearPlayerSearchResultsTable : function() {
		var emptyTableData = document.createElement('tbody');
		var oldTableData = document.getElementById('player-search-results-list-table-body');
		emptyTableData.id = 'player-search-results-list-table-body';

		oldTableData.parentNode.replaceChild(emptyTableData, oldTableData);

	},

	addPlayerSearchResultToTable : function(playerSearchResults) {
		var playerSearchResultsTable = document.getElementById("player-search-results-list-table-body"),
			newRow = playerSearchResultsTable.insertRow(-1),
			playerFirstNameCell = newRow.insertCell(0),
			playerLastNameCell = newRow.insertCell(1);

		playerFirstNameCell.innerHTML = playerSearchResults.firstName;
		playerLastNameCell.innerHTML = playerSearchResults.lastName;

		newRow.classList.add('py-2', 'clickable');

		this.addPlayerSelectionAbilityForRow(newRow, playerSearchResults);

	},

	addPlayerSelectionAbilityForRow : function(newRow, playerSearchResults) {

		newRow.onclick = function() {
			
			if (PlayerSearchUtil.isPlayerAlreadyInRecentPlayerList(playerSearchResults.userId)) {
				PlayerSearchUtil.setRecentPlayersSelectElementValue(playerSearchResults.userId);
			} else {
				PlayerSearchUtil.addNewPlayerOption(playerSearchResults);
			}

			PlayerSearchUtil.makeRecentPlayersSelectElementStyledValid();

			setTimeout(function(){ PlayerSearchUtil.removeRecentPlayersSelectElementStyledValid(); }, 3000);

			PlayerSearchUtil.hidePlayerSearchPopup();

		}

	},

	isPlayerAlreadyInRecentPlayerList : function(selectedUsersId) {

		var options = PlayerSearchUtil.recentPlayersSelectElement.options;

		for (var option, i = 0; option = options[i]; i++) {

			if (option.value == selectedUsersId) {
				return true;
			}

		}

		return false;

	},

	addNewPlayerOption : function(playerSearchResults) {

		var playerSelectOption = document.createElement("option");

		playerSelectOption.text = playerSearchResults.firstName + ' ' + playerSearchResults.lastName;

		playerSelectOption.value = playerSearchResults.userId;

		PlayerSearchUtil.recentPlayersSelectElement.add(playerSelectOption);

		this.setRecentPlayersSelectElementValue(playerSearchResults.userId);

	},

	setRecentPlayersSelectElementValue : function(userId) {
		this.recentPlayersSelectElement.value = userId;
	},

	makeRecentPlayersSelectElementStyledValid : function() {
		this.recentPlayersSelectElement.classList.add('is-valid-border');
	},

	removeRecentPlayersSelectElementStyledValid : function() {
		this.recentPlayersSelectElement.classList.remove('is-valid-border');
	}

}

$( document ).ready(function() {

	PlayerSearchUtil.playerSearchResultsStatusTextElement = document.getElementById('player-search-results-popup-status-text');

	PlayerSearchUtil.playerSearchResultsSpinner = document.getElementById('player-search-results-popup-spinner');

	PlayerSearchUtil.playerSearchResultsList = document.getElementById('player-search-results-list');

});
