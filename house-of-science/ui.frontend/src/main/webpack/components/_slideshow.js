/* eslint-disable max-len */
import jQuery from "jquery";

// Wrap the code in a self-invoking anonymous function to avoid polluting the global scope
jQuery(function ($) {

	const myCarousel = document.getElementById('slideshowCarousel');					// Get the carousel element from the DOM
	if(myCarousel){
	
		var videos = myCarousel.querySelectorAll('video');									// Get all the videos in the carousel
	var allVids = $("#slideshowCarousel").find('.carousel-item');						// Get all the carousel items
	var slides = myCarousel.querySelectorAll('.carousel-item');							// Get all the slides in the carousel
	var durations = [];																	// Array to store the durations of all videos
	var progressBars = $("#slideshowCarousel").find('.slidebutton .progress-value'); 
	
	

	// Get all the progress bars
		// Pause all videos except the first one
		allVids.each(function (index) {
			if (index !== 0) {
				$(this).find('video')[0].pause();
			}
		});

		// Calculate the duration of all videos
		for (let i = 0; i < videos.length; i++) {
			videos[i].onloadedmetadata = function () {
				durations.push(videos[i].duration);

				// Update the interval time of each carousel item with the duration of the video
				slides[i].setAttribute('data-bs-interval', durations[i] * 1000);

				// Update the animation duration of each progress bar with the duration of the video
				progressBars[i].style.animationDuration = durations[i] + "s";
			};
		}

		// Add title and subtitle text to the progress bars
		allVids.each(function (index) {
			let title = $(this).find('input.title').val();
			let subtitle = $(this).find('input.subtitle').val();
			let slideButton = $("#slideshowCarousel").find('.slidebutton');
			// Check if the title and subtitle elements already exist in the progress bar
			if((slideButton[index].querySelector('.title') == null) || (slideButton[index].querySelector('.subtitle')  == null)){
				let titleElem = document.createElement('div');
				let subtitleElem = document.createElement('div');
				titleElem.className = 'title';
				titleElem.textContent = title;
				subtitleElem.className = 'subtitle';
				subtitleElem.textContent = subtitle;
				slideButton[index].append(titleElem);
				slideButton[index].append(subtitleElem);
			}			
		});
		
		// Add an event listener to the carousel that listens for when the carousel slides to a new item
		myCarousel.addEventListener('slide.bs.carousel', event => {
			// Get the index of the active slide
			let activeSlideIndex = document.querySelector('.slidebutton.active').dataset.bsSlideTo;
			// Get the animation time for the current active slide
			//let animationTime = durations[activeSlideIndex] + "s";
			//console.log("activeSlideIndex", activeSlideIndex, "animationTime", animationTime);
			// Select all carousel items
			let slides = $(this).find('.carousel-item');
			// Set the interval time for the carousel items based on the active slide
			slides.data("bs-interval", durations[activeSlideIndex] * 1000);
			// Select the previous video in the carousel
			let pvid = slides[event.from].querySelectorAll('video')[0];
			// Select the current video in the carousel
			let vid = slides[event.to].querySelectorAll('video')[0];
			// Check if the current video is playing
			let isPlaying = vid.currentTime > 0 && vid.readyState > 2;
			// Play the current video
			vid.play();
			// If the current video is playing, pause the previous video
			if (isPlaying) {
				pvid.pause();
			}
		});
	}
});
