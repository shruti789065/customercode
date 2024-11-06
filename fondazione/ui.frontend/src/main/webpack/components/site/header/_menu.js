import $ from "jquery";
import { _isDesktop, _prependHtml } from "../../../site/_util.js";

const MenuTabs = (() => {
  const CONST = {
    ACTIVE_TAB: "cmp-tabs__tab--active",
    ACTIVE_PANEL: "cmp-tabs__tabpanel--active",
    GENERIC_TAB_CLASS: "cmp-tabs__tab",
    GENERIC_PANEL_CLASS: "cmp-tabs__tabpanel",
  };

  const fragmentMenuClass = ".cmp-experiencefragment--header";

  let $tabsMenu, $whiteMenu, $mobileTabsActive, $tablistMobile, shareOpened;

  function init() {
    $tabsMenu = $(".tabs-menu");
    if ($tabsMenu.length === 0) {
      return;
    }

    $(".tabs-menu__container").removeClass(CONST.ACTIVE_PANEL);
    $(".menu-nav li")
      .removeClass(CONST.ACTIVE_TAB)
      .attr("aria-selected", "false")
      .attr("tabindex", "-1");

    $(document).on("click", ".menu-nav li", function (e) {
      const tabpanelId = $(this).attr("aria-controls");
      const tabpanel = $("#" + tabpanelId);

      const isActiveTab = $(this).hasClass(CONST.ACTIVE_TAB);

      if (isActiveTab) {
        $(this)
          .removeClass(CONST.ACTIVE_TAB)
          .attr("aria-selected", "false")
          .attr("tabindex", "-1");

        $(".tabs-menu__container")
          .removeClass(CONST.ACTIVE_PANEL)
          .attr("aria-hidden", "true")
          .hide();
      } else {
        $(this)
          .addClass(CONST.ACTIVE_TAB)
          .attr("aria-selected", "true")
          .attr("tabindex", "0");

        tabpanel
          .addClass(CONST.ACTIVE_PANEL)
          .attr("aria-hidden", "false")
          .show();

        _addWhiteMenu();
        $whiteMenu = true;
      }
    });

    $whiteMenu = false;
    $(window).on("scroll", handleScroll);
    $(window).on("resize", handleResize);

    window.addEventListener("resize", _isMobileWindowSize);

    $(".cmp-tabs__tab .cmp-link--text").on("click", function (event) {
      event.stopPropagation();
      _addWhiteMenu();
    });

    if (!_isDesktop() || _isMobileWindowSize()) {
      setupMobileMenu();
    }
  }

  function setupMobileMenu() {
    const tabsMenuContainers = document.querySelectorAll(
      ".tabs-menu__container"
    );

    _toggleMenu();
    $tablistMobile = $(".cmp-tabs__tablist_mobile");
    tabsMenuContainers.forEach((mobItem) => {
      const dataPanelAttr = mobItem.getAttribute("data-cmp-panel-title");
      const panelClose = '<div class="panel-menu-toggler"></div>';
      const html = `<div class="tabs-mobile-panel__title">${panelClose}<h3>${dataPanelAttr}</h3></div>`;
      _prependHtml(mobItem, html);
    });

    $(".panel-menu-toggler").on("click", () => {
      $(".tabs-menu__container").removeClass(CONST.ACTIVE_PANEL);
    });

    $(".cmp-button--mobile__toggler").on("click", () => {
      toggleMobileMenu();
    });
  }

  function handleScroll() {
    const isFragmentMenuPresent = $(`${fragmentMenuClass}`).length > 0;
    const isMobileMenuOpen = $(".cmp-button--mobile").hasClass(
      "cmp-button--mobile__toggler_close"
    );
    const isScrolledPastThreshold = $(window).scrollTop() > 40;
    const isActiveTabPanelAbsent =
      $("header .cmp-tabs__tabpanel--active").length < 1;

    if (isFragmentMenuPresent && !isMobileMenuOpen) {
      if (isScrolledPastThreshold) {
        _addWhiteMenu();
      } else if (isActiveTabPanelAbsent) {
        if (shareOpened) {
          _addWhiteMenu();
        } else {
          _removeWhiteMenu();
        }
      }
    }
  }

  function handleResize() {
    if ($(window).height() < 600) {
      _addWhiteMenu();
    }
  }

  function _isMobileWindowSize() {
    const w = document.documentElement.clientWidth;
    const tablist = $(".cmp-tabs__tablist");
    tablist.toggleClass("cmp-tabs__tablist_mobile", w <= 1200);
    return w <= 1200;
  }

  function _addWhiteMenu() {
    if (!$whiteMenu) {
      $(`${fragmentMenuClass}`).addClass("scrolled-page");
      $whiteMenu = true;
    }
  }

  function _removeWhiteMenu() {
    if ($whiteMenu) {
      $(`${fragmentMenuClass}`).removeClass("scrolled-page");
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
      _addWhiteMenu();
    } else {
      $tablistMobile.removeClass("cmp-tabs__tablist-opened");
      $(".tabs-menu__container").removeClass(CONST.ACTIVE_PANEL);
      $(".cmp-button--mobile").removeClass("cmp-button--mobile__toggler_close");
      $("body").removeClass("h-overflow");
      $(".cmp-navbar-overlayer").hide();
      _removeWhiteMenu();
    }
    $tablistMobile.toggleClass("cmp-tabs__tablist_mobile-active");
    $mobileTabsActive = document.querySelectorAll(
      ".cmp-tabs__tablist_mobile-active .cmp-tabs__tab"
    );
    $mobileTabsActive.forEach((mobItem) => {
      mobItem.addEventListener("click", () => {
        document
          .querySelector(".cmp-container--menu_item-mobile")
          .classList.add("cmp-container--menu_item-mobile--active");
      });
    });
  }

  return {
    init: init,
  };
})();

$(function () {
  MenuTabs.init();
});
