/* eslint-disable max-len */

/*const lang = document.documentElement.lang;
  const url = `https://raw.githubusercontent.com/davide-mariotti/davide-mariotti.github.io/main/search2_.json/${lang}`;
  fetch(url)*/

function copyDataFromJson() {  
  fetch('https://raw.githubusercontent.com/davide-mariotti/davide-mariotti.github.io/main/search2.json')
    .then(response => response.json())
    .then(data => {
      localStorage.setItem('searchResults', JSON.stringify(data));
      console.log('Data copied to local storage!');
    })
    .catch(error => {
      console.error('Error copying data to local storage:', error);
    });
}

function performSearch() {
  const input = document.querySelector('#search-input');
  const searchButton = document.querySelector('#search-button');
  const searchResults = JSON.parse(localStorage.getItem('searchResults'));

  searchButton.addEventListener('click', () => {
    const query = input.value.toLowerCase();
    const matchingResults = searchResults.filter(result => {
      return result.title.toLowerCase().includes(query) || (result.description && result.description.toLowerCase().includes(query));
    });
    displaySearchResults(matchingResults);
  });

  input.addEventListener('keydown', event => {
    if (event.key === 'Enter') {
      const query = input.value.toLowerCase();
      const matchingResults = searchResults.filter(result => {
        return result.title.toLowerCase().includes(query) || (result.description && result.description.toLowerCase().includes(query));
      });
      displaySearchResults(matchingResults);
    }
  });

}

function displaySearchResults(results) {
  const resultsContainer = document.querySelector('#search-results');
  resultsContainer.innerHTML = '';

  if (results.length === 0) {
    resultsContainer.innerHTML = 'No results found.';
    return;
  }

  /*const domainName = window.location.hostname;
  <a href="https://${domainName}${result.url}" target="_blank">${result.title}</a>*/

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
  copyDataFromJson();
  const searchButton = document.querySelector('#search-button');
  if (searchButton) {
    performSearch();
  } else {
    console.log('No search filter found.');
  }
});
