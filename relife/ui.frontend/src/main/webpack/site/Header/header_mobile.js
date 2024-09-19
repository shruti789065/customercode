import { toggleOverlay } from ".././_util";

(function () {
  "use strict";

  const HEADER = {
    mobile: document.querySelector(".cmp-experiencefragment--header-mobile"),
    menuButton: document.querySelector("#navigationMenuMobile .cmp-button__icon--menu"),
    mobileNavbar: document.getElementById("mobileNavbar"),
    menuNavbar: document.getElementById("menuNavbar"),
    tabButtons: document.querySelectorAll("#navigationMenuMobile .cmp-tabs__tab"),
    tabPanels: document.querySelectorAll("#navigationMenuMobile .cmp-tabs__tabpanel"),
    backButton: document.querySelector("#navigationMenuMobile .cmp-button__icon--back"),
    closeButton: document.querySelector("#navigationMenuMobile .cmp-button__icon--close"),
    textLabel: document.querySelector("#navigationMenuMobile #menuNavbar .text"),
  };

  let lastScrollTop = 0;
  let menuVisible = true;

  function handleMenuClick() {
    HEADER.mobile?.classList.toggle("cmp-menu--opened");
    toggleOverlay("overlay");
    hideActiveTabs();
  }

  function handleMobileTabClick(event) {
    event.preventDefault();
    const selectedTab = event.currentTarget;
    const selectedPanelId = selectedTab.getAttribute("aria-controls");

    deactivateAllTabsAndPanels();

    selectedTab.classList.add("cmp-tabs__tab--active");
    selectedTab.setAttribute("aria-selected", "true");
    selectedTab.setAttribute("tabindex", "0");

    const selectedPanel = document.getElementById(selectedPanelId);
    if (selectedPanel) {
      selectedPanel.classList.add("cmp-tabs__tabpanel--active");
      selectedPanel.setAttribute("aria-hidden", "false");

      HEADER.mobileNavbar.style.display = "none";
      HEADER.menuNavbar.style.display = "flex";
      HEADER.textLabel.textContent = selectedTab.textContent;

      const tabList = selectedTab.closest(".cmp-tabs__tablist");
      if (tabList) {
        tabList.style.display = "none";
      }
    }
  }

  function handleBackClick() {
    deactivateAllTabsAndPanels();
    HEADER.tabButtons.forEach((tab) => {
      tab.style.display = "block";
    });

    HEADER.mobileNavbar.style.display = "flex";
    HEADER.menuNavbar.style.display = "none";

    document.querySelectorAll(".cmp-tabs__tablist").forEach((tabList) => {
      tabList.style.display = "flex";
    });

    HEADER.textLabel.textContent = "";
  }

  function handleCloseClick() {
    HEADER.mobile?.classList.remove("cmp-menu--opened");
    toggleOverlay("overlay");
    handleBackClick();
  }

  function deactivateAllTabsAndPanels() {
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

  function hideActiveTabs() {
    deactivateAllTabsAndPanels();
    document.querySelectorAll(".cmp-tabs__tablist").forEach((tabList) => {
      tabList.style.display = "flex";
    });
  }

  function handleScroll() {
	let scrollTop = window.scrollY || document.documentElement.scrollTop || document.body.scrollTop;
  
	// Evitare che scrollTop e lastScrollTop diventino negativi
	scrollTop = Math.max(0, scrollTop);
  
	if (!HEADER.mobile?.classList.contains("cmp-menu--opened")) {
	  if (scrollTop > lastScrollTop && menuVisible) {
		// Scroll down, hide menu
		HEADER.mobile.style.transform = "translateY(-100%)";
		menuVisible = false;
	  } else if (scrollTop <= 0) {
		// If we are at the top, show menu
		HEADER.mobile.style.transform = "translateY(0)";
		menuVisible = true;
	  } else if (scrollTop < lastScrollTop && !menuVisible) {
		// Scroll up, show menu
		HEADER.mobile.style.transform = "translateY(0)";
		menuVisible = true;
	  }
	}
  
	// Impedire che lastScrollTop sia negativo
	lastScrollTop = Math.max(0, scrollTop);
  }
  

  function init() {
    HEADER.menuButton?.addEventListener("click", handleMenuClick);
    HEADER.tabButtons.forEach((tab) => {
      tab.addEventListener("click", handleMobileTabClick);
    });
    HEADER.backButton?.addEventListener("click", handleBackClick);
    HEADER.closeButton?.addEventListener("click", handleCloseClick);

    window.addEventListener("scroll", handleScroll);

    hideActiveTabs();
  }

  document.addEventListener("DOMContentLoaded", init);
})();
