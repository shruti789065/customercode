/* eslint-disable max-len */
import $ from 'jquery';
import { _isDesktop, _prependHtml } from '../../site/_util.js';

(function () {
  "use strict";

  var MenuTabs = function () {
    var CONST = {
      ACTIVE_TAB: 'cmp-tabs__tab--active',
      ACTIVE_PANEL: 'cmp-tabs__tabpanel--active',
      GENERIC_TAB_CLASS: 'cmp-tabs__tab',
      GENERIC_PANEL_CLASS: 'cmp-tabs__tabpanel'
    };

    var $tabsMenu, $tabsMenuContainer, $tablistMobile, $mobileTabsActive, $whiteMenu, $shareButton, $myActiveTab, $genericTab, $myActiveTabPanel, $genericPanel;

    function init() {
      $tabsMenu = $('.tabs-menu');
      if ($tabsMenu.length <= 0) {
        return;
      }

      $myActiveTab = $tabsMenu.find("." + CONST.ACTIVE_TAB);
      $myActiveTabPanel = $tabsMenu.find("." + CONST.ACTIVE_PANEL);
      $genericTab = $tabsMenu.find("." + CONST.GENERIC_TAB_CLASS);
      $genericPanel = $tabsMenu.find("." + CONST.GENERIC_PANEL_CLASS);
      $whiteMenu = false;
      $tabsMenuContainer = $('.tabs-menu__container');
      $shareButton = $(".cmp-share-desktop .cmp-button");

      $(window).on('scroll', function () {
		if ($("header.cmp-experiencefragment--header").length > 0 && !$(".cmp-button--mobile").hasClass("cmp-button--mobile__toggler_close")) {
		  if ($(window).scrollTop() > 40) {
			console.log('menu bianco scroll 40 dall alto 4');
			_addWhiteMenu();
		  } else {
			if ($("header .cmp-tabs__tabpanel--active").length < 1) {
			  console.log('rimuovi menu bianco');
			  _removeWhiteMenu();
			}
		  }
		}
	  });
	  

      $(document).ready(function () {
        if ($(window).height() < 600) {
          console.log('altezza < di 600');
          _addWhiteMenu();
        }
      });

      $(window).on('resize', function () {
        if ($(window).height() < 600) {
          console.log('altezza < di 600 al resize');
          _addWhiteMenu();
        } else {
          console.log('Height > 600');
        }
      });

      $('.cmp-tabs__tab').on('click', function () {
        console.log('menu 2lvl bianco al click menu');
        _addWhiteMenu();
      });

      window.addEventListener("resize", _isMobileWindowSize);

      if (!_isDesktop() || _isMobileWindowSize()) {
        let navbarText = $('.cmp-navbar .buildingblock').html();
        let navbarMobile = `<div class="cmp-navbar--text-mobile">${navbarText}</div>`;
        $('.menu-nav .cmp-tabs__tablist').append(navbarMobile);

        _toggleMenu();
        $tablistMobile = $('.cmp-tabs__tablist_mobile');
        if ($tablistMobile.length > 0) {
          $tabsMenuContainer.each(function (index, mobItem) {
            let dataPanelAttr = $(mobItem).data('cmp-panel-title');
            let panelClose = '<div class="panel-menu-toggler"><span></span><span></span></div>';
            let html = `<div class="tabs-mobile-panel__title">${panelClose}<h3>${dataPanelAttr}</h3></div>`;
            _prependHtml(mobItem, html);
          });

          $('.panel-menu-toggler').on('click', function () {
            $('.tabs-menu__container').removeClass('cmp-tabs__tabpanel--active');
          });
        }

        $('.cmp-button--mobile__toggler').on('click', function () {
          if ($(".cmp-button--mobile__toggler_close").length < 1) {
            $('.cmp-button--mobile').addClass('cmp-button--mobile__toggler_close');
            $('body').addClass('h-overflow');
			console.log('menu aperto');
          } else {
            $('.cmp-tabs__tablist_mobile').removeClass('cmp-tabs__tablist-opened');
            $('.tabs-menu__container').removeClass('cmp-tabs__tabpanel--active');
            $('.cmp-button--mobile').removeClass('cmp-button--mobile__toggler_close');
            $('body').removeClass('h-overflow');
            $('.cmp-navbar-overlayer').hide();
			console.log('menu chiuso');
          }
          $('.cmp-tabs__tablist_mobile').toggleClass('cmp-tabs__tablist_mobile-active');
          $mobileTabsActive = $('.cmp-tabs__tablist_mobile-active .cmp-tabs__tab');
          _addWhiteMenu();
          $mobileTabsActive.on('click', function () {
            $('.cmp-container--menu_item-mobile').addClass('cmp-container--menu_item-mobile--active');
          });
        });
      } else {
        if ($shareButton.length > 0) {
          $shareButton.on('click', function (e) {
            e.preventDefault();
            let shareOpened = false;

            $('.cmp-share-desktop .cmp-share__container').toggleClass('cmp-share__active');

            shareOpened = document.querySelectorAll('.cmp-share__active').length > 0;

            if (!shareOpened) {
              $('.cmp-navbar-overlayer').hide();
              $('.cmp-share__container').hide();
              $('body').removeClass('h-overflow');
            } else {
              $('.cmp-navbar-overlayer').show();
              $('.cmp-share__container').show();
              $('body').addClass('h-overflow');
            }

            if ($(window).scrollTop() == 0 && shareOpened) {
              $whiteMenu = false;
              _addWhiteMenu();
            }
          });
        }
      }
    }

    function _isMobileWindowSize() {
      var w = document.documentElement.clientWidth;
      if (w < 1200) {
        $('.cmp-tabs__tablist').addClass('cmp-tabs__tablist_mobile');
        return true;
      } else {
        $('.cmp-tabs__tablist').removeClass('cmp-tabs__tablist_mobile');
        return false;
      }
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
      if ($('.cmp-tabs__tablist_toggler').length > 0) {
        $('.cmp-tabs__tablist_toggler').toggleClass('cmp-tabs__tablist_mobile');
      }
      if ($('.tabs-menu__container').length > 0) {
        $('.tabs-menu__container').toggleClass('cmp-container--menu_item-mobile');
      }
    }

    return {
      init: init
    };
  }();

  $(function () {
    MenuTabs.init();
  });
}($));
