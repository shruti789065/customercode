/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable max-len */
(function () {
	"use strict";
  
	const MENU = {
		DESKTOP: ".menu-desktop",
		MOBILE: ".menu-mobile"
	};

	/**
	 * Initializes the Language Navigation
	 *
	 * @public
	 */
	function init() {
		/* try {
			moveTablistToHeader();
		} catch (error) {
			console.error("Error initializing tablist move:", error);
		} */
	}
  
	/**
	 * Moves the tablist to the specified header container
	 */
	/* function moveTablistToHeader() {
		const tablist = document.querySelector(".menu-desktop .cmp-tabs__tablist");
		const headerContainer = document.querySelector("#header--tablist");

		if (!tablist) {
			console.warn("Tablist element not found.");
			return;
		}

		if (!headerContainer) {
			console.warn("Header container element not found.");
			return;
		}

		// Ensure tablist is not already inside the header container
		if (!headerContainer.contains(tablist)) {
			headerContainer.appendChild(tablist);
			console.log("Tablist successfully moved to header container.");
		} else {
			console.warn("Tablist is already inside the header container.");
		}
	} */
  
	document.addEventListener("DOMContentLoaded", init);
})();
