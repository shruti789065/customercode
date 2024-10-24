document.addEventListener("DOMContentLoaded", function () {
  function changePage(pageNumber, pageSize, totalPages, totalResults) {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set("page", pageNumber);
    urlParams.set("pageSize", pageSize);
    urlParams.set("totalPages", totalPages);
    urlParams.set("totalResults", totalResults);

    const formData = new FormData(document.querySelector(".filter-form"));
    formData.forEach((value, key) => {
      if (value) {
        urlParams.set(key, value);
      }
    });

    window.location.search = urlParams.toString();
  }

  document
    .querySelector(".pagination__controls")
    .addEventListener("click", function (event) {
      if (event.target.classList.contains("pagination__button")) {
        const navElement = event.target.closest(".pagination__controls");
        const currentPage = parseInt(navElement.dataset.currentPage);
        const pageSize = parseInt(navElement.dataset.pageSize);
        const totalPages = parseInt(navElement.dataset.totalPages);
        const totalResults = parseInt(navElement.dataset.totalResults);

        let nextPage = currentPage;
        if (event.target.classList.contains("pagination__button--next")) {
          nextPage = Math.min(currentPage + 1, totalPages);
        } else if (
          event.target.classList.contains("pagination__button--prev")
        ) {
          nextPage = Math.max(currentPage - 1, 1);
        }

        changePage(nextPage, pageSize, totalPages, totalResults);
      }
    });
});
