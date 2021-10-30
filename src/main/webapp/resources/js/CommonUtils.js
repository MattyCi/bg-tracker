
$(function () {
  $('[data-toggle="popover"]').popover()
})

var SGGCommonUtils = {
	
	formatDate : function(date) {
		
		let year = date.getFullYear(),
			month = date.getMonth() + 1,
			day = date.getDate();
			formattedDate = [
				month < 10 ? '0' + month : month,
				day < 10 ? '0' + day : day,
				year,
			].join('/');
		
		document.getElementById('datepicker').value = formattedDate;
		
	}
	
}
	
