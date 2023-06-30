/* eslint-disable max-len */
function copyDataFromJson(query) {
  const domainName = window.location.hostname;
  const port = window.location.port;
  const protocol = window.location.protocol;
  const lang = document.documentElement.lang;
  const currentNodeSearch = document.querySelector(".currentNodeSearch").value;
  let url;
  //const url = `https://${domainName}/${lang}/search.searchresult.json?fulltext=${query}`;

  // Show loading spinner
  const loadingSpinner = document.createElement("div");
  loadingSpinner.classList.add("loading-spinner");
  document.body.appendChild(loadingSpinner);

  if (domainName === "localhost" && port === "4502") {
    url = `${protocol}//${domainName}:${port}${currentNodeSearch}.searchresult.json?fulltext=${query}`;
  } else if (domainName === "localhost") {
    url =
      "https://raw.githubusercontent.com/davide-mariotti/JSON/main/searchMT/search.json";
  } else {
    url = `${protocol}//${domainName}/${lang}/${currentNodeSearch}.searchresult.json?fulltext=${query}`;
  }

  fetch(url)
    .then((response) => response.json())
    .then((data) => {
      localStorage.setItem("searchResults", JSON.stringify(data));
      console.log("Data copied to local storage!");
      // Hide loading spinner
      loadingSpinner.remove();
    })
    .catch((error) => {
      console.error("Error copying data to local storage:", error);
      // Hide loading spinner
      loadingSpinner.remove();
    });
}

function displaySearchResults(dataResults) {
  const resultsContainer = document.querySelector("#search-results");
  resultsContainer.innerHTML = "";

  if (dataResults.results.length === 0) {
    resultsContainer.innerHTML = "No results found.";
    return;
  }

  const template = `
    <ul>
      ${dataResults.results
        .map(
          (result) => `
        <li>
          <a href="${result.url}" target="_self">${result.title}</a>
          <p>${result.description ? result.description : ""}</p>
        </li>
      `
        )
        .join("")}
    </ul>
  `;

  resultsContainer.innerHTML = template;
}

document.addEventListener("DOMContentLoaded", () => {
  const input = document.querySelector("#search-input");
  const searchButton = document.querySelector("#search-button");
  searchButton.disabled = true;

  if (searchButton) {
    input.addEventListener("keyup", function (event) {
      let val = event.target.value;
      if (val === "") {
        searchButton.disabled = true;
      } else {
        searchButton.disabled = false;
      }
    });

    // Gestore dell'evento "click"
    searchButton.addEventListener("click", () => {
      performSearch();
    });

    // Gestore dell'evento "keydown"
    input.addEventListener("keydown", (event) => {
      if (event.keyCode === 13) {
        performSearch();
      }
    });
  }

  function performSearch() {
    const query = input.value.toLowerCase().trim();
    copyDataFromJson(query);
    const searchResults = JSON.parse(localStorage.getItem("searchResults"));
    displaySearchResults(searchResults);
  }
});
