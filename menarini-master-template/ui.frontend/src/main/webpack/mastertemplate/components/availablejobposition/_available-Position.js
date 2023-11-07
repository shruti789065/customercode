/* eslint-disable max-len */
import $ from "jquery";

(function () {
  "use strict";

  var Positions = (function () {
    var dropdown, url, currentNodeCountry, resultsContainer;
    /**
     * Initializes the job positions
     *
     * @public
     */
    function init() {
      const domainName = window.location.hostname;
      const port = window.location.port;
      const protocol = window.location.protocol;
      dropdown = document.querySelector("#dropdownCountries");
      if (document.querySelector(".currentNodeCountry") !== null) {
        currentNodeCountry = document.querySelector(".currentNodeCountry")
          .value;
      }

      if (document.querySelector("#availablePositionsResults") != null) {
        resultsContainer = document.querySelector("#availablePositionsResults");
        resultsContainer.style.display = "none";
      }

      function callServlet(selectedCountry) {
        if (domainName === "localhost" && port === "4502") {
          // eslint-disable-next-line max-len
          url = `${protocol}//${domainName}:${port}${currentNodeCountry}.searchJobPosition.json?country=${selectedCountry}`;
        } else {
          url = `${protocol}//${domainName}${currentNodeCountry}.searchJobPosition.json?country=${selectedCountry}`;
        }

        fetch(url)
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
            card = data.results
              .map(
                (result) => `
                              <div class="container responsivegrid cmp-container--border availablePosition aem-GridColumn--phone--12 aem-GridColumn aem-GridColumn--tablet--8 aem-GridColumn--default--8">
                                <div id="container-a5013d7404" class="cmp-container">
                                    <div class="aem-Grid aem-Grid--8 aem-Grid--default--8 aem-Grid--phone--12 ">
                                        <div class="spacer aem-GridColumn aem-GridColumn--default--8">
                                            <div style="width: 100%; min-height: 1px; height:20px; display: block;"></div>
                                        </div>
                                        <div
                                            class="title cmp-title--corporate aem-GridColumn--default--none aem-GridColumn aem-GridColumn--default--8 aem-GridColumn--offset--default--0">
                                            <div data-cmp-data-layer="{&quot;title-f19817d612&quot;:{&quot;@type&quot;:&quot;menarinimaster/components/title&quot;,&quot;repo:modifyDate&quot;:&quot;2023-06-07T15:37:34Z&quot;,&quot;dc:title&quot;:&quot;Position 1&quot;}}"
                                                id="title-f19817d612" class="cmp-title">
                                                <h5 class="cmp-title__text">${result.title}</h5>
                                            </div>
                                        </div>
                                        <div
                                            class="button cmp-title--primary cmp-button--small cmp-text-center aem-GridColumn--offset--phone--7 aem-GridColumn--default--none aem-GridColumn--phone--none aem-GridColumn--offset--default--6 aem-GridColumn aem-GridColumn--phone--5 aem-GridColumn--default--2">
                                            <a id="button-590321cb80" class="cmp-button" data-cmp-clickable=""
                                                data-cmp-data-layer="{&quot;button-590321cb80&quot;:{&quot;@type&quot;:&quot;menarinimaster/components/button&quot;,&quot;repo:modifyDate&quot;:&quot;2023-06-09T07:11:08Z&quot;,&quot;dc:title&quot;:&quot;READ MORE&quot;,&quot;xdm:linkURL&quot;:&quot;/content/menarini-demo/en/careers/open-positions/open-position-1.html&quot;}}"
                                                href="${result.url}.html"> <span
                                                    class="cmp-button__text">READ MORE</span> </a> </div>
                                    </div>
                                </div>
                              </div>
                                `
              )
              .join("");

            resultsContainer.innerHTML = card;
          })
          .catch((error) => {
            console.error("Error copying data to local storage:", error);
          });
      }
      if (dropdown != null) {
        dropdown.addEventListener("click", (e) => {
          e.preventDefault();
          e.target.classList.toggle("active");
        });
        dropdown.addEventListener("change", function () {
          var selectedValue = dropdown.value;
          if (resultsContainer != null) {
            if (selectedValue == "-") {
              resultsContainer.innerHTML = "";
              resultsContainer.style.display = "none";
            } else {
              callServlet(selectedValue);
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
