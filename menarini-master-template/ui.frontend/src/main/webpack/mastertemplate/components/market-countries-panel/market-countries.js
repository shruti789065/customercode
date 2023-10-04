/* eslint-disable max-len */
import $ from "jquery";

(function () {
  "use strict";

  var MarketCountries = (function () {
    const marketCountriesAnchor = document.getElementById("marketCountries");
    const hiddenInputs = document.querySelectorAll('input[type="hidden"]');
    const marketCountriesContainer = document.getElementById(
      "marketCountriesContainer"
    );
    const marketCountriesCloseButton = document.getElementById(
      "marketCountriesClose"
    );

    for (const input of hiddenInputs) {
      const dataLogoValue = input.getAttribute("data-logo");

      if (dataLogoValue !== null && marketCountriesContainer) {
        marketCountriesContainer.style.backgroundImage = `
		  linear-gradient(
			to bottom,
			#143F59,
			#143F59,
			rgb(20 63 89 / 0%)
		  ),
		  url(${dataLogoValue})
		`;
      }
    }

    /**
     * Initializes the MarketCountries
     *
     * @public
     */
    function init() {
      marketCountriesAnchor.addEventListener("click", toggleMarketPanel);
      marketCountriesCloseButton.addEventListener("click", toggleMarketPanel);
    }

    function toggleMarketPanel() {
      if (marketCountriesContainer) {
        const currentDisplayStyle = window
          .getComputedStyle(marketCountriesContainer)
          .getPropertyValue("display");
        if (currentDisplayStyle === "none") {
          marketCountriesContainer.style.display = "block";
        } else {
          marketCountriesContainer.style.display = "none";
        }
      }
    }

    return {
      init: init,
    };
  })();

  $(function () {
    MarketCountries.init();
  });
})($);
