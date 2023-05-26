/* eslint-disable max-len */
import $ from 'jquery';
import { _isDesktop, _prependHtml } from '../../site/_util.js';

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

		var $tabsMenu, $tabsMenuContainer, $tablistMobile, $mobileTabsActive, $whiteMenu, $shareButton,
			$myActiveTab, $genericTab, $myActiveTabPanel, $genericPanel;

		/**
		 * Initializes the MenuTabs
		 *
		 * @public
		 */
		function init() {
			$tabsMenu = $('.tabs-menu');
			if ($tabsMenu.length <= 0) { return; }

			$myActiveTab = $tabsMenu.find("." + CONST.ACTIVE_TAB);
			$myActiveTabPanel = $tabsMenu.find("." + CONST.ACTIVE_PANEL);
			$genericTab = $tabsMenu.find("." + CONST.GENERIC_TAB_CLASS);
			$genericPanel = $tabsMenu.find("." + CONST.GENERIC_PANEL_CLASS);
			$whiteMenu = false;
			$tabsMenuContainer = document.querySelectorAll('.tabs-menu__container');
			$shareButton = document.querySelector(".cmp-share-desktop .cmp-button");

			$(window).on('scroll', function () {
				if (document.querySelectorAll("header.cmp-experiencefragment--header").length > 0) {
					if ($(window).scrollTop() > 40) {
						_addWhiteMenu();
					} else {
						if (document.querySelectorAll("header .cmp-tabs__tabpanel--active").length < 1) {
							_removeWhiteMenu();
						}
					}
				}
			});

			$(document).ready(function() {
				// Controllo iniziale al caricamento della pagina
				if ($(window).height() < 600) {
					_addWhiteMenu();
				}
			});
			
			$(window).on('resize', function() {
				// Controllo al ridimensionamento della finestra
				if ($(window).height() < 600) {
					_addWhiteMenu();
				} else {
					console.log('Height > 600');
				}
			});

			/********************************************************************************/
			// Funzione per gestire l'aggiunta della classe "h-overflow" al body
			function handleHeaderScroll() {
				var body = document.body;
				var header = document.querySelector('header');

				if (header.classList.contains('scrolled-page')) {
				body.classList.add('h-overflow');
				} else {
				body.classList.remove('h-overflow');
				}
			}
			// Aggiungi l'evento di caricamento della pagina per controllare lo stato iniziale dell'header
			window.addEventListener('load', handleHeaderScroll);
			// Aggiungi l'evento di cambio classe all'header per controllare ogni volta che l'header cambia
			var observer = new MutationObserver(handleHeaderScroll);
			observer.observe(document.querySelector('header'), { attributes: true, attributeFilter: ['class'] });
			/********************************************************************************/

			
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
				if ($shareButton !== undefined || $shareButton !== null) {
					$shareButton.addEventListener('click', (e) => {
						e.preventDefault();
						let shareOpened = false;

						document.querySelector('.cmp-share-desktop .cmp-share__container').classList.toggle('cmp-share__active');

						document.querySelectorAll('.cmp-share__active').length < 1 ? shareOpened = false : shareOpened = true;
						if (!shareOpened) {
							$('.cmp-navbar-overlayer').hide();
							$('.cmp-share__container').hide();
							document.querySelector('body').classList.remove('h-overflow');
						} else {
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

		function _isMobileWindowSize() {
			var w = document.documentElement.clientWidth;
			if (w < 1200) {
				document.querySelector('.cmp-tabs__tablist').classList.add('cmp-tabs__tablist_mobile');
				return true;
			} else {
				document.querySelector('.cmp-tabs__tablist').classList.remove('cmp-tabs__tablist_mobile');
				return false;
			}
		}

		function _addWhiteMenu() {
			if (!$whiteMenu) {
				document.querySelector("header.cmp-experiencefragment--header").classList.add("scrolled-page");
				$whiteMenu = true;
			}
		}

		function _removeWhiteMenu() {
			if ($whiteMenu) {
				document.querySelector("header.cmp-experiencefragment--header").classList.remove("scrolled-page");
				$whiteMenu = false;
			}
		}


		function _toggleMenu() {
			if (document.querySelectorAll('.cmp-tabs__tablist_toggler').length > 0) {
				document.querySelector('.cmp-tabs__tablist_toggler').classList.toggle('cmp-tabs__tablist_mobile');
			}
			if (document.querySelectorAll('.tabs-menu__container').length > 0) {
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
