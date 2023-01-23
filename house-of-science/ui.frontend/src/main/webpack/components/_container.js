import jQuery from "jquery";

// Wrap bindings in anonymous namespace to prevent collisions
jQuery(function ($) {
    "use strict";
    
        if($(".teaser.card").parent().hasClass("aem-Grid")){
            $(".teaser.card").parent().addClass("cmp-container_card-padding");
        } 
})