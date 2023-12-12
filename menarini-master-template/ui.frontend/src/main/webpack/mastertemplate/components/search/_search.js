/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable max-len */
import $ from "jquery";
import { showOverlayAndLoader, hideOverlayAndLoader } from "../../site/_util";

$(function () {
  const Search = {
    url: "",
    searchResults: null,
    query: "",
    input: null,
    searchButton: null,
    resultsContainer: null,
    currentNodeSearch: "",

    init() {
      this.initializeVariables();
      this.setupEventListeners();
    },

    initializeVariables() {
      const domainName = window.location.hostname;
      const port = window.location.port;
      const protocol = window.location.protocol;

      this.currentNodeSearch = $(".currentNodeSearch").val();
      this.input = $("#search-input");
      this.resultsContainer = $("#search-results");
      this.searchButton = $("#search-button");
      this.searchButton.prop("disabled", true);
    },

    copyDataFromJson(query, domainName, port, protocol, currentNodeSearch) {
      const url =
        domainName === "localhost" && port === "4502"
          ? `${protocol}//${domainName}:${port}${currentNodeSearch}.searchresult.json?fulltext=${query}`
          : domainName === "localhost"
          ? "https://raw.githubusercontent.com/davide-mariotti/JSON/main/searchMT/search.json"
          : `${protocol}//${domainName}${currentNodeSearch}.searchresult.json?fulltext=${query}`;

      fetch(url)
        .then((response) => response.json())
        .then((data) => {
          localStorage.setItem("searchResults", JSON.stringify(data));
        })
        .catch((error) => {
          console.error("Error copying data to local storage:", error);
        });
    },

    displaySearchResults(dataResults) {
      this.resultsContainer.html(
        dataResults.results.length === 0
          ? "No results found."
          : `<ul>${dataResults.results
              .map(
                (result) =>
                  `<li><a href="${result.url}" target="_self">${
                    result.title
                  }</a><p>${result.description || ""}</p></li>`
              )
              .join("")}</ul>`
      );
    },

    setupEventListeners() {
      this.input.on("keyup", (event) => {
        this.searchButton.prop("disabled", !event.target.value.trim());
      });

      this.searchButton.on("click", () => {
        this.performSearch();
      });

      this.input.on("keydown", (event) => {
        if (event.keyCode === 13) {
          this.performSearch();
        }
      });
    },

    performSearch() {
      this.query = this.input.val().toLowerCase().trim();
      showOverlayAndLoader(this.resultsContainer, false);
      this.copyDataFromJson(
        this.query,
        window.location.hostname,
        window.location.port,
        window.location.protocol,
        this.currentNodeSearch
      );
      setTimeout(() => {
        this.searchResults = JSON.parse(localStorage.getItem("searchResults"));
        if (this.searchResults !== null) {
          this.displaySearchResults(this.searchResults);
        }
        hideOverlayAndLoader(this.resultsContainer);
      }, 1000);
    },
  };
  Search.init();
});
/* eslint-disable max-len */
