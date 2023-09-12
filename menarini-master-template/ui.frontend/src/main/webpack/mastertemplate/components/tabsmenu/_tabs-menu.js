import $ from "jquery";
import { _isDesktop, _prependHtml } from "../../site/_util.js";

const MenuTabs = (() => {
  const CONST = {
    ACTIVE_TAB: "cmp-tabs__tab--active",
    ACTIVE_PANEL: "cmp-tabs__tabpanel--active",
    GENERIC_TAB_CLASS: "cmp-tabs__tab",
    GENERIC_PANEL_CLASS: "cmp-tabs__tabpanel",
  };

  let $tabsMenu, $whiteMenu, $shareButton, $mobileTabsActive;

  function init() {
    $tabsMenu = $(".tabs-menu");
    if ($tabsMenu.length === 0) return;

    const $tabsMenuContainer = $(".tabs-menu__container");
    const $tabsMenuTablist = $(".cmp-tabs__tablist");

    $whiteMenu = false;
    $shareButton = $(".cmp-share-desktop .cmp-button");

    $(window).on("scroll", handleScroll);
    $(window).on("resize", handleResize);

    $(".cmp-tabs__tab").on("click", handleTabClick);

    window.addEventListener("resize", _isMobileWindowSize);

    if (!_isDesktop() || _isMobileWindowSize()) {
      let navbarText = $(".cmp-navbar .buildingblock").html();
      let navbarMobile = `<div class="cmp-navbar--text-mobile">${navbarText}</div>`;
      $(".menu-nav .cmp-tabs__tablist").append(navbarMobile);

      _toggleMenu();
      $tablistMobile = document.querySelectorAll(".cmp-tabs__tablist_mobile");
      if ($tablistMobile.length > 0) {
        $tabsMenuContainer.forEach((mobItem) => {
          let dataPanelAttr = mobItem.getAttribute("data-cmp-panel-title");
          let panelClose = `<div class="panel-menu-toggler"><span></span><span></span></div>`;
          let html = `<div class="tabs-mobile-panel__title">${panelClose}<h3>${dataPanelAttr}</h3></div>`;
          _prependHtml(mobItem, html);
        });

        $(".panel-menu-toggler").on("click", () => {
          $(".tabs-menu__container").removeClass("cmp-tabs__tabpanel--active");
        });
      }

      document
        .querySelector(".cmp-button--mobile__toggler")
        .addEventListener("click", () => {
          if (
            document.querySelectorAll(".cmp-button--mobile__toggler_close")
              .length < 1
          ) {
            document
              .querySelector(".cmp-button--mobile")
              .classList.add("cmp-button--mobile__toggler_close");
            document.querySelector("body").classList.add("h-overflow");
          } else {
            $(".cmp-tabs__tablist_mobile").removeClass(
              "cmp-tabs__tablist-opened"
            );
            $(".tabs-menu__container").removeClass(
              "cmp-tabs__tabpanel--active"
            );
            document
              .querySelector(".cmp-button--mobile")
              .classList.remove("cmp-button--mobile__toggler_close");
            document.querySelector("body").classList.remove("h-overflow");
            $(".cmp-navbar-overlayer").hide();
          }
          document
            .querySelector(".cmp-tabs__tablist_mobile")
            .classList.toggle("cmp-tabs__tablist_mobile-active");
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
        });
    } else {
      if ($shareButton.length > 0) {
        $shareButton.on("click", handleShareButtonClick);
      }
    }
  }

  function _isMobileWindowSize() {
    const w = document.documentElement.clientWidth;
    const tablist = document.querySelector(".cmp-tabs__tablist");
    tablist.classList.toggle("cmp-tabs__tablist_mobile", w < 1200);
    return w < 1200;
  }

  function handleScroll() {
    if (
      document.querySelectorAll("header.cmp-experiencefragment--header")
        .length > 0
    ) {
      if ($(window).scrollTop() > 40) {
        _addWhiteMenu();
      } else {
        if (
          document.querySelectorAll("header .cmp-tabs__tabpanel--active")
            .length < 1
        ) {
          _removeWhiteMenu();
        }
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

  function handleShareButtonClick(e) {
    e.preventDefault();
    let shareOpened = false;

    document
      .querySelector(".cmp-share-desktop .cmp-share__container")
      .classList.toggle("cmp-share__active");

    document.querySelectorAll(".cmp-share__active").length < 1
      ? (shareOpened = false)
      : (shareOpened = true);

    if (shareOpened) {
      $whiteMenu ? _removeWhiteMenu() : _addWhiteMenu();
      $(".cmp-navbar-overlayer").hide();
      $(".cmp-share__container").hide();
      document.querySelector("body").classList.remove("h-overflow");
    } else {
      $whiteMenu ? _addWhiteMenu() : _removeWhiteMenu();
      //_addWhiteMenu();
      $(".cmp-navbar-overlayer").show();
      $(".cmp-share__container").show();
      document.querySelector("body").classList.add("h-overflow");
    }
  }

  function _addWhiteMenu() {
    if (!$whiteMenu) {
      document
        .querySelector("header.cmp-experiencefragment--header")
        .classList.add("scrolled-page");
      $whiteMenu = true;
    }
  }

  function _removeWhiteMenu() {
    if ($whiteMenu) {
      document
        .querySelector("header.cmp-experiencefragment--header")
        .classList.remove("scrolled-page");
      $whiteMenu = false;
    }
  }

  return {
    init: init,
  };
})();

$(function () {
  MenuTabs.init();
});
