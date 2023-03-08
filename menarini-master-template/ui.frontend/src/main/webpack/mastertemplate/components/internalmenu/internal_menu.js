/* eslint-disable max-len */
import $ from 'jquery';
import jQuery from 'jquery';
window.$ = jQuery;

(function () {
	"use strict";
	/**
	 * Internal Menù Component
	 *
	 * @class InternalMenu
	 * @classdesc initiating javascript for the Internal Menu Component.
	 */
	var InternalMenu = function () {
		var $internalMenu, $menuButton;
		
		/**
		 * Initializes the InternalMenu
		 *
		 * @public
		 */
		function init() {
			$internalMenu = document.querySelectorAll('.internalmenu');
			$menuButton = document.querySelector('.cmp-internalmenu__button .cmp-button');
			if ($internalMenu.length <= 0) {return;}

			$menuButton.addEventListener('click', (e) => {
				e.preventDefault();
				let buttonText="";
				document.querySelector('.cmp-internalmenu__button').classList.toggle('cmp-internalmenu__button-opened');
				document.querySelector('.internalmenu .cmp-navigation').classList.toggle('cmp-navigation--opened');
				document.querySelectorAll('.cmp-internalmenu__button-opened').length > 0 ? buttonText = 'close' : buttonText = 'menu';
				$('.cmp-button__text').text(buttonText);
			});
		}

		return {
			init: init
		};
	}();

	$(function () {
		InternalMenu.init();

	});

}($));
