window.onload = function () {
  // Select the carousel
  const myCarousel = document.getElementById("slideshowCarousel");
  if (myCarousel) {
    //start script
    // Store all videos, all vids, slides, and progress value elements within the carousel
    let videos = myCarousel.querySelectorAll("video");
    let allVids = document.querySelectorAll(
      "#slideshowCarousel .carousel-item"
    );
    let slides = myCarousel.querySelectorAll(".carousel-item");
    let durations = [];
    let progressBars = document.querySelectorAll(
      "#slideshowCarousel .slidebutton .progress-value"
    );

    // Loop over all videos in the carousel
    allVids.forEach((el, index) => {
      // Get the video title and subtitle
      let title = el.querySelector("input.title").value;
      let subtitle = el.querySelector("input.subtitle").value;
      let slideButton = document.querySelectorAll(
        "#slideshowCarousel .slidebutton"
      );

      // If the slide button doesn't have title and subtitle, append them
      if (
        slideButton[index].querySelector(".title") == null ||
        slideButton[index].querySelector(".subtitle") == null
      ) {
        let titleElem = document.createElement("div");
        let subtitleElem = document.createElement("div");
        titleElem.className = "title";
        titleElem.textContent = title;
        subtitleElem.className = "subtitle";
        subtitleElem.textContent = subtitle;
        slideButton[index].appendChild(titleElem);
        slideButton[index].appendChild(subtitleElem);
      }
    });

    // Add event listener to the carousel for when the slide transitions
    myCarousel.addEventListener("slide.bs.carousel", (event) => {
      // Get the index of the active slide
      let activeSlideIndex = document.querySelector(".slidebutton.active")
        .dataset.bsSlideTo;
      // Set the active slide interval to the video duration
      let animationTime = durations[activeSlideIndex] + "s";
      console.log(
        "activeSlideIndex",
        activeSlideIndex,
        "animationTime",
        animationTime
      );
      let slides = document.querySelectorAll(
        "#slideshowCarousel .carousel-item"
      );
      slides[activeSlideIndex].setAttribute(
        "data-bs-interval",
        durations[activeSlideIndex] * 1000
      );

      // Get the previous video being played and set the video being transitioned to
      let pvid = slides[event.from].querySelectorAll("video")[0];
      let vid = slides[event.to].querySelectorAll("video")[0];
      // Check if the video is currently playing
      let isPlaying = vid.currentTime > 0 && vid.readyState > 2;
      // Play the video
      vid.play();
      // If the video is playing, pause the previous video
      if (isPlaying) {
        pvid.pause();
      }
    });

    // Iterate over each video in the carousel
    videos.forEach((el, index) => {
      // Get video duration
      let duration = el.duration;
      // Store duration in array
      durations[index] = duration;
      // Create progress bar inner element
      let innerElem = document.createElement("div");
      innerElem.className = "progress-inner";
      // Create progress bar
      let progBar = document.createElement("div");
      progBar.className = "progress-bar";
      // Append the inner element to the progress bar
      progBar.appendChild(innerElem);
      // Set progress bar width based on video duration
      progBar.style.width = duration + "s";
      // Append progress bar to progress value
      progressBars[index].appendChild(progBar);
      // Update slide data-interval
      slides[index].setAttribute("data-bs-interval", duration * 1000);
      // Check if the video has audio
      let audioAvailable = el.querySelector('source[type="audio/mp4"]') != null;
      // If audio is available add click event handler
      if (audioAvailable) {
        el.addEventListener("click", () => {
          if (el.paused) {
            el.play();
          } else {
            el.pause();
          }
        });
      }
    });

    // Add event handlers to video
    videos.forEach((el) => {
      // Update video progress bar when the video playing
      el.addEventListener("timeupdate", () => {
        let currentTime = el.currentTime;
        let vidIndex = [...videos].indexOf(el);
        progressBars[vidIndex].querySelector(".progress-inner").style.width =
          (currentTime / durations[vidIndex]) * 100 + "%";
      });

      // Pause video when the slide transition starts
      el.addEventListener("transitionstart", () => {
        el.pause();
      });
    });

    //end script
  } else {
    console.log("noCarousel");
  }
};
