import $ from "jquery";
import {
  _getJsonProperty,
  _generateUniqueValue,
  getUrl,
} from "../../site/_util.js";

(function () {
  "use strict";

  var ConnectedOptions = (function () {
    var connectedContainer,
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
      if (connectedContainer) {
        jsonArray = await callServlet(currentNodePath, jsonSelector);
        fillFirstOption(jsonArray, firstOption.querySelector("option").value);
        secondOption.innerHTML = `<option value="${
          secondOption.querySelector("option").value
        }">
	  ${secondOption.querySelector("option").value}</option>`;
        secondOption.disabled = true;

        if (firstOption != null) {
          firstOption.addEventListener("change", (event) => {
            fillSecondOption(
              event.target.value,
              jsonArray,
              secondOption.querySelector("option").value
            );
          });
        }
        if (secondOption != null) {
          secondOption.addEventListener("change", (event) => {
            let inputElement = document.querySelector(
              'input[type="hidden"][name="_crypted-value_"]'
            );

            let selectedOption =
              event.target.options[event.target.selectedIndex];
            let dataEmailValue = selectedOption.getAttribute("data-email");
            inputElement.value = dataEmailValue;
          });
        }
      }
    }

    async function callServlet(node, _customSelector) {
      let jsonArray;
      const endpoint = `${node}.${_customSelector}.json`;
      await fetch(getUrl(endpoint))
        .then((response) => response.json())
        .then((data) => {
          jsonArray = data;
        })
        .catch((error) => {
          console.error("Error:", error);
        });

      return jsonArray;
    }

    function fillFirstOption(jsonArray, placeholder) {
      const titles = _getJsonProperty(jsonArray, "title");
      const names = _getJsonProperty(jsonArray, "name");

      let out = "";
      out += `<option value="">${placeholder}</option>`;
      for (let i = 0; i < titles.length; i++) {
        out += `<option value="${names[i]}">${titles[i]}</option>`;
      }
      firstOption.innerHTML = out;
    }

    function fillSecondOption(value, jsonArray, placeholder) {
      const departments = getDepartments(jsonArray, value);
      let inputElement = document.querySelector(
        'input[type="hidden"][name="_crypted-value_"]'
      );
      inputElement.value = "";
      let out = "";
      out += `<option value="${placeholder}">${placeholder}</option>`;

      for (const department of departments) {
        const { name, email } = department;
        out += `<option data-email="${email}" value="${_generateUniqueValue(
          name,
          email
        )}">${name}</option>`;
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
