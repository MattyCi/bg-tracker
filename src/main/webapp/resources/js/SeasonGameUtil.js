
var SeasonGameUtil = {
		
	setup : function(bggId) {
		
		let seasonGames = boardGameGeekAPI.getGameById(bggId),
			seasonGame = seasonGames[0];
		
		if (seasonGame.thumbnail == null || seasonGame.thumbnail == undefined) {
			seasonGame.thumbnail = "/resources/img/missing-game-image.png";
		}
		
		$("#season-game-image").attr("src", seasonGame.thumbnail);
		
	}
		
}

$( document ).ready(function() {
	
	let bggId = $("#season-game-name").data("bgg-id");
	SeasonGameUtil.setup(bggId);
	
});
