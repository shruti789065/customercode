$(function () {
  function changePage(pageNumber, pageSize, totalPages, totalResults) {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set("page", pageNumber);
    urlParams.set("pageSize", pageSize);
    urlParams.set("totalPages", totalPages);
    urlParams.set("totalResults", totalResults);

    const formData = $(".filter-form").serializeArray();
    $.each(formData, function (index, field) {
      if (field.value) {
        urlParams.set(field.name, field.value);
      }
    });

    window.location.search = urlParams.toString();
  }

  $(".pagination__controls").on("click", ".pagination__button", function () {
    const navElement = $(this).closest(".pagination__controls");
    const currentPage = parseInt(navElement.data("current-page"));
    const pageSize = parseInt(navElement.data("page-size"));
    const totalPages = parseInt(navElement.data("total-pages"));
    const totalResults = parseInt(navElement.data("total-results"));

    let nextPage = currentPage;
    if ($(this).hasClass("pagination__button--next")) {
      nextPage = Math.min(currentPage + 1, totalPages);
    } else if ($(this).hasClass("pagination__button--prev")) {
      nextPage = Math.max(currentPage - 1, 1);
    }

    changePage(nextPage, pageSize, totalPages, totalResults);
  });
});
