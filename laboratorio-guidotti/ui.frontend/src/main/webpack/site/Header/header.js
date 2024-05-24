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
      const group = el.querySelector(".cmp-navigation__group");
      if (group) {
        const link = el.querySelector(".cmp-navigation__item-link");

        // Create the toggle icon element
        const toggleIcon = document.createElement("span");
        toggleIcon.classList.add("toggle-icon");

        // Append the toggle icon to the link
        link.appendChild(toggleIcon);

        toggleIcon.addEventListener("click", (e) => {
          e.preventDefault();
          e.stopPropagation(); // Prevent the click from bubbling up to the link
          group.classList.toggle("is-visible");
          link.classList.toggle("is-open");
        });
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
    e.stopPropagation();
    toggleNavbar();
  });

  document.body.addEventListener("click", (e) => {
    if (
      HEADER.navbarMobile &&
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
