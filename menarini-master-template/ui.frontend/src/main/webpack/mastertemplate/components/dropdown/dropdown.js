import $ from "jquery";

(function () {
  "use strict";

  var AutocompleteDropdown = (function () {
    function init() {
      var selectElements = document.querySelectorAll(
        ".cmp-form-options__field--drop-down"
      );

      selectElements.forEach(function (selectElement) {
        createCustomSelect(selectElement);
      });
    }

    function copyAttributes(source, destination) {
      Array.from(source.attributes).forEach((attr) => {
        if (attr.name !== "id" && attr.name !== "class") {
          destination.setAttribute(attr.name, attr.value);
        }
      });
    }

    function createInputFilter() {
      const inputFilter = document.createElement("input");
      inputFilter.type = "text";
      inputFilter.className = "select-filter";
      inputFilter.placeholder = "Search value...";
      return inputFilter;
    }

    function createCustomSelectElement(select) {
      const customSelect = document.createElement("div");
      customSelect.classList.add("cmp-cloned-options__field--drop-down");
      copyAttributes(select, customSelect);
      return customSelect;
    }

    function createOptionsList() {
      const optionsList = document.createElement("ul");
      optionsList.classList.add("cmp-cloned--options-list");
      return optionsList;
    }

    /* function createSelectedValue(select) {
      const selectedValueSpan = document.createElement("span");
      let selectedValue = selectedValue.textContent;
      selectedValueSpan.classList.add("cmp--selected-value");
      selectedValue = select[0].text;
      //select.value = selectedValue;
      return selectedValueSpan;
    } */
    function createSelectedValue(select) {
      const selectedValueSpan = document.createElement("span");
      let selectedValue = select[0].text; // Inizializza selectedValue con il valore desiderato
      selectedValueSpan.classList.add("cmp--selected-value");
      selectedValueSpan.textContent = selectedValue; // Assegna il valore al testo del span
      //select.value = selectedValue;
      return selectedValueSpan;
    }
    function setOldSelectValue(oldSelect, value) {
      const oldOptions = oldSelect.querySelectorAll("option");
      oldOptions.forEach(function (option) {
        if (option.textContent === value) {
          option.selected = true;
        } else {
          option.selected = false;
        }
      });
    }

    function createListItem(option, selectedValue, customSelect, optionsList) {
      const listItem = document.createElement("li");
      listItem.textContent = option.textContent;
      listItem.classList.add("cmp-cloned--option-item");

      Array.from(option.attributes).forEach((attr) => {
        listItem.setAttribute(attr.name, attr.value);
      });

      listItem.addEventListener("click", function () {
        selectedValue.textContent = option.textContent;
        customSelect.classList.remove("show-options");
      });

      optionsList.appendChild(listItem);
    }

    function handleCustomSelectClick(e, customSelect, oldSelect) {
      const selectedValue = customSelect.querySelector(".cmp--selected-value");

      if (
        e.target.classList.contains("cmp-cloned-options__field--drop-down") ||
        e.target.classList.contains("cmp--selected-value")
      ) {
        customSelect.classList.toggle("show-options");
      } else if (e.target.classList.contains("cmp-cloned--option-item")) {
        const newValue = e.target.textContent;
        selectedValue.textContent = newValue;
        setOldSelectValue(oldSelect, newValue); // Imposta il valore nella vecchia select
        customSelect.classList.remove("show-options");
      }
    }

    function handleInputFilterClick(e) {
      if (e.target.classList.contains("select-filter")) {
        e.stopPropagation();
      }
    }

    function handleInputFilterInput(inputFilter, optionsList) {
      var filterValue = inputFilter.value.toLowerCase();
      const optionsItems = optionsList.querySelectorAll("li");
      optionsItems.forEach(function (option) {
        var optionText = option.textContent.toLowerCase();
        if (optionText.includes(filterValue)) {
          option.style.display = "";
        } else {
          option.style.display = "none";
        }
      });
    }

    function createCustomSelect(select) {
      const inputFilter = createInputFilter();
      const customSelect = createCustomSelectElement(select);
      const optionsList = createOptionsList();
      const selectedValue = createSelectedValue(select);

      optionsList.appendChild(inputFilter);

      const options = select.querySelectorAll("option");
      options.forEach(function (option) {
        createListItem(option, selectedValue, customSelect, optionsList);
      });

      customSelect.appendChild(selectedValue);
      customSelect.appendChild(optionsList);

      customSelect.addEventListener("click", function (e) {
        handleCustomSelectClick(e, customSelect, select);
      });

      inputFilter.addEventListener("click", function (e) {
        handleInputFilterClick(e);
      });

      inputFilter.addEventListener("input", function () {
        handleInputFilterInput(inputFilter, optionsList);
      });

      select.style.display = "none";
      select.parentNode.insertBefore(customSelect, select.nextSibling);
    }

    return {
      init: init,
    };
  })();

  $(function () {
    AutocompleteDropdown.init();
  });
})($);
