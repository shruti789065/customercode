/* eslint-disable max-len */

import jQuery from "jquery";

jQuery(function ($) {


	const myCarousel = document.getElementById('slideshowCarousel');
	var videos = myCarousel.querySelectorAll('video');
	var allVids = $("#slideshowCarousel").find('.carousel-item');
	var slides = myCarousel.querySelectorAll('.carousel-item');
	var durations = [];
	var progressBars = $("#slideshowCarousel").find('.slidebutton .progress-value');

		allVids.each(function (index, el) {
			if (index !== 0) {
				$(this).find('video')[0].pause();
			}
		});

		for (let i = 0; i < videos.length; i++) {
			videos[i].onloadedmetadata = function () {
				durations.push(videos[i].duration);
				slides[i].setAttribute('data-bs-interval', durations[i] * 1000);
				progressBars[i].style.animationDuration = durations[i] + "s";
			};
		}

		allVids.each(function (index, el) {
			let title = $(this).find('input.title').val();
			let subtitle = $(this).find('input.subtitle').val();
			let slideButton = $("#slideshowCarousel").find('.slidebutton');
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

		
		myCarousel.addEventListener('slide.bs.carousel', event => {
			let activeSlideIndex = document.querySelector('.slidebutton.active').dataset.bsSlideTo;
			let animationTime = durations[activeSlideIndex] + "s";
			//console.log("activeSlideIndex", activeSlideIndex, "animationTime", animationTime);

			let slides = $(this).find('.carousel-item');
			slides.data("bs-interval", durations[activeSlideIndex] * 1000);
			let pvid = slides[event.from].querySelectorAll('video')[0];
			let vid = slides[event.to].querySelectorAll('video')[0];
			let isPlaying = vid.currentTime > 0 && vid.readyState > 2;

			vid.play();

			if (isPlaying) {
				pvid.pause();
			}
		});



});