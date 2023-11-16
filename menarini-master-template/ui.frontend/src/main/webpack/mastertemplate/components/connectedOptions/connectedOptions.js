import $ from "jquery";
import { getJsonProperty } from "../../site/_util.js";

(function () {
  "use strict";

  var ConnectedOptions = (function () {
    const domainName = window.location.hostname;
    const port = window.location.port;
    const protocol = window.location.protocol;

    var connectedContainer,
      url,
      currentNodePath,
      jsonSelector,
      jsonArray,
      firstOption,
      secondOption;
    /**
     * Initializes the connected options
     *
     * @public
     */
    async function init() {
      if (
        document.querySelector(".cmp-connected--currentnode") !== null &&
        document.querySelector(".cmp-connected--json-selector") !== null
      ) {
        currentNodePath = document.querySelector(".cmp-connected--currentnode")
          .value;
        jsonSelector = document.querySelector(".cmp-connected--json-selector")
          .value;
      }

      if (document.querySelector(".cmp-connected-option-container") !== null) {
        connectedContainer = document.querySelector(
          ".cmp-connected-option-container"
        );
        const selects = connectedContainer.querySelectorAll("select");
        if (selects.length >= 2) {
          firstOption = selects[0];
          secondOption = selects[1];
        } else {
          console.error(
            "Non ci sono abbastanza elementi select all'interno di connectedContainer"
          );
          return;
        }
      }

      jsonArray = await callServlet(currentNodePath, jsonSelector);
      fillFirstOption(jsonArray);
      secondOption.innerHTML = `<option value="">Choose department</option>`;
      secondOption.disabled = true;

      if (firstOption != null) {
        firstOption.addEventListener("change", (event) => {
          fillSecondOption(event.target.value, jsonArray);
        });
      }
    }

    async function callServlet(node, _customSelector) {
      let jsonArray;

      if (domainName === "localhost" && port === "4502") {
        url = `${protocol}//${domainName}:${port}${node}.${_customSelector}.json`;
      } else {
        url = `${protocol}//${domainName}${node}.${_customSelector}.json`;
      }

      await fetch(url)
        .then((response) => response.json())
        .then((data) => {
          jsonArray = data;
        })
        .catch((error) => {
          console.error("Error:", error);
        });

      return jsonArray;
    }

    function fillFirstOption(jsonArray) {
      const titles = getJsonProperty(jsonArray, "title");
      const names = getJsonProperty(jsonArray, "name");

      let out = "";
      out += `<option value="">Choose country</option>`;
      for (let i = 0; i < titles.length; i++) {
        out += `<option value="${names[i]}">${titles[i]}</option>`;
      }
      firstOption.innerHTML = out;
    }

    function fillSecondOption(value, jsonArray) {
      const departments = getDepartments(jsonArray, value);

      let out = secondOption.innerHTML;

      for (const department of departments) {
        const { name, email } = department;
        out += `<option data-email="${email}" value="${email}">${name}</option>`;
      }

      secondOption.innerHTML = out;
	  secondOption.disabled = false;
    }

    function getDepartments(json, name) {
      const departments = [];

      for (const key in json) {
        if (json.hasOwnProperty(key)) {
          const fragment = json[key];

          if (fragment.name === name && fragment.hasOwnProperty("department")) {
            const departmentObject = fragment.department[0];

            if (departmentObject && typeof departmentObject === "object") {
              for (const departmentName in departmentObject) {
                if (departmentObject.hasOwnProperty(departmentName)) {
                  const email = departmentObject[departmentName];
                  departments.push({ name: departmentName, email: email });
                }
              }
            }
          }
        }
      }

      return departments;
    }

    return {
      init: init,
    };
  })();

  $(function () {
    ConnectedOptions.init();
  });
})($);
