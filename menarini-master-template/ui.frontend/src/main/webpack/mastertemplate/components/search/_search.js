/* eslint-disable max-len */
function copyDataFromJson(query) {
  const lang = document.documentElement.lang;
  const domainName = window.location.hostname;
  const url = `https://${domainName}/${lang}/search.searchresult.json?fulltext=${query}`;
  
  // Show loading spinner
  const loadingSpinner = document.createElement('div');
  loadingSpinner.classList.add('loading-spinner');
  document.body.appendChild(loadingSpinner);

  fetch(url)
    .then(response => response.json())
    .then(data => {
      localStorage.setItem('searchResults', JSON.stringify(data));
      console.log('Data copied to local storage!');
      // Hide loading spinner
      loadingSpinner.remove();
    })
    .catch(error => {
      console.error('Error copying data to local storage:', error);
      // Hide loading spinner
      loadingSpinner.remove();
    });
}

function displaySearchResults(results) {
  const resultsContainer = document.querySelector('#search-results');
  resultsContainer.innerHTML = '';

  if (results.length === 0) {
    resultsContainer.innerHTML = 'No results found.';
    return;
  }

  const template = `
    <ul>
      ${results.map(result => `
        <li>
          <a href="${result.url}" target="_self">${result.title}</a>
          <p>${result.description ? result.description : ''}</p>
        </li>
      `).join('')}
    </ul>
  `;

  resultsContainer.innerHTML = template;
}

document.addEventListener('DOMContentLoaded', () => {
  const input = document.querySelector('#search-input');
  const searchButton = document.querySelector('#search-button');

  if (searchButton) {
    searchButton.addEventListener('click', () => {
      const query = input.value.toLowerCase().trim();
      copyDataFromJson(query);
      const searchResults = JSON.parse(localStorage.getItem('searchResults'));
      displaySearchResults(searchResults);
    });
  } else {
    //console.log('NoSearchGlobal.');
  }
});
