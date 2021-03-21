
var SeasonCreateUtil = {
	bggSearchResultsContainer : null,
	isNoResultsFoundMessageVisible : false,
	spinner : null,
	
	populateInitialGamesList : function() {
		
		this.bggSearchResultsContainer = document.getElementById("bgg-search-results");
		this.spinner = document.getElementById("bgg-spinner");
		
		var popularGames = boardGameGeekAPI.searchForPopularGames();
		
		this.hideSpinner();
		
		for (var i = 0; i < popularGames.length; i++) {
			this.addGameResultToDOM(popularGames[i], i);
		}
		
		this.formatLastResult();
		
		this.showBGGSearchResults();
		
	},
	
	searchForGame : function(searchTerm) {
			
		this.resetBGGContainer();
		
		var searchResultGames = boardGameGeekAPI.getGamesBySearchTerm(searchTerm);
		
		if (searchResultGames.length == 0) {
			
			this.showNoResultsFoundMessage();
			
		} else {
			
			for (var i = 0; i < searchResultGames.length; i++) {
				this.addGameResultToDOM(searchResultGames[i], i);
			}
			
		}
		
		this.formatLastResult();
		
		this.hideSpinner();
		
		this.showBGGSearchResults();
		
	},

	showNoResultsFoundMessage : function() {
		
		if (this.isNoResultsFoundMessageVisible == false) {
			
			$("#no-results-container").removeClass("d-none");
			
			this.isNoResultsFoundMessageVisible = true;
			
		}
		
	},
	
	hideNoResultsFoundMessage : function() {
		
		if (this.isNoResultsFoundMessageVisible == true) {
			
			$("#no-results-container").addClass("d-none");
			
			this.isNoResultsFoundMessageVisible = false;
			
		}
		
	},
	
	addGameResultToDOM : function(gameResult, indexOfGames) {
		
		let gameNameDiv = this.creategameNameDiv(),
			gameSelectDiv = this.createGameSelectDiv();
		
		let gameNameElement = this.createGameNameElement(gameResult),
			gameSelectButton = this.createGameSelectButton(gameResult);
		
		gameNameDiv.appendChild(gameNameElement);		
		gameSelectDiv.appendChild(gameSelectButton);
		
		$('#bgg-container').on('click', "#"+gameResult.bggId, function(event) {
			
			SeasonCreateUtil.selectGame(event.target.id);
			
		});
		
		if(typeof this.bggSearchResultsContainer !== 'undefined' && this.bggSearchResultsContainer !== null) {
			this.bggSearchResultsContainer.appendChild(gameNameDiv);
			this.bggSearchResultsContainer.appendChild(gameSelectDiv);
		}
		
	},
	
	creategameNameDiv : function() {
		
		let gameNameDiv = document.createElement("div");
		gameNameDiv.classList.add("col-8", "mb-4", "pb-4", "border-bottom", "border-primary", "text-left", "sgg-align-middle");
		return gameNameDiv;
		
	},
	
	createGameSelectDiv : function() {
		
		let gameSelectDiv = document.createElement("div");
		gameSelectDiv.classList.add("col-4", "mb-4", "pb-4", "border-bottom", "border-primary", "sgg-align-middle");
		return gameSelectDiv;
		
	},
	
	createGameNameElement : function(gameResult) {
		
		let gameNameElement = document.createElement("h5");
		
		gameNameElement.id = "bgg-name-" + gameResult.bggId;
		gameNameElement.textContent = gameResult.name + " (" + gameResult.yearPublished + ")";
		
		return gameNameElement;
		
	},
	
	createGameSelectButton : function(gameResult) {
		
		let gameSelectButton = document.createElement("button");
		
		gameSelectButton.type = "button";
		gameSelectButton.classList.add("btn", "btn-primary", "btn-lg");
		gameSelectButton.textContent = "Select";
		gameSelectButton.id = gameResult.bggId;
		
		return gameSelectButton;
		
	},
	
	selectGame : function(bggId) {
		
		let gameName = $("#bgg-name-" + bggId).text();
		imgSrc = boardGameGeekAPI.getGameImagePath(bggId);
	
		$("#selected-bgg-game-id").val(bggId);
		$("#selected-bgg-game-name").val(gameName);
		
		$("#selected-game-name").text(gameName);
		$("#selected-game-img").attr("src", imgSrc);
		
		$("#bgg-container").addClass("border-bottom-0");
		$("#selected-game-container").removeClass("d-none", "border-primary").addClass("border-success");
		
		$([document.documentElement, document.body]).animate({
        	scrollTop: $("#selected-game-container").offset().top - 100
	    }, 1000);
		
		setTimeout(function() {
						
			$("#selected-game-container").removeClass("border-success").addClass("border-primary");
			
		}, 1000);
		
	},
	
	hideSpinner : function() {
		
	    if(typeof this.spinner !== 'undefined' && this.spinner !== null) {
			this.spinner.classList.add("d-none");
		}
		
	},
	
	showSpinner : function() {
		if(typeof this.spinner !== 'undefined' && this.spinner !== null) {
			this.spinner.classList.remove("d-none");
		}
	},
	
	resetBGGContainer : function() {
		this.hideNoResultsFoundMessage();
		$("#bgg-search-results").empty(); 
		this.showSpinner();
	},
	
	formatLastResult : function() {
		
		let lastChild = $("#bgg-search-results").children().last();
		
		lastChild.removeClass("border-bottom mb-4");
		lastChild.prev().removeClass("border-bottom mb-4");
		
	},
	
	showBGGSearchResults : function() {
		
		if(typeof this.bggSearchResultsContainer !== 'undefined' && this.bggSearchResultsContainer !== null) {
			this.bggSearchResultsContainer.classList.remove("d-none");
		}
		
	}
	
}

$(document).ready(function() {
	
	SeasonCreateUtil.populateInitialGamesList();
	
	$("#game-search-input").keydown(function(event) {
		
		if(event.keyCode == 13) {
			
			event.preventDefault();
			let searchTerm = $("#game-search-input").val();
			SeasonCreateUtil.searchForGame(searchTerm);
			
		}
		
	});
	
	$("#game-search-button").click(function(event) {
		
		  let searchTerm = $("#game-search-input").val();
		  
		  SeasonCreateUtil.searchForGame(searchTerm);
		  
	});
	
});
