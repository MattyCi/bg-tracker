
class BGGAPI {
	
	constructor() {
		this.xmlParser = new DOMParser();
	}

	searchForPopularGames() {
		
        var request = new XMLHttpRequest();
        request.open("GET", "https://www.boardgamegeek.com/xmlapi2/hot?type=boardgame", false);
        request.send(null);
        
        var popularGames = this.parseResults(request.responseText);
        
        return popularGames;
        
	}
	
	getGamesBySearchTerm(searchTerm) {
		
        var request = new XMLHttpRequest();
        request.open("GET", "https://www.boardgamegeek.com/xmlapi2/search?type=boardgame&query=" + searchTerm, false);
        request.send(null);
        
        var searchResultGames = this.parseResults(request.responseText);
        
        return searchResultGames;
		
	}
	
	getGameImagePath(bggId) {
		
		var imagePath = "/resources/img/missing-game-image.png";
		
        var request = new XMLHttpRequest();
        request.open("GET", "https://www.boardgamegeek.com/xmlapi2/thing?id=" + bggId, false);
       
        try {
        	 request.send(null);
        } catch(e) {
        	console.error(e);
        	return imagePath;
        }
       
        
        var xmlDoc = this.xmlParser.parseFromString(request.responseText, "text/xml");
        
        var thumbnailNode = xmlDoc.getElementsByTagName("thumbnail")[0];
        
        if (thumbnailNode == undefined) {
        	return imagePath;
        } else {
        	var imagePath = thumbnailNode.childNodes[0].nodeValue;
        }
        
        return imagePath;
		
	}
	
	getGameById(bggId) {
		
        var request = new XMLHttpRequest();
        request.open("GET", "https://www.boardgamegeek.com/xmlapi2/thing?id=" + bggId, false);
        
        request.send(null);
        
        var game = this.parseResults(request.responseText);
        
        return game;
		
	}
	
	parseResults(xmlResponse) {
		
		var xmlDoc = this.xmlParser.parseFromString(xmlResponse, "text/xml"),
			items = xmlDoc.getElementsByTagName("item"),
			bggGames = []; 
		
		for (var i = 0; i < items.length; i++) {
			
			let bggId = items[i].getAttribute('id');
			
			var bggGame = {bggId:bggId};
			
			for (var j = 0; j < items[i].childNodes.length; j++) {
				
				let childNode = items[i].childNodes[j];
				
				if (childNode.nodeName === "name")
					bggGame.name = childNode.getAttribute('value');
				
				if (childNode.nodeName === "yearpublished")
					bggGame.yearPublished = childNode.getAttribute('value');
				
				if (childNode.nodeName === "thumbnail") {
					
					bggGame.thumbnail = childNode.getAttribute('value');
					
					if (bggGame.thumbnail == null || bggGame.thumbnail == undefined)
						bggGame.thumbnail = childNode.childNodes[0].nodeValue;
					
				}
				
			}
			
			bggGames.push(bggGame);
		}
		
		return bggGames;
		
	}
  
}

var boardGameGeekAPI = new BGGAPI();
