/* eslint-disable max-len */
import $ from "jquery";
import "../tabsmenu/_tabs-menu";
import { _isDesktop } from "../../site/_util.js";

const MarketCountries = (() => {
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
	const mobileBtn = document.querySelector(
		".cmp-button--mobile"
	  );
    const marketCountriesAnchor = document.querySelectorAll(
      ".cmp-countries-markets"
    );
    marketCountriesAnchor.forEach((element) => {
      element.addEventListener("click", toggleMarketPanel);
    });
	if(marketCountriesCloseButton != null){
		marketCountriesCloseButton.addEventListener("click", toggleMarketPanel);
	} 
    const observer = new MutationObserver((mutationsList, observer) => {
      // Itera attraverso le mutazioni osservate
      for (const mutation of mutationsList) {
        // Verifica se la classe cmp-button--mobile__toggler_close è stata aggiunta al DOM
        if (
          mutation.target.classList.contains(
            "cmp-button--mobile__toggler_close"
          )
        ) {
          const menuCloseButton = document.querySelector(
            ".cmp-button--mobile__toggler_close"
          );
          menuCloseButton.addEventListener("click", () => {marketCountriesContainer.style.left = "100%";});
        }
      }
    });

    observer.observe(mobileBtn, { attributes: true, attributeFilter: ["class"] });
  }

  function toggleMarketPanel() {
    if (marketCountriesContainer) {
      if (_isDesktop()) {
        const currentDisplayStyle = window
          .getComputedStyle(marketCountriesContainer)
          .getPropertyValue("display");
        currentDisplayStyle === "none"
          ? (marketCountriesContainer.style.display = "block")
          : (marketCountriesContainer.style.display = "none");
      } else {
        const currentPosition = window
          .getComputedStyle(marketCountriesContainer)
          .getPropertyValue("left");
        currentPosition == "0px"
          ? (marketCountriesContainer.style.left = "100%")
          : (marketCountriesContainer.style.left = "0px");
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
