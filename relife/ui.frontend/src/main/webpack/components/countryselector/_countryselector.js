/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable max-len */
(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", function() {

    // check if country template
    var meta = document.querySelector('meta[name="template"][content="country"]');
    if (meta) {
      document.body.classList.add("country-template");
      console.log('Country template detected');
    }

    // check if country selector exists
    const countrySelector = document.querySelector(".countryselector");
    if (!countrySelector) {
      console.log('Country selector not found');
      return;
    }

    // translate country selector
    const langMap = {
      en: 'Corporate',
      it: 'Italy',
      de: 'Germany',
      es: 'Spain',
      fr: 'France',
      ga: 'Ireland',
      ie: 'Ireland',
      gb: 'United Kingdom',
      uk: 'United Kingdom',
      at: 'Austria',
      th: 'Thailand'
    };

    // translate navigation
    const navItems = document.querySelectorAll(".cmp-languagenavigation__item-link");
    navItems.forEach(item => {
      const lang = item.getAttribute("lang");
      const shortLang = lang.split('-')[1];
      console.log(`Processing item with lang: ${lang}`);
      if (langMap[shortLang]) {
        item.innerHTML = langMap[shortLang];
        console.log(`Translated ${lang} to ${langMap[shortLang]}`);
      } else {
        console.log(`No translation found for ${shortLang}`);
      }
    });

    // translate label
    const labelMenu = document.querySelector(".cmp-languagenavigation__label");
    const label = document.querySelector(".cmp-languagenavigation__label-text");
    const navGroup = document.querySelector(".cmp-languagenavigation");

    if (label && navGroup) {
      console.log('Label and navGroup found');

      // handle clicks
      navItems.forEach(item => {
        item.addEventListener("click", function() {
          const lang = item.getAttribute("lang");
          if (langMap[lang]) {
            label.textContent = langMap[lang];
            navGroup.classList.remove("is-open");
            console.log(`Clicked on ${lang}, updated label to ${langMap[lang]}`);
          }
        });
      });

      // handle toggling
      labelMenu.addEventListener("click", function(event) {
        event.stopPropagation();
        navGroup.classList.toggle("is-open");
        console.log('Label clicked, toggling navGroup open class');
      });

      // close on outside click
      document.addEventListener("click", function(event) {
        if (!countrySelector.contains(event.target)) {
          navGroup.classList.remove("is-open");
          console.log('Clicked outside, removing navGroup open class');
        } else {
          console.log('Clicked inside countrySelector');
        }
      });

      // close on escape
      document.addEventListener("keydown", function(event) {
        if (event.key === "Escape") {
          navGroup.classList.remove("is-open");
          console.log('Escape key pressed, removing navGroup open class');
        }
      });

    } else {
      console.log('Label or navGroup not found');
    }

  });

})();
