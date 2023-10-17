/* eslint-disable max-len */
import $ from "jquery";

(function () {
  "use strict";

  var Search = (function () {
    var url,
      searchResults,
      query,
      input,
      searchButton,
      resultsContainer,
      currentNodeSearch,
      loadingSpinner;

    /**
     * Initializes the Search
     *
     * @public
     */
    function init() {
      const domainName = window.location.hostname;
      const port = window.location.port;
      const protocol = window.location.protocol;
      //const lang = document.documentElement.lang;
      if (document.querySelector(".currentNodeSearch") !== null) {
        currentNodeSearch = document.querySelector(".currentNodeSearch").value;

        input = document.querySelector("#search-input");
        resultsContainer = document.querySelector("#search-results");
        searchButton = document.querySelector("#search-button");
        searchButton.disabled = true;

        loadingSpinner = document.createElement("div");
        loadingSpinner.classList.add("loading-spinner");
      }
      function loadingSpinner() {
        resultsContainer.appendChild(loadingSpinner);
      }

      function copyDataFromJson(query) {
        //loadingSpinner();
        //localStorage.clear();
        if (domainName === "localhost" && port === "4502") {
          url = `${protocol}//${domainName}:${port}${currentNodeSearch}.searchresult.json?fulltext=${query}`;
        } else if (domainName === "localhost") {
          url =
            "https://raw.githubusercontent.com/davide-mariotti/JSON/main/searchMT/search.json";
        } else {
          url = `${protocol}//${domainName}${currentNodeSearch}.searchresult.json?fulltext=${query}`;
        }

        fetch(url)
          .then((response) => response.json())
          .then((data) => {
            localStorage.setItem("searchResults", JSON.stringify(data));
            console.log("Data copied to local storage!");
            // Hide loading spinner
            //loadingSpinner.remove();
          })
          .catch((error) => {
            console.error("Error copying data to local storage:", error);
            // Hide loading spinner
            //loadingSpinner.remove();
          });
      }

      function displaySearchResults(dataResults) {
        resultsContainer.innerHTML = "";
        let template = "";

        if (dataResults.results.length === 0) {
          resultsContainer.innerHTML = "No results found.";
          return;
        }

        template = `
		  <ul>
			${dataResults.results
        .map(
          (result) => `
			  <li>
				<a href="${result.url}" target="_self">${result.title}</a>
				<p>${result.description ? result.description : ""}</p>
			  </li>
			`
        )
        .join("")}
		  </ul>
		`;

        resultsContainer.innerHTML = template;
      }

      /* document.addEventListener("DOMContentLoaded", () => {
        input = document.querySelector("#search-input");
        searchButton = document.querySelector("#search-button");
        searchButton.disabled = true;
      }); */

      if (searchButton) {
        input.addEventListener("keyup", function (event) {
          let val = event.target.value;
          if (val === "") {
            searchButton.disabled = true;
          } else {
            searchButton.disabled = false;
          }
        });

        // Gestore dell'evento "click"
        searchButton.addEventListener("click", () => {
          performSearch();
        });

        // Gestore dell'evento "keydown"
        input.addEventListener("keydown", (event) => {
          if (event.keyCode === 13) {
            performSearch();
          }
        });
      }

      function performSearch() {
        query = input.value.toLowerCase().trim();
        copyDataFromJson(query);
        searchResults = JSON.parse(localStorage.getItem("searchResults"));
        if (searchResults !== null) {
          displaySearchResults(searchResults);
        }
      }
    }
    return {
      init: init,
    };
  })();

  $(function () {
    Search.init();
  });
})($);

/* eslint-disable max-len */
