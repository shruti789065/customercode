/* eslint-disable max-len */
(function () {
  "use strict";

  const HEADER = {
    allHeaders: document.querySelectorAll("header"),
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

  function init() {
    if (!HEADER.allHeaders) return;

    window.addEventListener("scroll", handleScroll);
    HEADER.menuItemsFirstLevel.forEach(addToggleFunctionality);
    HEADER.toggleButton.addEventListener("click", toggleNavbar);

    document.body.addEventListener("click", closeNavbarOnClickOutside);
  }

  function handleScroll() {
    HEADER.allHeaders.forEach((el) => {
      el.classList.toggle("scrolled", window.scrollY > 0);
    });
  }

  function addToggleFunctionality(el) {
    const group = el.querySelector(".cmp-navigation__group");
    if (!group) return;

    const link = el.querySelector(".cmp-navigation__item-link");
    const toggleIcon = createToggleIcon();

    link.appendChild(toggleIcon);

    toggleIcon.addEventListener("click", (e) => toggleMenu(e, group, link));
    link.addEventListener("click", (e) => toggleMenu(e, group, link));
  }

  function createToggleIcon() {
    const toggleIcon = document.createElement("span");
    toggleIcon.classList.add("toggle-icon");
    return toggleIcon;
  }

  function toggleMenu(e, group, link) {
    e.preventDefault();
    e.stopPropagation();

    const isOpen = group.classList.contains("is-visible");

    closeAllMenus();

    if (!isOpen) {
      group.classList.add("is-visible");
      link.classList.add("is-open");
    }
  }

  function closeAllMenus() {
    HEADER.menuItemsFirstLevel.forEach((el) => {
      const group = el.querySelector(".cmp-navigation__group");
      const link = el.querySelector(".cmp-navigation__item-link");
      if (group) {
        group.classList.remove("is-visible");
        link.classList.remove("is-open");
      }
    });
  }

  function toggleNavbar(e) {
    e.preventDefault();
    e.stopPropagation();
    HEADER.navbarMobile.style.display =
      HEADER.navbarMobile.style.display === "none" ||
      HEADER.navbarMobile.style.display === ""
        ? "block"
        : "none";
  }

  function closeNavbarOnClickOutside(e) {
    if (
      HEADER.navbarMobile &&
      !HEADER.mobile.contains(e.target) &&
      HEADER.navbarMobile.style.display === "block"
    ) {
      HEADER.navbarMobile.style.display = "none";
      console.log("BODY CLICKED");
    }
  }

  document.addEventListener("DOMContentLoaded", init);
})();
/* eslint-disable max-len */
