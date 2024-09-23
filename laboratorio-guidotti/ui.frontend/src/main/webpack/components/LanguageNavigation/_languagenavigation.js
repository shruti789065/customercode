/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable max-len */
(function () {
  "use strict";

  const LANGUAGE_NAVIGATION = {
    NAV: ".cmp-languagenavigation",
    CMP_HEADER_SELECTOR: ".cmp-languagenavigation--header",
    CMP_PROCESSED: "data-lang-nav-processed",
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
    document.querySelectorAll(LANGUAGE_NAVIGATION.NAV).forEach((navElement) => {
      const activeLink = navElement.querySelector(
        LANGUAGE_NAVIGATION.ACTIVE_LINK_SELECTOR
      );
      if (activeLink) {
        const activeLanguageText = activeLink.getAttribute("lang").toUpperCase();
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
        link.textContent = langCode;
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

  document.addEventListener("DOMContentLoaded", init);
})();
