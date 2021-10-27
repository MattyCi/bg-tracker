
var RoundController = {
	
	setRoundToBeDeleted : function(roundId) {
		
		$("#round-delete-id").val(roundId);
		
	}
    
}

$( document ).ready(function() {

	$("[id^=delete-round-modal-]").each(function() {
		
		var roundId = $(this).attr("data-round-id");
		
		$("#delete-round-modal-" + roundId).click(function() {
			
			RoundController.setRoundToBeDeleted($(this).attr("data-round-id"));
			
		});
				
	});

});
