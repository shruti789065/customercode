/* eslint-disable max-len */
import _ from "lodash";




const copyDataFromJson =()=> {
  fetch("https://davide-mariotti.github.io/lodash/data.json")
    .then((response) => response.json()) // Convert response to JSON
    .then((data) => {
      localStorage.setItem("myData", JSON.stringify(data)); // Store data in local storage as string
    })
    .catch((error) => console.error(error)); // Log error if fetch fails

};

const getData = ()=> {
  return   JSON.parse(localStorage.getItem("myData")); // Retrieve data from local storage
};

// Change the color of the typology text based on its value
const changeTypologyColor= ()=> {
    var elements = document.getElementsByClassName("typology");
    for (var i = 0; i < elements.length; i++) {
      // Check the value of each typology text and apply the corresponding color
      if (elements[i].textContent === "Articles") {
        elements[i].style.backgroundColor = "red";
      }
      if (elements[i].textContent === "Report") {
        elements[i].style.backgroundColor = "green";
      }
      if (elements[i].textContent === "Perspective") {
        elements[i].style.backgroundColor = "yellow";
        elements[i].style.color = "black";
      }
      if (elements[i].textContent === "Correspondence") {
        elements[i].style.backgroundColor = "gray";
      }
      if (elements[i].textContent === "News") {
        elements[i].style.backgroundColor = "blue";
      }
      if (elements[i].textContent === "Original Investigation") {
        elements[i].style.backgroundColor = "black";
      }
      if (elements[i].textContent === "New Result") {
        elements[i].style.backgroundColor = "brown";
      }
      if (elements[i].textContent === "Editorial") {
        elements[i].style.backgroundColor = "orange";
      }
      if (elements[i].textContent === "Comment") {
        elements[i].style.backgroundColor = "purple";
      }
      if (elements[i].textContent === "Viewpoint") {
        elements[i].style.backgroundColor = "aqua";
      }
      if (elements[i].textContent === "Case Report") {
        elements[i].style.backgroundColor = "burlywood";
      } else if (elements[i].textContent === "Letter") {
        elements[i].style.backgroundColor = "cadetblue";
      }
    }
};

// used to remove elements from the DOM if they don't meet certain conditions.
const checkFilterEmpty=()=> {
      // An array of class selectors for elements that should be checked for content.
      var classes = [
        ".card-body .Topic",
        ".card-body .Author",
        ".card-body .Source",
        ".card-body .Year",
        ".card-body .Tag",
        ".card-body .description",
      ];
      for (var i = 0; i < classes.length; i++) {
        var elements = document.querySelectorAll(classes[i]);
        for (var j = 0; j < elements.length; j++) {
          if (elements[j].textContent.trim().length <= 0) {
            elements[j].remove();
          }
        }
      }
  
      var links = document.querySelectorAll(".card-body .Link");
      for (var k = 0; k < links.length; k++) {
        if (links[k].href.length === 42) {
          // If the length of the link's href attribute is equal to 42, remove the link from the DOM.
          links[k].remove();
          //console.log('link rimosso');
        } else {
          //console.log('link ok');
        }
      }
};

//Function to filter data
const filterData = (data,filters) =>{
return _.filter(data, (item) => {
  // Check if each filter is empty or if the current item matches the criteria
  return (
    (_.isEmpty(filters.topic) || _.includes(filters.topic, item.topic)) &&
    (_.isEmpty(filters.author) ||
      _.includes(filters.author, item.author)) &&
    (_.isEmpty(filters.source) ||
      _.includes(filters.source, item.source)) &&
    (_.isEmpty(filters.year) || _.includes(filters.year, item.year)) &&
    (_.isEmpty(filters.typology) ||
      _.includes(filters.typology, item.typology)) &&
    (_.isEmpty(filters.Tag) || _.includes(filters.Tag, item.Tag))
  );
})};

// Function to display  data
const displayData = (filteredData) => {
  let html = "";
  // Loop through filtered data
  filteredData.forEach((item) => {
    // Build HTML for each item
    html += `
    <div class="cardSFR">
      <div class="card-header" id="heading-${item.typology.replace(/\s+/g, '')}-${item.date.replace(/\//g, '-')}">
          <button class="btn btn-link collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse-${item.typology.replace(/\s+/g, '')}-${item.date.replace(/\//g, '-')}" aria-expanded="true" aria-controls="collapse-${item.typology.replace(/\s+/g, '')}-${item.date.replace(/\//g, '-')}">
            <span class="typology">${item.typology}</span>
            <span class="description">${item.description}</span>
          </button>
          <span class="date">${item.date}</span>
      </div>
      <div id="collapse-${item.typology.replace(/\s+/g, '')}-${item.date.replace(/\//g, '-')}" class="collapse" aria-labelledby="heading-${item.typology.replace(/\s+/g, '')}-${item.date.replace(/\//g, '-')}" data-bs-parent="#results">
        <div class="card-body">
          <div class="meta-data">
            <div class="meta-data-item Topic">TOPIC: ${item.topic}</div>
            <div class="meta-data-item Author">AUTHORS: ${item.author}</div>
            <div class="meta-data-item Source">SOURCE: ${item.source}</div>
            <div class="meta-data-item Tag">${item.tag}</div>
          </div>
          <div class="description">
          ABSTRACT:<br>
            ${item.description}
          </div>
          <div class="containerLink">
            <a class="Link" href="${item.link}" target="_blank">Read more</a>
          </div>
        </div>
      </div>
    </div>
  `;
  });
  document.getElementById("results").innerHTML = html;
  changeTypologyColor();
  checkFilterEmpty();

};

const getFilters = () => {

  const filters = {
    topic: [],
    author: [],
    source: [],
    year: [],
    typology: [],
    tag: [],
  };

// Removes 'filter-selected' class from all elements with class 'filter-selected'
document
.querySelectorAll(".filter-selected")
.forEach((el) => el.classList.remove("filter-selected"));
// Adds 'filter-selected' class to the parent of all checked checkboxes
[...document.querySelectorAll("input[type='checkbox']:checked")].forEach(
(el) => el.parentNode.classList.add("filter-selected")
);

// Filters the data based on selected topics, authors, sources, years, typologies, and tags
filters.topic = [
...document.querySelectorAll(
  "#topic-filter input[type='checkbox']:checked"
),
].map((x) => x.value);
filters.author = [
...document.querySelectorAll(
  "#author-filter input[type='checkbox']:checked"
),
].map((x) => x.value);
filters.source = [
...document.querySelectorAll(
  "#source-filter input[type='checkbox']:checked"
),
].map((x) => x.value);
filters.year = [
...document.querySelectorAll(
  "#year-filter input[type='checkbox']:checked"
),
].map((x) => x.value);
filters.typology = [
...document.querySelectorAll(
  "#typology-filter input[type='checkbox']:checked"
),
].map((x) => x.value);
filters.tag = [
...document.querySelectorAll(
  "#tag-filter input[type='checkbox']:checked"
),
].map((x) => x.value);

return filters;
};

//function to search
function searchFilt()  { 
  const filteredData = filterData(getData(), getFilters()); // Get the filtered data from the filterData function
  displayData(filteredData); // Displays the filtered data using the displayData function
  changeTypologyColor(); // Calls the changeTypologyColor function
  checkFilterEmpty();
}


const init = ()=> {

  copyDataFromJson();
  let data = getData();

  
  // Extract unique topics, authors, sources, years, typologies, and tags
  //from data and generate checkbox options for each category
  const uniqueTopics = _.uniqBy(data, "topic");
  let options = "";
  uniqueTopics.forEach((topic) => {
    options += `<label><input type="checkbox" value="${topic.topic}" onchange="searchFilt()">${topic.topic}</label>`;
  });
  document.getElementById("topic-filter").innerHTML = options;

  const uniqueAuthors = _.uniqBy(data, "author");
  options = "";
  uniqueAuthors.forEach((author) => {
    options += `<label><input type="checkbox" value="${author.author}" onchange="searchFilt()">${author.author}</label>`;
  });
  document.getElementById("author-filter").innerHTML = options;

  const uniqueSources = _.uniqBy(data, "source");
  options = "";
  uniqueSources.forEach((source) => {
    options += `<label><input type="checkbox" value="${source.source}" onchange="searchFilt()">${source.source}</label>`;
  });
  document.getElementById("source-filter").innerHTML = options;

  const uniqueYears = _.uniqBy(data, "year");
  options = "";
  uniqueYears.forEach((year) => {
    options += `<label><input type="checkbox" value="${year.year}" onchange="searchFilt()">${year.year}</label>`;
  });
  document.getElementById("year-filter").innerHTML = options;

  const uniqueTypologys = _.uniqBy(data, "typology");
  options = "";
  uniqueTypologys.forEach((typology) => {
    options += `<label><input type="checkbox" value="${typology.typology}" onchange="searchFilt()">${typology.typology}</label>`;
  });
  document.getElementById("typology-filter").innerHTML = options;

  const uniqueTags = _.uniqBy(data, "tag");
  options = "";
  uniqueTags.forEach((tag) => {
    options += `<label><input type="checkbox" value="${tag.tag}" onchange="searchFilt()">${tag.tag}</label>`;
  });
  document.getElementById("tag-filter").innerHTML = options;

  displayData(data); // Display the data



};

window.searchFilt = searchFilt;
window.onload = function () {
  // Select the carousel
  const results = document.getElementById("results");
  if (results) {
    init();
  } else {
    console.log("noSearchFilter");
  }
}