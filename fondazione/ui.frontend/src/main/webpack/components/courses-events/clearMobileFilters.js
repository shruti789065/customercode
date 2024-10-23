$(function () {
  const clearFilters = () => {
    $(".filter-dropdown").each(function () {
      const defaultText = $(this).data("default-text");
      $(this).text(defaultText);
    });

    $("#dateOrPeriod").val("");
    $(".flatpickr-input").val("");

    $(".clear-button").each(function () {
      $(this).trigger("click");
    });

    $("#clearDateOrPeriod").trigger("click");
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

  $(".filters-mobile-buttons__cancel").on("click", function () {
    clearFilters();
    clearUrlQueryStrings();
  });
});
