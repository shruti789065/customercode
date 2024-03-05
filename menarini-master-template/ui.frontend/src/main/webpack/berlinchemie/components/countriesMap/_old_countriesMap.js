/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable max-len */
/*
import { AllContinents } from "./continents.js";
import { AllCountries } from "./countries.js";

let map;
let domainName;
let port;
let protocol;
let CONSTANT_CONTINENT = 3;
let CONSTANT_COUNTRY = 5;
let CONSTANT_ZOOM_VENUE = 20;

document.addEventListener("DOMContentLoaded", function () {
  var mapContainer = document.getElementById("mapWithFilterView");
  if (mapContainer) {
    initMap();
  } else {
    console.log("No mapWithFilterView");
  }

  var checkboxes = document.querySelectorAll('input[type="checkbox"]');
  checkboxes.forEach(function (checkbox) {
    checkbox.addEventListener("change", function () {
      setMarks(map.getZoom(), GetFilters());
    });
  });
});

function initMap() {
  map = new google.maps.Map(document.getElementById("mapWithFilterView"), {
    center: {
      lat: 33.37754075,
      lng: 54.4043076,
    },
    zoom: 3,
  });
  google.maps.event.addListener(map, "zoom_changed", function () {
    console.log("ZOOM: " + map.getZoom());
    setMarks(map.getZoom(), GetFilters());
  });
  setMarks(map.getZoom(), GetFilters());

  domainName = window.location.hostname;
  port = window.location.port;
  protocol = window.location.protocol;

  copyDataFromJson();
}

function copyDataFromJson() {
  const lang = document.documentElement.getAttribute("lang");

  const url =
    domainName === "localhost" && port === "4502"
      ? `${protocol}//${domainName}:${port}/graphql/execute.json/global/locale;locale=${lang}`
      : domainName === "localhost"
      ? "https://raw.githubusercontent.com/davide-mariotti/JSON/main/countriesMap/AllVenues.json"
      : `${protocol}//${domainName}/graphql/execute.json/global/locale;locale=${lang}`;

  fetch(url)
    .then((response) => response.json())
    .then((data) => {
      const items = data.data.venuesList.items;

      items.forEach((item) => {
        item.Coord.lat = parseFloat(item.Coord.lat);
        item.Coord.lng = parseFloat(item.Coord.lng);

        switch (item.Type) {
          case "DS":
            item.Icon =
              "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-ds.svg";
            break;
          case "LO":
            item.Icon =
              "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-lo.svg";
            break;
          case "ML":
            item.Icon =
              "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-ml.svg";
            break;
          case "MS":
            item.Icon =
              "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-ms.svg";
            break;
          case "RC":
            item.Icon =
              "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-rc.svg";
            break;
          case "LS":
            item.Icon =
              "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-ls.svg";
            break;
          default:
            // Set a default icon URL if the Type doesn't match any specific case
            item.Icon =
              "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg";
            break;
        }
      });

      // Save to localStorage
      localStorage.setItem("mapResults", JSON.stringify(items));
    })
    .catch((error) => {
      console.error("Error copying data to local storage:", error);
    });
}

let AllVenues = JSON.parse(localStorage.getItem("mapResults")) || [];
let markersS = [];
let infoWindows = [];
var globalInfoWindows = null;
var countrySelected = null;
var countrySelectedId = null;

if (AllVenues.length === 0) {
  copyDataFromJson();
}

function resetMarkers() {
  markersS.forEach(function (marker) {
    marker.setMap(null);
  });
  markersS = [];
  infoWindows = [];
}

function setMarks(zoomLevel, filters) {
  resetMarkers();
  setContinentMarkers(zoomLevel, filters);
  setCountriesMarkers(zoomLevel, filters);
  setVenuesMarkers(zoomLevel, filters);
  UpdateListOfVenues(filters);
}

function GetTotalByType(collection, filtri) {
  let total = 0;
  for (var i = 0; i < collection.length; i++) {
    if (filtri.length == 0 || filtri.includes(collection[i].Type)) {
      total++;
    }
  }
  return total;
}

function GetTotalByTypeContinent(collection, filtri, code) {
  let total = 0;
  for (var i = 0; i < collection.length; i++) {
    if (
      (filtri.length == 0 || filtri.includes(collection[i].Type)) &&
      collection[i].Continent == code
    ) {
      total++;
    }
  }
  return total;
}

function GetTotalByTypeCountry(collection, filtri, code) {
  let total = 0;
  for (var i = 0; i < collection.length; i++) {
    if (
      (filtri.length == 0 || filtri.includes(collection[i].Type)) &&
      collection[i].Country == code
    ) {
      total++;
    }
  }
  return total;
}

function GetFilters() {
  let filtri = [];
  let checkboxes = document.querySelectorAll('input[type="checkbox"]');
  checkboxes.forEach(function (checkbox) {
    if (checkbox.checked) {
      filtri.push(checkbox.id.replace("check", ""));
    }
  });

  console.log("Filtri: " + filtri);
  console.log("Zoom: " + map.getZoom());
  return filtri;
}



function setContinentMarkers(zoomLevel, filters) {
  if (zoomLevel <= CONSTANT_CONTINENT) {
    console.log("setContinentMarkers");
    for (var i = 0; i < AllContinents.length; i++) {
      let numberOfChildren = GetTotalByTypeContinent(
        AllVenues,
        filters,
        AllContinents[i].Continent
      );
      if (numberOfChildren > 0) {
        markersS.push(
          new google.maps.Marker({
            map: map,
            icon: AllContinents[i].Icon,
            title: AllContinents[i].Name,
            label: {
              text: numberOfChildren.toString(),
              color: "white",
            },
            position: AllContinents[i].Coord,
            animation: google.maps.Animation.DROP,
          })
        );
      }
    }
  }
}

function setCountriesMarkers(zoomLevel, filters) {
  if (zoomLevel > CONSTANT_CONTINENT && zoomLevel <= CONSTANT_COUNTRY) {
    console.log("setCountriesMarkers");
    for (var i = 0; i < AllCountries.length; i++) {
      if (
        countrySelected != null &&
        countrySelected != "" &&
        AllCountries[i].Name != countrySelected
      )
        continue;

      let numberOfChildren = GetTotalByTypeCountry(
        AllVenues,
        filters,
        AllCountries[i].Country
      );
      if (numberOfChildren > 0) {
        markersS.push(
          new google.maps.Marker({
            map: map,
            icon: AllCountries[i].Icon,
            title: AllCountries[i].Name,
            label: {
              text: numberOfChildren.toString(),
              color: "white",
            },
            position: AllCountries[i].Coord,
            animation: google.maps.Animation.DROP,
          })
        );
      }
    }
  }
}

function setVenuesMarkers(zoomLevel, filters) {
  if (zoomLevel > CONSTANT_COUNTRY) {
    console.log("setVenuesMarkers");
    var template =
      '<div class="picker"><h2 class="cityName">{CITY}</h2><p class="companyName">{NAME}</p><p class="websiteName"><a href="{LINK}" target="_blank">{WEB}</a></p></div>';
    for (var i = 0; i < AllVenues.length; i++) {
      console.log("countrySelected:" + countrySelectedId);
      if (
        countrySelectedId != null &&
        countrySelectedId != "" &&
        AllVenues[i].Country != countrySelectedId
      )
        continue;

      if (filters.length == 0 || filters.includes(AllVenues[i].Type)) {
        if (globalInfoWindows == null)
          globalInfoWindows = new google.maps.InfoWindow({
            content: "",
          });

        var html = template
          .replace("{NAME}", AllVenues[i].Name)
          .replace("{CITY}", AllVenues[i].City)
          .replace("{LINK}", AllVenues[i].Link)
          .replace("{WEB}", AllVenues[i].Link);

        infoWindows[i] = new google.maps.InfoWindow({
          content: html,
        });

        var marker = new google.maps.Marker({
          map: map,
          icon: AllVenues[i].Icon,
          title: AllVenues[i].Name,
          label: {
            text: "1",
            color: "white",
          },
          position: AllVenues[i].Coord,
          animation: google.maps.Animation.DROP,
        });
        google.maps.event.addListener(
          marker,
          "click",
          (function (marker, i) {
            return function () {
              globalInfoWindows.close();
              globalInfoWindows.setContent(infoWindows[i].content);
              globalInfoWindows.open(map, marker);
            };
          })(marker, i)
        );
        markersS.push(marker);
      }
    }
  }
}

function UpdateListOfVenues(filters) {
  if (typeof ASPxCallbackPanelResultAfterFiltering !== "undefined") {
    ASPxCallbackPanelResultAfterFiltering.PerformCallback(filters.join(","));
  }
}

function ASPxCallbackPanelResultAfterFiltering_EndCallback(s, e) {
  ASPxCallbackPanelResultAfterFiltering.SetVisible(s.cp_ClientVisible);
}

function countrySelectedChanged(name) {
  console.log(name);

  countrySelected = null;
  countrySelectedId = null;

  let filters = [];
  if (name == null || name == "") {
    map.setCenter({
      lat: 22.7592994,
      lng: 10.7933395,
    }); //rimette al centro
    map.setZoom(2);
    setMarks(2, filters);
    return;
  }
  for (var i = 0; i < AllCountries.length; i++) {
    if (AllCountries[i].Name === name) {
      countrySelected = AllCountries[i].Name;
      countrySelectedId = AllCountries[i].Id;

      map.setCenter(AllCountries[i].Coord);
      map.setZoom(CONSTANT_COUNTRY);
      setMarks(CONSTANT_COUNTRY, filters);
      return;
    }
  }
  var inputValue = document.getElementById("country_search");
  inputValue.value = "";
}

function countrySelectedChangedByAnchor() {
  countrySelectedChanged(ASPxComboBoxCountrySearch.GetText());
}

function ASPxComboBoxCountrySearch_SelectedIndexChanged(s, e) {
  e.processOnServer = false;
  countrySelectedChanged(s.GetText());
}



function setFocusOnMapByVenueId(id) {
  if (id == null || id == "") {
    return;
  }
  setVenuesMarkers(CONSTANT_ZOOM_VENUE, GetFilters());
  for (var i = 0; i < AllVenues.length; i++) {
    if (AllVenues[i].Id === id) {
      map.setCenter(AllVenues[i].Coord);
      map.setZoom(CONSTANT_ZOOM_VENUE);
      return;
    }
  }
}
*/