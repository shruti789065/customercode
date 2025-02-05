import $ from "jquery";
import { getUrl } from "../../site/_util.js";
(function () {
  "use strict";

  var Positions = (function () {
    var dropdownMarketList, currentNodeCountry, resultsContainer;
    /**
     * Initializes the job positions
     *
     * @public
     */
    function init() {
      dropdownMarketList = document.querySelector("#marketListDropdown");
      if (document.querySelector(".currentNodeCountry") !== null) {
        currentNodeCountry = document.querySelector(".currentNodeCountry")
          .value;
      }

      if (document.querySelector("#marketListResultContainer") != null) {
        resultsContainer = document.querySelector("#marketListResultContainer");
        resultsContainer.style.display = "none";
      }

      function callServletMarketList(selectedCountry) {
        const endpoint = `${currentNodeCountry}.marketList.json?country=${selectedCountry}`;

        fetch(getUrl(endpoint))
          .then((response) => response.json())
          .then((data) => {
            //console.log("Data copied to local storage!", data);
            resultsContainer.innerHTML = "";
            if (data.results.length === 0) {
              //console.log("No results found.");
              resultsContainer.style.display = "grid";
              resultsContainer.innerHTML = "No results found.";
              return;
            }
            let card = "";
            resultsContainer.style.display = "grid";
            card = `
                        <div class="marketlistcontainer">
                            ${data.results
                              .map((result) => {
                                if (result.iconPath != "") {
                                  return `
                                        <a class="iconUrl" href="${
                                          result.url.startsWith("/content")
                                            ? result.url + ".html"
                                            : result.url
                                        }" target="_self">
                                            <div class="marketlisticon" >
                                                <img src="${result.iconPath}">
                                            </div>
                                        </a>`;
                                } else {
                                  return "";
                                }
                              })
                              .join("")}
                        </div> `;
            resultsContainer.innerHTML = card;
          })
          .catch((error) => {
            console.error("Error copying data to local storage:", error);
          });
      }
      if (dropdownMarketList != null) {
        dropdownMarketList.addEventListener("click", (e) => {
          e.preventDefault();
          e.target.classList.toggle("active");
        });
        dropdownMarketList.addEventListener("change", function () {
          var selectedValue = dropdownMarketList.value;
          if (resultsContainer != null) {
            if (selectedValue == "-") {
              resultsContainer.innerHTML = "";
              resultsContainer.style.display = "none";
            } else {
              callServletMarketList(selectedValue);
            }
          }
          //console.log("selected value", selectedValue);
        });
      }
    }
    return {
      init: init,
    };
  })();

  $(function () {
    Positions.init();
  });
})($);
