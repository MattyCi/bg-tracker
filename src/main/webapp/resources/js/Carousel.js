
var CarouselUtil = {

	currentSlide : 0,
	textSlides : null,
	imgSlides : null,
	slideSelectors : null,
	beginCycleSlideTimeoutId : null,
	activateSlideTimeoutId : null,
	slideSelected : false,
	
	init: function() {
		this.textSlides = $("[id*=carousel-text-]");
		this.imgSlides = $("[id*=carousel-img-]");
		this.slideSelectors = $("[id*=carousel-selector-]");
	},

	changeSlide: function(selectedSlide) {
		
		clearTimeout(CarouselUtil.beginCycleSlideTimeoutId);
		clearTimeout(CarouselUtil.activateSlideTimeoutId);
		CarouselUtil.slideSelected = true;
		
		CarouselUtil.deactivateSlide(CarouselUtil.currentSlide);
		
		CarouselUtil.currentSlide = selectedSlide;
		
		CarouselUtil.activateSlide(selectedSlide);
		
		CarouselUtil.currentSlide = selectedSlide;
		
	},

	cycleSlides: function() {
		
		CarouselUtil.beginCycleSlideTimeoutId = setTimeout(function() {
			
			CarouselUtil.deactivateSlide(CarouselUtil.currentSlide);

			if (CarouselUtil.currentSlide === 2) {
				CarouselUtil.currentSlide = 0;
			} else {
				CarouselUtil.currentSlide++;
			}

			CarouselUtil.activateSlide(CarouselUtil.currentSlide);

		}, 3000);
	
	},
	
	deactivateSlide: function(oldSlidePosition) {
		
		CarouselUtil.textSlides.eq(oldSlidePosition).fadeOut();
		CarouselUtil.imgSlides.eq(oldSlidePosition).fadeOut();
		CarouselUtil.slideSelectors.eq(oldSlidePosition).removeClass("active");
		
	},
	
	activateSlide: function(newSlidePosition) {
		
		CarouselUtil.activateSlideTimeoutId = setTimeout(function() {
					
			CarouselUtil.textSlides.eq(newSlidePosition).fadeIn();
			CarouselUtil.imgSlides.eq(newSlidePosition).fadeIn();
			CarouselUtil.slideSelectors.eq(newSlidePosition).addClass("active");
				
			if (!CarouselUtil.slideSelected) {
				CarouselUtil.cycleSlides();
			}
					
		}, (500));
	
	},
	
	deactivateSlideSelectors: function() {
		
		// TODO
		
	},

};

$(document).ready(function() {
	CarouselUtil.init();
	CarouselUtil.cycleSlides();
	
	CarouselUtil.slideSelectors.click(function() {
		
		let slide = $(this).data('slide');
		
		if (slide != CarouselUtil.currentSlide) {
			CarouselUtil.changeSlide(slide);
		}
		
	});
	
});

