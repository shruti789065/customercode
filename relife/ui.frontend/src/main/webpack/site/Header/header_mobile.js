/* eslint-disable max-len */
import { toggleOverlay } from ".././_util";

(function () {
  "use strict";

  const HEADER = {
    mobile: document.querySelector(".cmp-experiencefragment--header-mobile"),
    menuButton: document.querySelector(".cmp-button__icon--menu"),
    navigationItem: document.getElementById("navigationItem"),
    logo: document.querySelector(".cmp-image__link"),
    searchButton: document.querySelector(".cmp-button__icon--search"),
    accordionItems: document.querySelectorAll(".cmp-accordion__button"),
    backButton: document.querySelector("#navigationItem .cmp-button__icon--back"),
    textLabel: document.querySelector("#navigationItem .text")
  };

  function handleMenuClick() {
    HEADER.mobile.classList.toggle("cmp-menu--opened");
    toggleOverlay("overlay");
  }

  function handleAccordionClick(event) {
    event.preventDefault();
    const button = event.currentTarget;
    const panel = document.getElementById(button.getAttribute("aria-controls"));
    const title = button.querySelector(".cmp-accordion__title").textContent;

    if (panel && !panel.classList.contains("cmp-accordion__panel--hidden")) {
      panel.classList.add("cmp-accordion__panel--hidden");
      HEADER.navigationItem.style.display = "flex";
      HEADER.logo.style.display = "none";
      HEADER.searchButton.style.display = "none";
      HEADER.textLabel.textContent = title;
    } else {
      panel.classList.remove("cmp-accordion__panel--hidden");
    }
  }

  function handleBackClick() {
    const openedPanel = document.querySelector(".cmp-accordion__panel:not(.cmp-accordion__panel--hidden)");

    if (openedPanel) {
      openedPanel.classList.add("cmp-accordion__panel--hidden");
      HEADER.navigationItem.style.display = "none";
      HEADER.logo.style.display = "block";
      HEADER.searchButton.style.display = "block";
      HEADER.textLabel.textContent = "";
    }
  }

  function init() {
    if (HEADER.menuButton) {
      HEADER.menuButton.addEventListener("click", handleMenuClick);
    }

    HEADER.accordionItems.forEach(item => {
      item.addEventListener("click", handleAccordionClick);
    });

    if (HEADER.backButton) {
      HEADER.backButton.addEventListener("click", handleBackClick);
    }
  }

  document.addEventListener("DOMContentLoaded", function () {
    init();
  });
})();
