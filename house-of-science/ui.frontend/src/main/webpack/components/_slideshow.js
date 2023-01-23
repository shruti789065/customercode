/* eslint-disable max-len */

import jQuery from "jquery";

jQuery(function ($) {


	const myCarousel = document.getElementById('carouselExampleIndicators');
	var videos = myCarousel.querySelectorAll('video');
	var allVids = $("#carouselExampleIndicators").find('.carousel-item');
	var slides = myCarousel.querySelectorAll('.carousel-item');
	var durations = [];

	allVids.each(function (index, el) {
		if (index !== 0) {
			$(this).find('video')[0].pause();
		}
		durations.push(videos[index].duration);
		//in caso di carousel misto questo parametro dovrà prevedere un valore standard per le img
		slides[index].setAttribute('data-bs-interval', durations[index]*1000);
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
		document.querySelector('.slidebutton.active .progress-value').style.animationDuration = animationTime;

		if (isPlaying) {
			pvid.pause();
		}
	});





	/* carousel.addEventListener('mouseenter', pauseProgressBar, false);
	carousel.addEventListener('touchstart', pauseProgressBar, false);

	carousel.addEventListener('mouseleave', resumeProgressBar, false);
	carousel.addEventListener('touchend', resumeProgressBar, false); */




	function pauseProgressBar() {
		//document.querySelector('.progress-value').style.animationPlayState = 'paused';

	}
	function resumeProgressBar() {
		//document.querySelector('.progress-value').style.animationPlayState = 'running';

	}




	/* var remainingTime;
	var startTime;

	function startTimer(element) {
		startTime = new Date();
		remainingTime = setTimeout(function () {
			//codice da eseguire alla fine del timer
		}, 6000);

		element.addEventListener('mouseenter', function () {
			clearTimeout(remainingTime);
		});

		element.addEventListener('mouseleave', function () {
			var elapsedTime = new Date() - startTime;
			var remaining = 6000 - elapsedTime;
			remainingTime = setTimeout(function () {
				//codice da eseguire alla fine del timer
			}, remaining);
		});
	}


	

	$('.carousel').carousel({
		interval: 6000,
		pause: "hover",
		ride: 'carousel'
	});

	$('.carousel').on('slid.bs.carousel', function () {
		clearTimeout(remainingTime);
		remainingTime = setTimeout(function () {
			$('.carousel').carousel('next');
		}, 6000);
	});

	$('.carousel').hover(
		function () {
			clearTimeout(remainingTime);
		},
		function () {
			remainingTime = setTimeout(function () {
				$('.carousel').carousel('next');
			}, 6000);
		}
	); */


	//document.querySelector('.progress-value').style.animationDuration  = '5000';

	/* 
		carousel.addEventListener('mouseenter', pauseProgressBar, false);
		carousel.addEventListener('touchstart', pauseProgressBar, false);
	
		carousel.addEventListener('mouseleave', resumeProgressBar, false);
		carousel.addEventListener('touchend', resumeProgressBar, false); */




	/* function pauseProgressBar() {
		//document.querySelector('.progress-value').style.animationPlayState = 'paused';
		myCarousel.Carousel('pause');
	}
	function resumeProgressBar() {
		//document.querySelector('.progress-value').style.animationPlayState = 'running';
		carousel.Carousel('cycle');
	} */

	/* $(function() {
	  //const myTimeout = setTimeout(setProgressBar(), 25);
	  setProgressBar();
	  const myCarousel = document.getElementById('carouselExampleIndicators');
	  myCarousel.addEventListener('slide.bs.carousel', event => {
		//myTimeout = setTimeout(setProgressBar(), 25);
		index_old=getCurrentSlideIndex();
	  //alert(index_old);
		setProgressBar();
	  });
	});
    
	function getCurrentSlideIndex(){
	  var id_active_button="";
	  var current_index=0;
	  if ( $( ".button_container button.active" ).length ) {
		id_active_button = $('.button_container button.active').attr('id');
		current_index = parseInt(id_active_button.split("_").pop());
	  }
	  else{
		id_active_button=0;
	  }
	  return current_index;
	}
  
	function getCurrentDataBsInterval(){
	  return parseInt($(".carousel-item.active").attr("data-bs-interval")); 
	}
  
	function setProgressBar(){
	  var current_progress = 0;
	  
	  var offsetProgressBar=getCurrentDataBsInterval()/100;
	  var interval=setInterval(function() {
		var actualSlide=getCurrentSlideIndex();
		current_progress += 1;
		$(".slidebutton[data-bs-slide-to="+actualSlide+"] .dynamic")
		.css("width", current_progress + "%")
		.css("background-color", "#B62623")
		.css("height", "3px")
		.attr("aria-valuenow", current_progress);
		if (current_progress >= 100){
			clearInterval(interval);
			$(".slidebutton .dynamic")
			.css("background-color", "rgba(255, 255, 255, 0.5)");
		  }
		}, offsetProgressBar);
	}
  
	$( "button.slidebutton" ).click(function() {
	  //alert(index_old);
	  //$(".slidebutton[data-bs-slide-to="+index_old+"] .dynamic")
	  $("#slidebutton_"+index_old+" .dynamic")
	  .css("background-color", "rgba(255, 255, 255, 0.5)");
	  var perc = ($("#slidebutton_"+index_old+" .dynamic").width() / $('#slidebutton_'+index_old+' .dynamic').parent().width() * 100).toFixed();
	  alert("la percentuale a cui si ferma la barra è "+perc+"%");   
	  index_clicked=getCurrentSlideIndex();
	  $("#slidebutton_"+index_old+" .dynamic")
	  .css("width", "0%")
	  .attr("aria-valuenow", 0);
	  setProgressBar();
	});
  
	$( ".carousel-inner" ).hover(
	  function() {
		$( this ).addClass( "hover" );
	  }, function() {
		$( this ).removeClass( "hover" );
	  }
	); */

});

