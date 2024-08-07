import { toggleOverlay } from ".././_util";

(function () {
  "use strict";

  const HEADER = {
    mobile: document.querySelector(".cmp-experiencefragment--header-mobile"),
    menuButton: document.querySelector(
      "#navigationMenuMobile .cmp-button__icon--menu"
    ),
    mobileNavbar: document.getElementById("mobileNavbar"),
    menuNavbar: document.getElementById("menuNavbar"),
    tabButtons: document.querySelectorAll(
      "#navigationMenuMobile .cmp-tabs__tab"
    ),
    tabPanels: document.querySelectorAll(
      "#navigationMenuMobile .cmp-tabs__tabpanel"
    ),
    backButton: document.querySelector(
      "#navigationMenuMobile .cmp-button__icon--back"
    ),
    closeButton: document.querySelector(
      "#navigationMenuMobile .cmp-button__icon--close"
    ),
    textLabel: document.querySelector(
      "#navigationMenuMobile #menuNavbar .text"
    ),
  };

  function handleMenuClick() {
    HEADER.mobile.classList.toggle("cmp-menu--opened");
    toggleOverlay("overlay");

    // Nascondere tutti i tab e pannelli ogni volta che viene cliccato il pulsante del menu
    hideActiveTabs();
  }

  function handleTabClick(event) {
    event.preventDefault();
    const selectedTab = event.currentTarget;
    const selectedPanelId = selectedTab.getAttribute("aria-controls");

    // Disattivare tutti i tab e nascondere tutti i pannelli
    HEADER.tabButtons.forEach((tab) => {
      tab.classList.remove("cmp-tabs__tab--active");
      tab.setAttribute("aria-selected", "false");
      tab.setAttribute("tabindex", "-1");
    });
    HEADER.tabPanels.forEach((panel) => {
      panel.classList.remove("cmp-tabs__tabpanel--active");
      panel.setAttribute("aria-hidden", "true");
    });

    // Attivare il tab selezionato e mostrare il pannello corrispondente
    selectedTab.classList.add("cmp-tabs__tab--active");
    selectedTab.setAttribute("aria-selected", "true");
    selectedTab.setAttribute("tabindex", "0");

    const selectedPanel = document.getElementById(selectedPanelId);
    if (selectedPanel) {
      selectedPanel.classList.add("cmp-tabs__tabpanel--active");
      selectedPanel.setAttribute("aria-hidden", "false");

      // Nascondere il pannello del logo e pulsante di ricerca, e mostrare quello con testo e freccia back
      HEADER.mobileNavbar.style.display = "none";
      HEADER.menuNavbar.style.display = "flex";
      HEADER.textLabel.textContent = selectedTab.textContent;
    }
  }

  function handleBackClick() {
    // Nascondere tutti i pannelli
    HEADER.tabPanels.forEach((panel) => {
      panel.classList.remove("cmp-tabs__tabpanel--active");
      panel.setAttribute("aria-hidden", "true");
    });

    // Rimuovere l'attivazione dai tab
    HEADER.tabButtons.forEach((tab) => {
      tab.classList.remove("cmp-tabs__tab--active");
      tab.setAttribute("aria-selected", "false");
      tab.setAttribute("tabindex", "-1");
    });

    // Ripristinare la visualizzazione iniziale
    HEADER.mobileNavbar.style.display = "flex";
    HEADER.menuNavbar.style.display = "none";
    HEADER.textLabel.textContent = "";
  }

  function handleCloseClick() {
    // Chiudere il menu
    HEADER.mobile.classList.remove("cmp-menu--opened");
    toggleOverlay("overlay");

    // Nascondere tutti i pannelli
    HEADER.tabPanels.forEach((panel) => {
      panel.classList.remove("cmp-tabs__tabpanel--active");
      panel.setAttribute("aria-hidden", "true");
    });

    // Rimuovere l'attivazione dai tab
    HEADER.tabButtons.forEach((tab) => {
      tab.classList.remove("cmp-tabs__tab--active");
      tab.setAttribute("aria-selected", "false");
      tab.setAttribute("tabindex", "-1");
    });

    // Ripristinare la visualizzazione iniziale
    HEADER.mobileNavbar.style.display = "flex";
    HEADER.menuNavbar.style.display = "none";
    HEADER.textLabel.textContent = "";
  }

  function hideActiveTabs() {
    // Rimuovere l'attivazione da tutti i tab e pannelli
    HEADER.tabButtons.forEach((tab) => {
      tab.classList.remove("cmp-tabs__tab--active");
      tab.setAttribute("aria-selected", "false");
      tab.setAttribute("tabindex", "-1");
    });
    HEADER.tabPanels.forEach((panel) => {
      panel.classList.remove("cmp-tabs__tabpanel--active");
      panel.setAttribute("aria-hidden", "true");
    });
  }

  function init() {
    if (HEADER.menuButton) {
      HEADER.menuButton.addEventListener("click", handleMenuClick);
    }

    HEADER.tabButtons.forEach((tab) => {
      tab.addEventListener("click", handleTabClick);
    });

    if (HEADER.backButton) {
      HEADER.backButton.addEventListener("click", handleBackClick);
    }

    if (HEADER.closeButton) {
      HEADER.closeButton.addEventListener("click", handleCloseClick);
    }

    // Nascondere i tab e i pannelli attivi all'inizializzazione
    hideActiveTabs();
  }

  document.addEventListener("DOMContentLoaded", function () {
    init();
  });
})();
