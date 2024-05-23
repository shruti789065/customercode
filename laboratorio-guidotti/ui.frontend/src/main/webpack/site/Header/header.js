/* eslint-disable max-len */
(function () {
  "use strict";

  const HEADER = {
    mobile: document.querySelector(".header-mobile"),
    desktop: document.querySelector(".header-desktop"),
    toggleButton: document.getElementById("btnMenuMobile"),
    navbarMobile: document.getElementById("navbarMobile"),
    menuItemsFirstLevel: document.querySelectorAll(
      ".cmp-navigation__item.cmp-navigation__item--level-0"
    ),
    menuItemsSecondLevel: document.querySelectorAll(
      ".cmp-navigation__item.cmp-navigation__item--level-1"
    ),
  };

  /**
   * Initializes the Header
   *
   * @public
   */
  function init() {
    HEADER.menuItemsFirstLevel.forEach(function (el) {
      if (el.querySelector(".cmp-navigation__group")) {
        // Add any initialization logic here if needed
      }
    });
  }

  function toggleNavbar() {
    if (HEADER.navbarMobile) {
      HEADER.navbarMobile.style.display =
        HEADER.navbarMobile.style.display === "none" ||
        HEADER.navbarMobile.style.display === ""
          ? "block"
          : "none";
    }
  }

  HEADER.toggleButton.addEventListener("click", (e) => {
    e.preventDefault();
    e.stopPropagation(); // Prevent click from bubbling to body
    toggleNavbar();
  });

  document.body.addEventListener("click", (e) => {
    if (
      !HEADER.mobile.contains(e.target) &&
      HEADER.navbarMobile.style.display === "block"
    ) {
      HEADER.navbarMobile.style.display = "none";
      console.log("BODY CLICKED");
    }
  });

  document.addEventListener("DOMContentLoaded", function () {
    init();
  });
})();
/* eslint-disable max-len */
