import jQuery from "jquery";

// Wrap bindings in anonymous namespace to prevent collisions
jQuery(function ($) {
  "use strict";

  var screenWidth = $(window).width();
  if (screenWidth > 1200) {
    //$('.video--teaser .video-container .video.autoplay').attr('autoplay', 'autoplay');
    $(".video--teaser").on("mouseenter", function () {
      if ($(this).find(".video-container").has(".autoplay").length > 0) {
        $(this)
          .children(".video-container")
          .children(".video.autoplay")[0]
          .pause();
      }
    });
    $(".video--teaser").on("mouseleave", function () {
      if ($(this).find(".video-container").has(".autoplay").length > 0) {
        var el = $(this)
          .children(".video-container")
          .children(".video.autoplay")[0];
        el.play();
      }
    });
  } else {
    $(".video-container").on("click", function () {
      if ($(this).children(".video.autoplay")[0].paused) {
        $(this).children(".video.autoplay")[0].play();
        $(this).children(".playpause").fadeOut();
      } else {
        $(this).children(".video.autoplay")[0].pause();
        $(this).children(".playpause").fadeIn();
      }
    });
  }
});
