/* eslint-disable max-len */
import $ from 'jquery';
import jQuery from 'jquery';
window.$ = jQuery;

(function () {

	"use strict";

	/**
	 * MenuTabs Component
	 *
	 * @class MenuTabs
	 * @classdesc initiating javascript for the MenuTabs Component.
	 */
	var MenuTabs = function () {

		var CONST = {
			ACTIVE_TAB: 'cmp-tabs__tab--active',
			ACTIVE_PANEL: 'cmp-tabs__tabpanel--active',
			GENERIC_TAB_CLASS: 'cmp-tabs__tab',
			GENERIC_PANEL_CLASS: 'cmp-tabs__tabpanel'
		};

		var $tabsMenu, $tabsMenuContainer, $tablistMobile, $myActiveTab, $myActiveTabPanel, $genericTab, $genericPanel,
			$mobileTabsActive, $whiteMenu, $shareButton, $mobileResize,$headerFragment;

		/**
		 * Initializes the MenuTabs
		 *
		 * @public
		 */
		function init() {
			$tabsMenu = $('.tabs-menu');
			if ($tabsMenu.length <= 0) {return;}

			$myActiveTab = $tabsMenu.find("." + CONST.ACTIVE_TAB);
			$myActiveTabPanel = $tabsMenu.find("." + CONST.ACTIVE_PANEL);
			$genericTab = $tabsMenu.find("." + CONST.GENERIC_TAB_CLASS);
			$genericPanel = $tabsMenu.find("." + CONST.GENERIC_PANEL_CLASS);
			$whiteMenu = false;
			$tabsMenuContainer = document.querySelectorAll('.tabs-menu__container');
			$shareButton = document.querySelector(".cmp-share-desktop .cmp-button");
			$headerFragment = document.querySelector("header.cmp-experiencefragment--header");

			$(window).on('scroll', function () {
				if ($headerFragment) {
					if ($(window).scrollTop() > 40) {
						_addWhiteMenu();
					} else {
						if (document.querySelectorAll("header .cmp-tabs__tabpanel--active").length < 1) {
							_removeWhiteMenu();
						}
					}
				}
			});

			$('.cmp-tabs__tab').on('click', () => {
				_addWhiteMenu();
			});

			window.addEventListener("resize", _isMobileWindowSize);

			if (!_isDesktop() || _isMobileWindowSize()) {

				let navbarText = $('.cmp-navbar .buildingblock').html();
				let navbarMobile = `<div class="cmp-navbar--text-mobile">${navbarText}</div>`;
				$('.menu-nav .cmp-tabs__tablist').append(navbarMobile);

				_toggleMenu();
				$tablistMobile = document.querySelectorAll('.cmp-tabs__tablist_mobile');
				if ($tablistMobile.length > 0) {
					$tabsMenuContainer.forEach(mobItem => {
						let dataPanelAttr = mobItem.getAttribute('data-cmp-panel-title');
						let panelClose = `<div class="panel-menu-toggler"><span></span><span></span></div>`;
						let html = `<div class="tabs-mobile-panel__title">${panelClose}<h3>${dataPanelAttr}</h3></div>`;
						_prependHtml(mobItem, html);
					});

					$('.panel-menu-toggler').on('click', () => {
						$('.tabs-menu__container').removeClass('cmp-tabs__tabpanel--active');
					});
				}

				document.querySelector('.cmp-button--mobile__toggler').addEventListener('click', () => {
					if (document.querySelectorAll(".cmp-button--mobile__toggler_close").length < 1) {
						document.querySelector('.cmp-button--mobile').classList.add('cmp-button--mobile__toggler_close');
						document.querySelector('body').classList.add('h-overflow');
					} else {
						$('.cmp-tabs__tablist_mobile').removeClass('cmp-tabs__tablist-opened');
						$('.tabs-menu__container').removeClass('cmp-tabs__tabpanel--active');
						document.querySelector('.cmp-button--mobile').classList.remove('cmp-button--mobile__toggler_close');
						document.querySelector('body').classList.remove('h-overflow');
						$('.cmp-navbar-overlayer').hide();

					}
					document.querySelector('.cmp-tabs__tablist_mobile').classList.toggle('cmp-tabs__tablist_mobile-active');
					$mobileTabsActive = document.querySelectorAll('.cmp-tabs__tablist_mobile-active .cmp-tabs__tab');
					_addWhiteMenu();

					$mobileTabsActive.forEach(mobItem => {
						mobItem.addEventListener('click', () => {
							document.querySelector('.cmp-container--menu_item-mobile').classList.add('cmp-container--menu_item-mobile--active');
						});
					});
				});
			} else {
				if($shareButton !== undefined || $shareButton !== null){
					$shareButton.addEventListener('click', (e) => {
						e.preventDefault();
						let shareOpened = false;
	
						document.querySelector('.cmp-share-desktop .cmp-share__container').classList.toggle('cmp-share__active');
	
						document.querySelectorAll('.cmp-share__active').length < 1 ? shareOpened = false : shareOpened = true;
						if(!shareOpened){
							$('.cmp-navbar-overlayer').hide();
							$('.cmp-share__container').hide();
							document.querySelector('body').classList.remove('h-overflow');
						}else{
							$('.cmp-navbar-overlayer').show();
							$('.cmp-share__container').show();
							document.querySelector('body').classList.add('h-overflow');
						}
						
						
						if ($(window).scrollTop() == 0 && shareOpened) { 
							$whiteMenu = false;
							_addWhiteMenu();
						}
					});
				}
			}
		}

		function _isMobileWindowSize(){
			var w = document.documentElement.clientWidth;
			if(w < 1200){
				document.querySelector('.cmp-tabs__tablist').classList.add('cmp-tabs__tablist_mobile');
				return true;
			}else{
				document.querySelector('.cmp-tabs__tablist').classList.remove('cmp-tabs__tablist_mobile');
				return false;
			}
		}

		function _addWhiteMenu() {
			if (!$whiteMenu && $headerFragment) {
				document.querySelector("header.cmp-experiencefragment--header").classList.add("scrolled-page");
				$whiteMenu = true;
			}
		}

		function _removeWhiteMenu() {
			if ($whiteMenu && $headerFragment) {
				document.querySelector("header.cmp-experiencefragment--header").classList.remove("scrolled-page");
				$whiteMenu = false;
			}
		}

		function _isDesktop() {
			if (window.innerWidth < 1200) {
				return false;
			} else {
				return true;
			}
		}

		function _prependHtml(el, str) {
			var div = document.createElement('div'); //container to append to
			div.innerHTML = str;
			while (div.children.length > 0) {
				el.prepend(div.children[0]);
			}
		}

		function _toggleMenu() {
			if(document.querySelectorAll('.cmp-tabs__tablist_toggler').length > 0){
				document.querySelector('.cmp-tabs__tablist_toggler').classList.toggle('cmp-tabs__tablist_mobile');
			}
			if(document.querySelectorAll('.tabs-menu__container').length > 0){
				document.querySelector('.tabs-menu__container').classList.toggle('cmp-container--menu_item-mobile');
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
