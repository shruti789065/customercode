(function () {
    "use strict";

    function displayCurrentLanguage() {
        const CMP_SELECTOR = '.cmp-languagenavigation';
        const CMP_PROCESSED = 'data-lang-nav-processed';
        const ACTIVE_LINK_SELECTOR = '.cmp-languagenavigation__item--active > .cmp-languagenavigation__item-link';
        const ACTIVE_COUNTRY_SELECTOR = '.cmp-languagenavigation__item--active';
        const langNav = document.querySelector(CMP_SELECTOR);
        
        if (langNav && !langNav.hasAttribute(CMP_PROCESSED)) {

            langNav.setAttribute(CMP_PROCESSED, 'true');

            let activeLanguage = langNav.querySelector(ACTIVE_LINK_SELECTOR)?.getAttribute('lang') || 'Language';
            let activeCountryImg = getComputedStyle(langNav.querySelector(ACTIVE_COUNTRY_SELECTOR))?.backgroundImage || 'none';
            
            activeCountryImg = activeCountryImg.replace(/"/g, "'");

            let toggleButton = document.createElement('div');
            toggleButton.className = 'cmp-languagenavigation--langnavtoggle';
            toggleButton.innerHTML = `<a id="langNavToggleHeader" style="background-image:${activeCountryImg}" href="#langNavToggle" aria-label="Toggle Language">${activeLanguage}</a>`;
            langNav.prepend(toggleButton);

            const langNavToggleHeader = document.getElementById('langNavToggleHeader');
            if (langNavToggleHeader) {
                langNavToggleHeader.addEventListener('click', function () {
                    const displayPosition = langNavToggleHeader.getBoundingClientRect().left - 240;
                    const langNavElement = langNav.querySelector('.cmp-languagenavigation__group');
                    langNavElement.style.left = `${displayPosition}px`;
                    langNavElement.classList.toggle('showMenu');
                    langNavToggleHeader.classList.toggle('open');
                });
            }

            window.addEventListener('click', function (event) {
                if (langNavToggleHeader && !event.target.closest('#langNavToggleHeader') && langNavToggleHeader.classList.contains('open')) {
                    const langNavElement = langNav.querySelector('.cmp-languagenavigation__group');
                    langNavElement.classList.remove('showMenu');
                    langNavToggleHeader.classList.remove('open');
                }
            });
        }
    }

    document.addEventListener("DOMContentLoaded", function () {
        displayCurrentLanguage();
    });
})();
