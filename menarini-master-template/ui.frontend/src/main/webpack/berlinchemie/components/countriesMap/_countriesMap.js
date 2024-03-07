/* eslint-disable @typescript-eslint/no-unused-vars */
import { AllContinents } from "./continents.js";
import { AllCountries } from "./countries.js";
import { getUrl } from "../../../mastertemplate/site/_util";

const BASE_ICON_URL =
  "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/";
const JSONmock =
  "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/mock/AllVenues.json";
const ZOOM = {
  DEFAULT: 3,
  CONTINENT_THRESHOLD: 3,
  COUNTRY_THRESHOLD: 5,
  VENUE: 20,
};

let map;
let AllVenues = JSON.parse(localStorage.getItem("mapResults")) || [];
let markersS = [];
let infoWindows = [];
let globalInfoWindows = null;
let countrySelected = null;
let countrySelectedId = null;

document.addEventListener("DOMContentLoaded", () => {
  const mapContainer = document.getElementById("mapWithFilterView");
  if (mapContainer) {
    initMap();
  } else {
    console.log("No mapWithFilterView");
  }

  const checkboxes = document.querySelectorAll('input[type="checkbox"]');
  checkboxes.forEach((checkbox) => {
    checkbox.addEventListener("change", () => {
      setMarks(map.getZoom(), getFilters());
    });
  });
});

function initMap() {
  if (AllVenues.length === 0) {
    copyDataFromJson();
  }
  map = new google.maps.Map(document.getElementById("mapWithFilterView"), {
    center: { lat: 33.37754075, lng: 54.4043076 },
    zoom: ZOOM.DEFAULT,
  });
  google.maps.event.addListener(map, "zoom_changed", () => {
    setMarks(map.getZoom(), getFilters());
  });
  setMarks(map.getZoom(), getFilters());
}

function copyDataFromJson() {
  const lang = document.documentElement.getAttribute("lang");
  const graphqlQuery = `/graphql/execute.json/global/locale;locale=${lang}`;
  fetch(getUrl(graphqlQuery, JSONmock))
    .then((response) => response.json())
    .then((data) => {
      AllVenues = data.data.venuesList.items.map((item) => ({
        ...item,
        Coord: {
          lat: parseFloat(item.Coord.lat),
          lng: parseFloat(item.Coord.lng),
        },
        Icon: getIconUrl(item.Type),
      }));
      localStorage.setItem("mapResults", JSON.stringify(AllVenues));
    })
    .catch((error) => {
      console.error("Error copying data to local storage:", error);
    });
}

function getIconUrl(type) {
  const defaultIcon = `${BASE_ICON_URL}marker-all.svg`;
  const iconMap = {
    DS: "marker-ds.svg",
    LO: "marker-lo.svg",
    ML: "marker-ml.svg",
    MS: "marker-ms.svg",
    RC: "marker-rc.svg",
    LS: "marker-ls.svg",
  };
  return BASE_ICON_URL + (iconMap[type] || defaultIcon);
}

function resetMarkers() {
  markersS.forEach((marker) => marker.setMap(null));
  markersS = [];
  infoWindows = [];
}

function setMarks(zoomLevel, filters) {
  resetMarkers();
  if (zoomLevel <= ZOOM.CONTINENT_THRESHOLD) {
    setContinentMarkers(filters);
  } else if (
    zoomLevel > ZOOM.CONTINENT_THRESHOLD &&
    zoomLevel <= ZOOM.COUNTRY_THRESHOLD
  ) {
    setCountriesMarkers(filters);
  } else {
    setVenuesMarkers(filters);
  }
  UpdateListOfVenues(filters);
}

function getFilters() {
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

function setContinentMarkers(filters) {
  AllContinents.forEach((continent) => {
    const numberOfChildren = AllVenues.filter(
      (venue) =>
        venue.Continent === continent.Continent &&
        (filters.length === 0 || filters.includes(venue.Type))
    ).length;
    if (numberOfChildren > 0) {
      markersS.push(
        new google.maps.Marker({
          map: map,
          icon: continent.Icon,
          title: continent.Name,
          label: { text: numberOfChildren.toString(), color: "white" },
          position: continent.Coord,
          animation: google.maps.Animation.DROP,
        })
      );
    }
  });
}

function setCountriesMarkers(filters) {
  AllCountries.forEach((country) => {
    if (
      countrySelected != null &&
      countrySelected != "" &&
      country.Name !== countrySelected
    )
      {return;}
    const numberOfChildren = AllVenues.filter(
      (venue) =>
        venue.Country === country.Country &&
        (filters.length === 0 || filters.includes(venue.Type))
    ).length;
    if (numberOfChildren > 0) {
      markersS.push(
        new google.maps.Marker({
          map: map,
          icon: country.Icon,
          title: country.Name,
          label: { text: numberOfChildren.toString(), color: "white" },
          position: country.Coord,
          animation: google.maps.Animation.DROP,
        })
      );
    }
  });
}

function setVenuesMarkers(filters) {
  AllVenues.forEach((venue, index) => {
    if (
      countrySelectedId != null &&
      countrySelectedId != "" &&
      venue.Country !== countrySelectedId
    )
      {return;}
    if (filters.length === 0 || filters.includes(venue.Type)) {
      const template = `<div class="picker"><h2 class="cityName">${venue.City}</h2>
	  <p class="companyName">${venue.Name}</p>
	  <p class="websiteName"><a href="${venue.Link}" target="_blank">${venue.Link}</a></p></div>`;
      const html = template
        .replace("{NAME}", venue.Name)
        .replace("{CITY}", venue.City)
        .replace("{LINK}", venue.Link)
        .replace("{WEB}", venue.Link);
      infoWindows[index] = new google.maps.InfoWindow({ content: html });
      const marker = new google.maps.Marker({
        map: map,
        icon: venue.Icon,
        title: venue.Name,
        label: { text: "1", color: "white" },
        position: venue.Coord,
        animation: google.maps.Animation.DROP,
      });
      google.maps.event.addListener(marker, "click", () => {
        globalInfoWindows && globalInfoWindows.close();
        globalInfoWindows &&
          globalInfoWindows.setContent(infoWindows[index].content);
        globalInfoWindows && globalInfoWindows.open(map, marker);
      });
      markersS.push(marker);
    }
  });
}

function UpdateListOfVenues(filters) {
  if (typeof ASPxCallbackPanelResultAfterFiltering !== "undefined") {
    ASPxCallbackPanelResultAfterFiltering.PerformCallback(filters.join(","));
  }
}

function countrySelectedChanged(name) {
  console.log(name);
  countrySelected = null;
  countrySelectedId = null;
  if (name == null || name == "") {
    map.setCenter({ lat: 22.7592994, lng: 10.7933395 });
    map.setZoom(2);
    setMarks(2, []);
    return;
  }
  const country = AllCountries.find((c) => c.Name === name);
  if (country) {
    countrySelected = country.Name;
    countrySelectedId = country.Id;
    map.setCenter(country.Coord);
    map.setZoom(ZOOM.COUNTRY_THRESHOLD);
    setMarks(ZOOM.COUNTRY_THRESHOLD, []);
  }
  const inputValue = document.getElementById("country_search");
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
  setVenuesMarkers(ZOOM.VENUE, []);
  const venue = AllVenues.find((v) => v.Id === id);
  if (venue) {
    map.setCenter(venue.Coord);
    map.setZoom(ZOOM.VENUE);
  }
}
