import $ from "jquery";
import { _isDesktop, _prependHtml } from "../../site/_util.js";

const MenuTabs = (() => {
  const CONST = {
    ACTIVE_TAB: "cmp-tabs__tab--active",
    ACTIVE_PANEL: "cmp-tabs__tabpanel--active",
    GENERIC_TAB_CLASS: "cmp-tabs__tab",
    GENERIC_PANEL_CLASS: "cmp-tabs__tabpanel",
  };

  let $tabsMenu,
    $whiteMenu,
    $shareButton,
    $mobileTabsActive,
    $tablistMobile,
    shareOpened;

  function init() {
    $tabsMenu = $(".tabs-menu");
    if ($tabsMenu.length === 0) return;

    $whiteMenu = false;
    $shareButton = $(".cmp-share-desktop .cmp-button");

    $(window).on("scroll", handleScroll);
    $(window).on("resize", handleResize);
    $(".cmp-tabs__tab").on("click", handleTabClick);
    window.addEventListener("resize", _isMobileWindowSize);

    if (!_isDesktop() || _isMobileWindowSize()) {
      setupMobileMenu();
    } else {
      setupDesktopMenu();
    }
  }

  function setupMobileMenu() {
    let navbarText = $(".cmp-navbar .buildingblock").html();
    let navbarMobile = `<div class="cmp-navbar--text-mobile">${navbarText}</div>`;
    $(".menu-nav .cmp-tabs__tablist").append(navbarMobile);
    const tabsMenuContainers = document.querySelectorAll(
      ".tabs-menu__container"
    );
    _toggleMenu();
    $tablistMobile = $(".cmp-tabs__tablist_mobile");
    tabsMenuContainers.forEach((mobItem) => {
      const dataPanelAttr = mobItem.getAttribute("data-cmp-panel-title");
      const panelClose =
        '<div class="panel-menu-toggler"><span></span><span></span></div>';
      const html = `<div class="tabs-mobile-panel__title">${panelClose}<h3>${dataPanelAttr}</h3></div>`;
      _prependHtml(mobItem, html);
    });

    $(".panel-menu-toggler").on("click", () => {
      $(".tabs-menu__container").removeClass("cmp-tabs__tabpanel--active");
    });

    $(".cmp-button--mobile__toggler").on("click", () => {
      toggleMobileMenu();
    });
  }

  function setupDesktopMenu() {
    if ($shareButton.length > 0) {
      $shareButton.on("click", (e) => {
        e.preventDefault();
        toggleShareMenu();
      });
    }
  }

  function handleScroll() {
    if (
      $("header.cmp-experiencefragment--header").length > 0 &&
      !$(".cmp-button--mobile").hasClass("cmp-button--mobile__toggler_close")
    ) {
      if ($(window).scrollTop() > 40) {
        _addWhiteMenu();
      } else if (
        $("header .cmp-tabs__tabpanel--active").length < 1 &&
        shareOpened
      ) {
        _addWhiteMenu();
      } else if ($("header .cmp-tabs__tabpanel--active").length < 1) {
        _removeWhiteMenu();
      }
    }
  }

  function handleResize() {
    if ($(window).height() < 600) {
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
    const tablist = $(".cmp-tabs__tablist");
    tablist.toggleClass("cmp-tabs__tablist_mobile", w < 1200);
    return w < 1200;
  }

  function _addWhiteMenu() {
    if (!$whiteMenu) {
      $("header.cmp-experiencefragment--header").addClass("scrolled-page");
      $whiteMenu = true;
    }
  }

  function _removeWhiteMenu() {
    if ($whiteMenu) {
      $("header.cmp-experiencefragment--header").removeClass("scrolled-page");
      $whiteMenu = false;
    }
  }

  function _toggleMenu() {
    if ($(".cmp-tabs__tablist_toggler").length > 0) {
      $(".cmp-tabs__tablist_toggler").toggleClass("cmp-tabs__tablist_mobile");
    }
    if ($(".tabs-menu__container").length > 0) {
      $(".tabs-menu__container").toggleClass("cmp-container--menu_item-mobile");
    }
  }

  function toggleMobileMenu() {
    if ($(".cmp-button--mobile__toggler_close").length < 1) {
      $(".cmp-button--mobile").addClass("cmp-button--mobile__toggler_close");
      $("body").addClass("h-overflow");
      console.log("menu aperto");
    } else {
      $tablistMobile.removeClass("cmp-tabs__tablist-opened");
      $(".tabs-menu__container").removeClass("cmp-tabs__tabpanel--active");
      $(".cmp-button--mobile").removeClass("cmp-button--mobile__toggler_close");
      $("body").removeClass("h-overflow");
      $(".cmp-navbar-overlayer").hide();
      console.log("menu chiuso");
    }
    $tablistMobile.toggleClass("cmp-tabs__tablist_mobile-active");
    $mobileTabsActive = document.querySelectorAll(
      ".cmp-tabs__tablist_mobile-active .cmp-tabs__tab"
    );
    _addWhiteMenu();
    $mobileTabsActive.forEach((mobItem) => {
      mobItem.addEventListener("click", () => {
        document
          .querySelector(".cmp-container--menu_item-mobile")
          .classList.add("cmp-container--menu_item-mobile--active");
      });
    });
  }

  function toggleShareMenu() {
    shareOpened = false;

    $(".cmp-share-desktop .cmp-share__container").toggleClass(
      "cmp-share__active"
    );

    shareOpened = document.querySelectorAll(".cmp-share__active").length > 0;

    if (!shareOpened) {
      $(".cmp-navbar-overlayer").hide();
      $(".cmp-share__container").hide();
      $("body").removeClass("h-overflow");
      //shareOpened = false;
      $whiteMenu = true;
      _removeWhiteMenu();
    } else {
      $(".cmp-navbar-overlayer").show();
      $(".cmp-share__container").show();
      $("body").addClass("h-overflow");
      $whiteMenu = false;
      _addWhiteMenu();
    }

	$(".cmp-navbar-overlayer").on('click',function(){
		shareOpened = false;
	});

    /*if ($(window).scrollTop() > 0 && shareOpened) {
      $whiteMenu = false;
      _addWhiteMenu();
    }*/
  }

  return {
    init: init,
  };
})();

$(function () {
  MenuTabs.init();
});
