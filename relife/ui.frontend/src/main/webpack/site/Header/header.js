import { toggleOverlay } from ".././_util";
/* eslint-disable max-len */
(function () {
  "use strict";

  const HEADER = {
    allHeaders: document.querySelectorAll("header"),
    desktop: document.querySelector(".cmp-experiencefragment--header-desktop"),
    navbar: document.getElementById("navbar"),
    headerTablist: document.getElementById("header--tablist"),
    desktopMenu: document.querySelector(".menu-desktop"),
    activeTabClass: "cmp-tabs__tab--active"
  };

  let lastClickedTab = null;

  /**
   * Toggles the desktop menu visibility based on the clicked tab
   *
   * @private
   */
  function handleTabClick(event) {
    const clickedTab = event.target.closest(".cmp-tabs__tab");

    if (!clickedTab || !HEADER.desktopMenu) {
      return;
    }

    // Se il tab cliccato contiene un elemento con la classe 'header-link', non aprire il menu
    if (clickedTab.querySelector('.header-link')) {
      return;
    }

    const isAlreadyActive = clickedTab.classList.contains(HEADER.activeTabClass);

    if (isAlreadyActive && lastClickedTab === clickedTab) {
      // Close the menu if the same tab is clicked again
      HEADER.desktopMenu.classList.remove("menu-desktop--opened");
      toggleOverlay("overlay"); // Disattiva overlay
      lastClickedTab = null;
    } else {
      // Open the menu and activate the clicked tab
      if (!HEADER.desktopMenu.classList.contains("menu-desktop--opened")) {
        toggleOverlay("overlay"); // Attiva overlay solo se il menu non è già aperto
      }
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
    if (!HEADER.headerTablist) {
      return;
    }

    const tabs = HEADER.headerTablist.querySelectorAll(".cmp-tabs__tab");
    tabs.forEach(t => t.classList.remove(HEADER.activeTabClass));
    tab.classList.add(HEADER.activeTabClass);
  }

  /**
   * Closes the desktop menu if click is outside the desktop header
   *
   * @private
   */
  function handleOutsideClick(event) {
    if (!HEADER.desktop || !HEADER.desktopMenu) {
      return;
    }

    if (
      !HEADER.desktop.contains(event.target) &&
      !HEADER.desktopMenu.contains(event.target)
    ) {
      if (HEADER.desktopMenu.classList.contains("menu-desktop--opened")) {
        toggleOverlay("overlay"); // Disattiva overlay
      }
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
        HEADER.allHeaders.forEach(el => {
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
