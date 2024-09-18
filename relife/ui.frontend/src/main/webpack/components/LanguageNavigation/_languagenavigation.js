/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable max-len */
(function () {
	"use strict";

	const LANGUAGE_NAVIGATION = {
		NAV: ".cmp-languagenavigation",
		CMP_LANGUAGE_NAV_LABEL: ".cmp-languagenavigation__label",
		ACTIVE_LINK_SELECTOR:
			".cmp-languagenavigation__item--active > .cmp-languagenavigation__item-link",
		LABEL_TEXT_SELECTOR: ".cmp-languagenavigation__label-text",
	};

	/**
	 * Initializes the Language Navigation
	 *
	 * @public
	 */
	function init() {
		// check if country template
		var meta = document.querySelector('meta[name="template"][content="country"]');
		if (!meta) {
			document.querySelectorAll(LANGUAGE_NAVIGATION.NAV).forEach((navElement) => {
				const activeLink = navElement.querySelector(
					LANGUAGE_NAVIGATION.ACTIVE_LINK_SELECTOR
				);
				if (activeLink) {
					//const activeLanguageText = activeLink.getAttribute("lang").toUpperCase();
					const activeLanguageText = activeLink.title;
					const labelTextElement = navElement.querySelector(
						LANGUAGE_NAVIGATION.LABEL_TEXT_SELECTOR
					);
					if (labelTextElement) {
						labelTextElement.textContent = activeLanguageText;
					}

					// Update the text content of the active link to show the language code
					activeLink.textContent = activeLanguageText;
				}

				// Update the text content of all language navigation links to show the language code
				navElement.querySelectorAll(".cmp-languagenavigation__item-link").forEach((link) => {
					const langCode = link.getAttribute("lang").toUpperCase();
					const langtitle = link.title;
					link.textContent = langtitle;
				});

				const toggleIcon = navElement.querySelector(
					`${LANGUAGE_NAVIGATION.CMP_LANGUAGE_NAV_LABEL} .toggle-icon`
				);
				if (toggleIcon) {
					toggleIcon.addEventListener("click", (e) => {
						e.preventDefault();
						e.stopPropagation();

						// Remove 'is-open' class from all other language nav elements
						document
							.querySelectorAll(LANGUAGE_NAVIGATION.NAV)
							.forEach((otherNav) => {
								if (otherNav !== navElement) {
									otherNav.classList.remove("is-open");
								}
							});

						// Toggle 'is-open' class on the current language nav element
						navElement.classList.toggle("is-open");
					});
				}
			});

			// Close language navigation if clicked outside
			document.addEventListener("click", (e) => {
				document
					.querySelectorAll(LANGUAGE_NAVIGATION.NAV)
					.forEach((navElement) => {
						if (!navElement.contains(e.target)) {
							navElement.classList.remove("is-open");
						}
					});
			});
		}
	}

	document.addEventListener("DOMContentLoaded", init);

	/*-----------------------------------------------------------------------------*/
	
	function seturlPOPup() {
		// Check if the meta tag is present
		let isCountryTemplate = document.querySelector('meta[name="template"][content="country"]') !== null;
	
		// Change all the Corporate links
		let languageNavItems = document.querySelectorAll('.cmp-languagenavigation__group');
	
		languageNavItems.forEach(group => {
			let corporateLink = group.querySelector('a[href="/content/relife/corporate.html"]');
			if (corporateLink) {
				corporateLink.setAttribute('href', '/content/relife/corporate/en.html');
			}
		});
	
		if (!isCountryTemplate) {
			// Add click event for all language links
			let links = document.querySelectorAll('.cmp-languagenavigation__item-link');
	
			links.forEach(link => {
				link.addEventListener('click', function (e) {
					e.preventDefault(); // Prevent default navigation
	
					let currentUrl = window.location.href;
					let currentCountry = getCountryFromUrl(currentUrl);
					let targetCountry = getCountryFromUrl(link.getAttribute('href'));
	
					let popupTemplate = `
						<div class="popup-overlay">
							<div class="popup-content">
								<p>You are about to leave ${currentCountry} website to go to ${targetCountry} website, are you sure?</p>
								<button id="popup-no">No</button>
								<button id="popup-yes">Yes</button>
							</div>
						</div>
					`;
	
					document.body.insertAdjacentHTML('beforeend', popupTemplate);
	
					document.getElementById('popup-no').addEventListener('click', function () {
						document.querySelector('.popup-overlay').remove();
					});
	
					document.getElementById('popup-yes').addEventListener('click', function () {
						window.location.href = link.getAttribute('href');
					});
				});
			});
		}
	
		function getCountryFromUrl(url) {
			if (url.includes('/corporate/en.html')) return 'RELIFE International';
			if (url.includes('/it/it.html')) return 'RELIFE Italian';
			if (url.includes('/it/en.html')) return 'RELIFE Italian';
			if (url.includes('/de/de.html')) return 'RELIFE German';
			if (url.includes('/de/en.html')) return 'RELIFE German';
			if (url.includes('/fr/fr.html')) return 'RELIFE French';
			if (url.includes('/fr/en.html')) return 'RELIFE French';
			if (url.includes('/at/de.html')) return 'RELIFE Austria';
			if (url.includes('/at/en.html')) return 'RELIFE Austria';
			if (url.includes('/ie/en.html')) return 'RELIFE Ireland';
			if (url.includes('/th/th.html')) return 'RELIFE Thailand';
			if (url.includes('/th/en.html')) return 'RELIFE Thailand';
			if (url.includes('/gb/en.html')) return 'RELIFE United Kingdom';
			if (url.includes('/uk/en.html')) return 'RELIFE United Kingdom';
			return 'Unknown';
		}
	}
	
	document.addEventListener("DOMContentLoaded", seturlPOPup);

	/*-----------------------------------------------------------------------------*/
})();
