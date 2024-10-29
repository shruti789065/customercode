import { _isDesktop, _prependHtml } from "../../site/_util.js";

const MenuTabs = (() => {
  const CONST = {
    ACTIVE_TAB: "cmp-tabs__tab--active",
    ACTIVE_PANEL: "cmp-tabs__tabpanel--active",
    GENERIC_TAB_CLASS: "cmp-tabs__tab",
    GENERIC_PANEL_CLASS: "cmp-tabs__tabpanel",
  };

  const fragmentMenu = document.querySelector(".cmp-experiencefragment--header");

  let tabsMenu, whiteMenu, mobileTabsActive, tablistMobile, shareOpened;

  /**
   * Initializes the menu tabs and sets up event listeners.
   */
  function init() {
    tabsMenu = document.querySelector(".tabs-menu");
    if (!tabsMenu) {
      return;
    }

    const tabsMenuContainers = document.querySelectorAll(".tabs-menu__container");
    const menuNavTabs = document.querySelectorAll(".menu-nav li");
    const tabLinks = document.querySelectorAll(".cmp-tabs__tab .cmp-link--text");

    tabsMenuContainers.forEach(container => {
      container.classList.remove(CONST.ACTIVE_PANEL);
    });

    menuNavTabs.forEach(tab => {
      tab.classList.remove(CONST.ACTIVE_TAB);
      tab.setAttribute("aria-selected", "false");
      tab.setAttribute("tabindex", "-1");
    });

    document.addEventListener("click", handleTabClick);

    whiteMenu = false;
    window.addEventListener("scroll", handleScroll);
    window.addEventListener("resize", handleResize);

    window.addEventListener("resize", isMobileWindowSize);

    tabLinks.forEach(link => {
      link.addEventListener("click", function (event) {
        event.stopPropagation();
        addWhiteMenu();
      });
    });

    if (!_isDesktop() || isMobileWindowSize()) {
      setupMobileMenu();
    } else {
      setupDesktopMenu();
    }
  }

  /**
   * Handles clicks on tabs to activate or deactivate them.
   * @param {Event} e - The click event.
   */
  function handleTabClick(e) {
    if (!e.target.closest(".menu-nav li")) {
      return false;
    }

    e.preventDefault();
    const tab = e.target.closest(".menu-nav li");
    const tabpanelId = tab.getAttribute("aria-controls");
    const tabpanel = document.getElementById(tabpanelId);
    const isActiveTab = tab.classList.contains(CONST.ACTIVE_TAB);

    if (isActiveTab) {
      deactivateTab(tab);
      deactivatePanel();
    } else {
      activateTab(tab);
      activatePanel(tabpanel);
      addWhiteMenu();
      whiteMenu = true;
    }
  }

  /**
   * Activates the given tab element.
   * @param {Element} tab - The tab element to activate.
   */
  function activateTab(tab) {
    tab.classList.add(CONST.ACTIVE_TAB);
    tab.setAttribute("aria-selected", "true");
    tab.setAttribute("tabindex", "0");
  }

  /**
   * Deactivates the given tab element.
   * @param {Element} tab - The tab element to deactivate.
   */
  function deactivateTab(tab) {
    tab.classList.remove(CONST.ACTIVE_TAB);
    tab.setAttribute("aria-selected", "false");
    tab.setAttribute("tabindex", "-1");
  }

  /**
   * Activates the given panel element.
   * @param {Element} panel - The panel element to activate.
   */
  function activatePanel(panel) {
    panel.classList.add(CONST.ACTIVE_PANEL);
    panel.setAttribute("aria-hidden", "false");
    panel.style.display = "block";
  }

  /**
   * Deactivates all panel elements.
   */
  function deactivatePanel() {
    document.querySelectorAll(`.${CONST.GENERIC_PANEL_CLASS}`).forEach(panel => {
      panel.classList.remove(CONST.ACTIVE_PANEL);
      panel.setAttribute("aria-hidden", "true");
      panel.style.display = "none";
    });
  }

  /**
   * Sets up the mobile version of the menu.
   */
  function setupMobileMenu() {
    const tabsMenuContainers = document.querySelectorAll(".tabs-menu__container");
    toggleMenu();
    tablistMobile = document.querySelector(".cmp-tabs__tablist_mobile");

    tabsMenuContainers.forEach((mobItem) => {
      const dataPanelAttr = mobItem.getAttribute("data-cmp-panel-title");
      const panelClose = '<div class="panel-menu-toggler"></div>';
      const html = `<div class="tabs-mobile-panel__title">${panelClose}<h3>${dataPanelAttr}</h3></div>`;
      _prependHtml(mobItem, html);
    });

    document.querySelectorAll(".panel-menu-toggler").forEach(toggler => {
      toggler.addEventListener("click", () => {
        tabsMenuContainers.forEach(container => {
          container.classList.remove(CONST.ACTIVE_PANEL);
        });
      });
    });

    document.querySelector(".cmp-button--mobile__toggler").addEventListener("click", () => {
      toggleMobileMenu();
    });
  }

  /**
   * Sets up the desktop version of the menu.
   */
  function setupDesktopMenu() {
    console.log("desktop");
  }

  /**
   * Handles the scroll event to adjust the menu appearance.
   */
  function handleScroll() {
    const isFragmentMenuPresent = fragmentMenu !== null;
    const isMobileMenuOpen = document.querySelector(".cmp-button--mobile")
                             .classList.contains("cmp-button--mobile__toggler_close");
    const isScrolledPastThreshold = window.scrollY > 40;
    const isActiveTabPanelAbsent = document.querySelector("header .cmp-tabs__tabpanel--active") === null;

    if (isFragmentMenuPresent && !isMobileMenuOpen) {
      if (isScrolledPastThreshold) {
        addWhiteMenu();
      } else if (isActiveTabPanelAbsent) {
        if (shareOpened) {
          addWhiteMenu();
        } else {
          removeWhiteMenu();
        }
      }
    }
  }

  /**
   * Handles the resize event to adjust the menu layout for smaller screens.
   */
  function handleResize() {
    if (window.innerHeight < 600) {
      addWhiteMenu();
    }
  }

  /**
   * Checks if the window size indicates a mobile layout.
   * @returns {boolean} True if the window is in mobile size.
   */
  function isMobileWindowSize() {
    const width = document.documentElement.clientWidth;
    const tablist = document.querySelector(".cmp-tabs__tablist");
    tablist.classList.toggle("cmp-tabs__tablist_mobile", width <= 1200);
    return width <= 1200;
  }

  /**
   * Adds a white background to the menu for better visibility.
   */
  function addWhiteMenu() {
    if (!whiteMenu) {
      fragmentMenu?.classList.add("scrolled-page");
      whiteMenu = true;
    }
  }

  /**
   * Removes the white background from the menu.
   */
  function removeWhiteMenu() {
    if (whiteMenu) {
      fragmentMenu?.classList.remove("scrolled-page");
      whiteMenu = false;
    }
  }

  /**
   * Toggles the menu between mobile and desktop view.
   */
  function toggleMenu() {
    const tabsToggler = document.querySelector(".cmp-tabs__tablist_toggler");
    const tabsContainer = document.querySelector(".tabs-menu__container");

    tabsToggler?.classList.toggle("cmp-tabs__tablist_mobile");
    tabsContainer?.classList.toggle("cmp-container--menu_item-mobile");
  }

  /**
   * Toggles the mobile menu state and adjusts UI accordingly.
   */
  /**
 * Toggles the mobile menu state and adjusts UI accordingly.
 */
function toggleMobileMenu() {
  const mobileButton = document.querySelector(".cmp-button--mobile");
  const body = document.body;
  const navbarOverlayer = document.querySelector(".cmp-navbar-overlayer");
  const isMenuOpen = mobileButton.classList.contains("cmp-button--mobile__toggler_close");
  const tabsContainers = document.querySelectorAll(".tabs-menu__container");

  if (!isMenuOpen) {
    // Open mobile menu
    mobileButton.classList.add("cmp-button--mobile__toggler_close");
    body.classList.add("h-overflow");
    addWhiteMenu();
  } else {
    // Close mobile menu
    tablistMobile.classList.remove("cmp-tabs__tablist-opened");
    tabsContainers.forEach(container => {
      container.classList.remove(CONST.ACTIVE_PANEL);
    });
    mobileButton.classList.remove("cmp-button--mobile__toggler_close");
    body.classList.remove("h-overflow");

    if (navbarOverlayer) {
      navbarOverlayer.style.display = "none";
    }
    removeWhiteMenu();
  }

  // Toggle mobile active class
  tablistMobile.classList.toggle("cmp-tabs__tablist_mobile-active");

  // Update each mobile tab item with the appropriate click event
  mobileTabsActive = document.querySelectorAll(".cmp-tabs__tablist_mobile-active .cmp-tabs__tab");
  mobileTabsActive.forEach(mobItem => {
    mobItem.addEventListener("click", () => {
      document.querySelector(".cmp-container--menu_item-mobile").classList.add("cmp-container--menu_item-mobile--active");
    });
  });
}


  return {
    init: init,
  };
})();

document.addEventListener("DOMContentLoaded", () => {
  MenuTabs.init();
});
