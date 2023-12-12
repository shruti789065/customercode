/* eslint-disable max-len */
import $ from "jquery";

(function () {
  "use strict";

  var LogoMenu = (function () {
    var $menuLogo, $header, $logoPath;

    /**
     * Initializes the LogoMenu
     *
     * @public
     */
    function init() {
      initializeVariables();
      addMutationObserver();
    }

    function initializeVariables() {
      $menuLogo = $(".cmp-image--logo .cmp-image__image");
      $header = $("header.cmp-experiencefragment--header");
      $logoPath = $("#logoFolder").val();
    }

    function addMutationObserver() {
      if ($header.length > 0) {
        const observer = new MutationObserver(function (mutationsList) {
          for (let mutation of mutationsList) {
            if (
              mutation.type === "attributes" &&
              mutation.attributeName === "class" &&
              $header.hasClass("scrolled-page")
            ) {
              $menuLogo.attr("src", $logoPath + "/logo-pos.svg");
            } else {
              $menuLogo.attr("src", $logoPath + "/logo-neg.svg");
            }
          }
        });

        observer.observe($header[0], {
          attributes: true,
          attributeFilter: ["class"],
        });
      }
    }

    return {
      init: init,
    };
  })();

  $(function () {
    LogoMenu.init();
  });
})($);
/* eslint-disable max-len */
