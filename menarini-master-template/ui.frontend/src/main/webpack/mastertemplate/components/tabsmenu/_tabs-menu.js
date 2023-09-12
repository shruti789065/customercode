import { _isDesktop, _prependHtml } from "../../site/_util.js";

const MenuTabs = (() => {
  const CONST = {
    ACTIVE_TAB: "cmp-tabs__tab--active",
    ACTIVE_PANEL: "cmp-tabs__tabpanel--active",
    GENERIC_TAB_CLASS: "cmp-tabs__tab",
    GENERIC_PANEL_CLASS: "cmp-tabs__tabpanel",
  };

  let tabsMenu,
    whiteMenu,
    shareButton,
    mobileTabsActive,
    tablistMobile,
    shareOpened = false;

  function init() {
    tabsMenu = document.querySelector(".tabs-menu");
    if (!tabsMenu) return;

    whiteMenu = false;
    shareButton = document.querySelector(".cmp-share-desktop .cmp-button");

    window.addEventListener("scroll", handleScroll);
    window.addEventListener("resize", handleResize);
    const tabElements = document.querySelectorAll(".cmp-tabs__tab");
    tabElements.forEach((tab) => {
      tab.addEventListener("click", handleTabClick);
    });

    window.addEventListener("resize", _isMobileWindowSize);

    if (!_isDesktop() || _isMobileWindowSize()) {
      setupMobileMenu();
    } else {
      setupDesktopMenu();
    }
  }

  function setupMobileMenu() {
    const navbarText = document.querySelector(".cmp-navbar .buildingblock")
      .innerHTML;
    const navbarMobile = `<div class="cmp-navbar--text-mobile">${navbarText}</div>`;
    const tablist = document.querySelector(".menu-nav .cmp-tabs__tablist");
    const panelMenuTogglers = document.querySelectorAll(".panel-menu-toggler");
    const tabsMenuContainers = document.querySelectorAll(
      ".tabs-menu__container"
    );

    tablist.insertAdjacentHTML("beforeend", navbarMobile);
    _toggleMenu();
    tablistMobile = document.querySelectorAll(".cmp-tabs__tablist_mobile");
    tabsMenuContainers.forEach((mobItem) => {
      const dataPanelAttr = mobItem.getAttribute("data-cmp-panel-title");
      const panelClose =
        '<div class="panel-menu-toggler"><span></span><span></span></div>';
      const html = `<div class="tabs-mobile-panel__title">${panelClose}<h3>${dataPanelAttr}</h3></div>`;
      _prependHtml(mobItem, html);
    });

    $('.panel-menu-toggler').on('click', () => {
		$('.tabs-menu__container').removeClass('cmp-tabs__tabpanel--active');
	});

    const mobileToggler = document.querySelector(
      ".cmp-button--mobile__toggler"
    );
    if (mobileToggler) {
      mobileToggler.addEventListener("click", toggleMobileMenu);
    }
  }

  function setupDesktopMenu() {
    if (shareButton) {
      shareButton.addEventListener("click", (e) => {
        e.preventDefault();
        toggleShareMenu();
      });
    }
  }

  function handleScroll() {
    if (
      document.querySelector("header.cmp-experiencefragment--header") &&
      !document
        .querySelector(".cmp-button--mobile")
        .classList.contains("cmp-button--mobile__toggler_close")
    ) {
      if (window.scrollY > 40) {
        _addWhiteMenu();
      } else {
        if (!document.querySelector("header .cmp-tabs__tabpanel--active")) {
          _removeWhiteMenu();
        }
      }
    }
  }

  function handleResize() {
    if (window.innerHeight < 600) {
      _addWhiteMenu();
    } else {
      console.log("Height > 600");
    }
  }

  function handleTabClick() {
    _addWhiteMenu();
  }

  function _isMobileWindowSize() {
    const w = document.documentElement.clientWidth;
    const tablist = document.querySelector(".cmp-tabs__tablist");
    if (w < 1200) {
      tablist.classList.add("cmp-tabs__tablist_mobile");
      return true;
    } else {
      tablist.classList.remove("cmp-tabs__tablist_mobile");
      return false;
    }
  }

  function _addWhiteMenu() {
    if (!whiteMenu) {
      document
        .querySelector("header.cmp-experiencefragment--header")
        .classList.add("scrolled-page");
      whiteMenu = true;
    }
  }

  function _removeWhiteMenu() {
    if (whiteMenu) {
      document
        .querySelector("header.cmp-experiencefragment--header")
        .classList.remove("scrolled-page");
      whiteMenu = false;
    }
  }

  function _toggleMenu() {
    const tablistToggler = document.querySelector(".cmp-tabs__tablist_toggler");
    if (tablistToggler) {
      tablistToggler.classList.toggle("cmp-tabs__tablist_mobile");
    }
    const tabsMenuContainers = document.querySelectorAll(
      ".tabs-menu__container"
    );
    tabsMenuContainers.forEach((container) => {
      container.classList.toggle("cmp-container--menu_item-mobile");
    });
  }

  function toggleMobileMenu() {
    const mobileButton = document.querySelector(".cmp-button--mobile");
    if (!mobileButton.classList.contains("cmp-button--mobile__toggler_close")) {
      mobileButton.classList.add("cmp-button--mobile__toggler_close");
      document.body.classList.add("h-overflow");
      console.log("menu aperto");
    } else {
      tablistMobile.forEach((item) => {
        item.classList.remove("cmp-tabs__tablist-opened");
      });
      const tabsMenuContainers = document.querySelectorAll(
        ".tabs-menu__container"
      );
      tabsMenuContainers.forEach((container) => {
        container.classList.remove("cmp-tabs__tabpanel--active");
      });
      mobileButton.classList.remove("cmp-button--mobile__toggler_close");
      document.body.classList.remove("h-overflow");
      document.querySelector(".cmp-navbar-overlayer").style.display = "none";
      console.log("menu chiuso");
    }
    tablistMobile.forEach((item) => {
      item.classList.toggle("cmp-tabs__tablist_mobile-active");
    });
    mobileTabsActive = document.querySelectorAll(
      ".cmp-tabs__tablist_mobile-active .cmp-tabs__tab"
    );
    _addWhiteMenu();
    mobileTabsActive.forEach((mobItem) => {
      mobItem.addEventListener("click", () => {
        document
          .querySelector(".cmp-container--menu_item-mobile")
          .classList.add("cmp-container--menu_item-mobile--active");
      });
    });
  }

  function toggleShareMenu() {
    const shareContainer = document.querySelector(
      ".cmp-share-desktop .cmp-share__container"
    );
    shareContainer.classList.toggle("cmp-share__active");
    shareOpened = document.querySelectorAll(".cmp-share__active").length > 0;

    if (!shareOpened) {
      document.querySelector(".cmp-navbar-overlayer").style.display = "none";
      shareContainer.style.display = "none";
      document.body.classList.remove("h-overflow");
    } else {
      document.querySelector(".cmp-navbar-overlayer").style.display = "block";
      shareContainer.style.display = "block";
      document.body.classList.add("h-overflow");
    }

    if (window.scrollY == 0 && shareOpened) {
      whiteMenu = false;
      _addWhiteMenu();
    }
  }

  return {
    init: init,
  };
})();

document.addEventListener("DOMContentLoaded", () => {
  MenuTabs.init();
});
