import { isMobileDevice } from "../../../utils/isMobileDevice";

document.addEventListener("DOMContentLoaded", function () {
  const optionContainers = document.querySelectorAll(".options-container");
  const dropdownButtons = document.querySelectorAll(".filter-dropdown");
  const filterButtons = document.querySelectorAll(".filter-button");
  const clearFilterButtons = document.querySelectorAll(".clear-button");
  const filterMobileToggleButton = document.querySelector(
    ".filter-toggle-button"
  );
  const filterCancelButton = document.querySelector(
    ".filters-mobile-buttons__cancel"
  );
  const filterConfirmButton = document.querySelector(
    ".filters-mobile-buttons__confirm"
  );
  const filterForm = document.querySelector(".filter-form");

  let selectedFilters = {
    topics: [],
    eventTypes: [],
    locations: [],
  };

  function updateSelectedFilters() {
    let url = window.location.href;
    let cleanUrl = url.replace(/\?/g, "&").replace("&", "?");
    let urlParams = new URLSearchParams(new URL(cleanUrl).search);

    if (urlParams.has("topics")) {
      selectedFilters.topics = urlParams.get("topics").split("-");
    }
    if (urlParams.has("eventTypes")) {
      selectedFilters.eventTypes = urlParams.get("eventTypes").split("-");
    }
    if (urlParams.has("locations")) {
      selectedFilters.locations = urlParams.get("locations").split("-");
    }

    Object.keys(selectedFilters).forEach((key, index) => {
      const currentOptionsContainer = optionContainers[index];
      const currentFilterButtons =
        currentOptionsContainer.querySelectorAll(".filter-button");
      const currentSelectedFilters = selectedFilters[key];

      currentFilterButtons.forEach((button) => {
        if (
          currentSelectedFilters.includes(button.getAttribute("data-value"))
        ) {
          button.classList.add("filter-button--toggled");
        }
      });

      const textValues = Array.from(currentFilterButtons)
        .filter((button) => button.classList.contains("filter-button--toggled"))
        .map((button) => button.innerText.trim());

      updateButtonTitle(textValues, index);
    });
  }

  updateSelectedFilters();

  function handleFilterUpdate(currentOptionsContainer, index) {
    const toggledButtons = currentOptionsContainer.querySelectorAll(
      ".filter-button.filter-button--toggled"
    );
    currentOptionsContainer.classList.add("options-container--hidden");

    const dataValues = Array.from(toggledButtons).map((button) =>
      button.getAttribute("data-value")
    );
    const textValues = Array.from(toggledButtons).map((button) =>
      button.innerText.trim()
    );

    updateButtonTitle(textValues, index);
    buildQueryString(dataValues, index);
  }

  function buildQueryString(dataValues, index) {
    const currentUrl = new URL(window.location.href);
    const urlParams = new URLSearchParams(currentUrl.search);

    selectedFilters[Object.keys(selectedFilters)[index]] = dataValues;

    Object.keys(selectedFilters).forEach((key) => {
      if (selectedFilters[key].length > 0) {
        urlParams.set(key, selectedFilters[key].join("-"));
      } else {
        urlParams.delete(key);
      }
    });

    updateUrl(urlParams);
  }

  function updateUrl(urlParams) {
    const currentUrl = new URL(window.location.href);
    const baseUrl = currentUrl.origin + currentUrl.pathname;

    const dateOrPeriodParam = currentUrl.searchParams.get("dateOrPeriod");

    if (dateOrPeriodParam && !urlParams.has("dateOrPeriod")) {
      urlParams.set("dateOrPeriod", dateOrPeriodParam);
    }

    const finalUrlParams = urlParams.toString();
    const newUrl = finalUrlParams ? `${baseUrl}?${finalUrlParams}` : baseUrl;

    window.history.pushState({ path: newUrl }, "", newUrl);

    if (!isMobileDevice()) {
      location.reload();
    }
  }

  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape") {
      optionContainers.forEach((item, index) => {
        if (item.classList.contains("options-container--hidden")) return;
        handleFilterUpdate(item, index);
      });
    }
  });

  optionContainers.forEach((container) => {
    const buttons = container.querySelectorAll(".filter-button");
    const showMoreButton = container.querySelector(".show-more");

    buttons.forEach((button, index) => {
      if (index > 10) {
        button.classList.add("filter-button--hidden");
      }
    });

    if (showMoreButton) {
      showMoreButton.addEventListener("click", function (e) {
        e.preventDefault();
        buttons.forEach((button, index) => {
          if (index > 10) {
            button.classList.remove("filter-button--hidden");
          }
        });
        showMoreButton.classList.add("show-more--hidden");
      });
    }
  });

  function updateButtonTitle(selectedFilters, index) {
    let selectedFiltersText = selectedFilters.join(", ");
    const maxCharachters = 25;
    const clearButton = document.querySelectorAll(".clear-button")[index];

    if (selectedFiltersText.length > maxCharachters) {
      selectedFiltersText =
        selectedFiltersText.substring(0, maxCharachters) + "...";
    }
    if (selectedFilters.length !== 0) {
      dropdownButtons[index].innerText = selectedFiltersText;
      dropdownButtons[index].classList.add("filter-dropdown--selected");
      clearButton.style.display = "flex";
    } else {
      dropdownButtons[index].innerText =
        dropdownButtons[index].getAttribute("data-default-text");
      dropdownButtons[index].classList.remove("filter-dropdown--selected");
      clearButton.style.display = "none";
    }
  }

  document.querySelectorAll(".filter-top-bar").forEach((item, index) => {
    item.addEventListener("click", function (e) {
      e.preventDefault();
      handleFilterUpdate(item.parentElement, index);
    });
  });

  document.addEventListener("click", function (e) {
    let clickedOutside = true;

    dropdownButtons.forEach((button) => {
      if (button.contains(e.target)) {
        clickedOutside = false;
      }
    });

    optionContainers.forEach((container) => {
      if (container.contains(e.target)) {
        clickedOutside = false;
      }
    });

    if (clickedOutside) {
      optionContainers.forEach((item, index) => {
        if (item.classList.contains("options-container--hidden")) return;
        handleFilterUpdate(item, index);
      });
    }
  });

  dropdownButtons.forEach(function (button, index) {
    button.addEventListener("click", function (e) {
      e.preventDefault();
      if (!optionContainers[index]) return;
      optionContainers[index].classList.remove("options-container--hidden");
    });
  });

  clearFilterButtons.forEach(function (button, index) {
    button.addEventListener("click", function (e) {
      e.preventDefault();
      const currentFilterButtons =
        optionContainers[index].querySelectorAll(".filter-button");
      currentFilterButtons.forEach(function (button) {
        button.classList.remove("filter-button--toggled");
      });
      handleFilterUpdate(optionContainers[index], index);
    });
  });

  filterButtons.forEach(function (button) {
    button.addEventListener("click", function (e) {
      e.preventDefault();
      button.classList.toggle("filter-button--toggled");
    });
  });

  function toggleFilterForm(e) {
    e.preventDefault();
    filterForm.classList.toggle("filter-form--hidden");
  }

  const applyMobileFilters = () => {
    if (isMobileDevice()) {
      location.reload();
    }
  };

  filterMobileToggleButton.addEventListener("click", toggleFilterForm);
  filterCancelButton.addEventListener("click", toggleFilterForm);
  filterConfirmButton.addEventListener("click", toggleFilterForm);
  filterConfirmButton.addEventListener("click", applyMobileFilters);
});
