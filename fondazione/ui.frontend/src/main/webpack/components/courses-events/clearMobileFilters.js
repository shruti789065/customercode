document.addEventListener("DOMContentLoaded", function () {
  const clearFilters = () => {
    document.querySelectorAll(".filter-dropdown").forEach((dropdown) => {
      const defaultText = dropdown.getAttribute("data-default-text");
      dropdown.textContent = defaultText;
    });

    document.getElementById("dateOrPeriod").value = "";
    document.querySelectorAll(".flatpickr-input").forEach((input) => {
      input.value = "";
    });

    document.querySelectorAll(".clear-button").forEach((button) => {
      button.click();
    });

    document.getElementById("clearDateOrPeriod").click();
  };

  const clearUrlQueryStrings = () => {
    const baseUrl =
      window.location.protocol +
      "//" +
      window.location.host +
      window.location.pathname;
    window.history.replaceState({}, "", baseUrl);
    location.reload();
  };

  document
    .querySelector(".filters-mobile-buttons__cancel")
    .addEventListener("click", function () {
      clearFilters();
      clearUrlQueryStrings();
    });
});
