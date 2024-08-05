/* eslint-disable max-len */
(function () {
	"use strict";

	const HEADER = {
	  
	  mobile: document.querySelector(".cmp-experiencefragment--header-mobile"),
	  toggleButton: document.getElementById("btnMenuMobile"),
	  navbar: document.getElementById("navbar"),
	  headerTablist: document.getElementById("header--tablist"),
	  activeTabClass: 'cmp-tabs__tab--active'
	};

	let lastClickedTab = null;

	/**
	 * Toggles the desktop menu visibility based on the clicked tab
	 *
	 * @private
	 */
	function handleTabClick(event) {
	  const clickedTab = event.target.closest('.cmp-tabs__tab');
	  if (!clickedTab) {return;}

	  const isAlreadyActive = clickedTab.classList.contains(HEADER.activeTabClass);

	  if (isAlreadyActive && lastClickedTab === clickedTab) {
		// Close the menu if the same tab is clicked again
		HEADER.desktopMenu.classList.remove("menu-desktop--opened");
		lastClickedTab = null;
	  } else {
		// Open the menu and activate the clicked tab
		HEADER.desktopMenu.classList.add("menu-desktop--opened");
		setActiveTab(clickedTab);
		lastClickedTab = clickedTab;
	  }
	}

	/**
	 * Sets the given tab as active and removes the active state from others
	 *
	 * @private
	 */
	function setActiveTab(tab) {
	  const tabs = HEADER.headerTablist.querySelectorAll('.cmp-tabs__tab');
	  tabs.forEach(t => t.classList.remove(HEADER.activeTabClass));
	  tab.classList.add(HEADER.activeTabClass);
	}

	/**
	 * Closes the desktop menu if click is outside the desktop header
	 *
	 * @private
	 */
	function handleOutsideClick(event) {
	  if (!HEADER.desktop.contains(event.target) && !HEADER.desktopMenu.contains(event.target)) {
		HEADER.desktopMenu.classList.remove("menu-desktop--opened");
		lastClickedTab = null;
	  }
	}

	/**
	 * Initializes the Header
	 *
	 * @public
	 */
	function init() {
	  if (HEADER.allHeaders) {
		window.addEventListener("scroll", function () {
		  HEADER.allHeaders.forEach((el) => {
			if (window.scrollY > 0) {
			  el.classList.add("scrolled");
			} else {
			  el.classList.remove("scrolled");
			}
		  });
		});
	  }

	  if (HEADER.headerTablist) {
		HEADER.headerTablist.addEventListener("click", handleTabClick);
	  }

	  document.addEventListener("click", handleOutsideClick);
	}

	document.addEventListener("DOMContentLoaded", function () {
	  init();
	});
})();
