/* eslint-disable max-len */
import $ from 'jquery';
import jQuery from 'jquery';
window.$ = jQuery;

(function () {
	"use strict";
	/**
	 * Card Avatar Component
	 *
	 * @class CardAvatar
	 * @classdesc initiating javascript for the Card Avatar Component.
	 */
	var CardAvatar = function () {
		var $cardButton;
		
		/**
		 * Initializes the CardAvatar
		 *
		 * @public
		 */
		function init() {
			
			document.querySelectorAll('.cmp-card-avatar__button').forEach((item) => {
				item.addEventListener('click', (e) => {
					e.preventDefault();
					e.target.classList.toggle("active");
					if(e.target.nextElementSibling !== null){
						e.target.nextElementSibling.classList.toggle('cmp-card-avatar__description_show');
					}
				});
				
			});
		}

		return {
			init: init
		};
	}();

	$(function () {
		CardAvatar.init();

	});

}($));
