/**
 * @file
 * Language navigation in header script.
 */

document.addEventListener("DOMContentLoaded", function () {
    "use strict";

    /**
     * Display the language dropdown.
     */
    function displayCurrentLanguage() {
        const CMP_SELECTOR = '.cmp-languagenavigation--header';
        const CMP_PROCESSED = 'data-lang-nav-processed';
        const ACTIVE_LINK_SELECTOR = '.cmp-languagenavigation__item--active > .cmp-languagenavigation__item-link';
        const ACTIVE_COUNTRY_SELECTOR = '.cmp-languagenavigation__item--level-0.cmp-languagenavigation__item--active';

        const languageNavigation = document.querySelector(CMP_SELECTOR + `:not([${CMP_PROCESSED}='true'])`);
        let activeCountryImg;
        let activeLanguage;
        let toggleButton;

        // Top Level Navigation (expected to only be one of these)
        if (languageNavigation) {
            // Mark as processed
            languageNavigation.setAttribute(CMP_PROCESSED, 'true');

            // Get the current language
            const activeLinkElement = languageNavigation.querySelector(ACTIVE_LINK_SELECTOR);
            activeLanguage = activeLinkElement ? activeLinkElement.getAttribute('lang') : 'Language';

            // Get the background image of the active country
            const activeCountryElement = languageNavigation.querySelector(ACTIVE_COUNTRY_SELECTOR);

            activeCountryImg = activeCountryElement ? getComputedStyle(activeCountryElement).backgroundImage : 'none';

            // Create the toggle button
            toggleButton = document.createElement("div");
            toggleButton.className = "cmp-languagenavigation--langnavtoggle";
            toggleButton.innerHTML =
                `
                    <a id="langNavToggleHeader"
                        style="background-image: ${activeCountryImg}"
                        href="#langNavToggle"
                        aria-label="Toggle Language">
                        ${activeLanguage}
                    </a>
                `;

            languageNavigation.prepend(toggleButton);

            // Attach toggle to change languages
            const langNavToggleHeader = document.getElementById("langNavToggleHeader");
            langNavToggleHeader.addEventListener("click", function () {
                const langNavMenu = languageNavigation.querySelector('.cmp-languagenavigation');
                langNavMenu.classList.toggle("showMenu");
                langNavToggleHeader.classList.toggle("open");
            });

            // Allow users to click anywhere to close language switcher
            window.addEventListener("click", function (event) {
                if (!event.target.matches('#langNavToggleHeader') && langNavToggleHeader.classList.contains('open')) {
                    const langNavMenu = languageNavigation.querySelector('.cmp-languagenavigation');
                    langNavMenu.classList.remove("showMenu");
                    langNavToggleHeader.classList.remove("open");
                }
            });
        }
    }

    displayCurrentLanguage();
});
