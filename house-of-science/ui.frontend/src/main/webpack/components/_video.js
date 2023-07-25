import jQuery from "jquery";

// Wrap bindings in anonymous namespace to prevent collisions
jQuery(function ($) {
    "use strict";

        var screenWidth = $(window).width();
        if (screenWidth > 1200) {
            //$('.video--teaser .video-container .video.autoplay').attr('autoplay', 'autoplay');
            $(".video--teaser").hover(function () {
                $(this).children(".video-container").children(".video.autoplay")[0].pause();
            }, function () {
                var el = $(this).children(".video-container").children(".video.autoplay")[0];
                el.play();
            });
        } else {
            $('.video-container').click(function () {
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

