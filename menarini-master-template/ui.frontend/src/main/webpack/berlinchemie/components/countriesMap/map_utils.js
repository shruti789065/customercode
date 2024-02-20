/*
import { AllContinents } from "./continents.js";
import { AllCountries } from "./countries.js";

const CONSTANT = {
  CONTINENT: 3,
  COUNTRY: 5,
};

export const ZOOM = {
  DEFAULT: 3,
  VENUE: 20,
  CONTINENT_THRESHOLD: 3,
  COUNTRY_THRESHOLD: 5,
};

export let AllVenues = JSON.parse(localStorage.getItem("mapResults")) || [];
let markersS = [];
let infoWindows = [];
var globalInfoWindows = null;
var countrySelected = null;
var countrySelectedId = null;

function GetTotalByType(collection, filtri) {
  let total = 0;
  for (let i = 0; i < collection.length; i++) {
    if (filtri.length == 0 || filtri.includes(collection[i].Type)) {
      total++;
    }
  }
  return total;
}

function GetTotalByTypeCountry(collection, filtri, code) {
  let total = 0;
  for (let i = 0; i < collection.length; i++) {
    if (
      (filtri.length == 0 || filtri.includes(collection[i].Type)) &&
      collection[i].Country == code
    ) {
      total++;
    }
  }
  return total;
}

function setContinentMarkers(zoomLevel, filters) {
  if (zoomLevel <= CONSTANT.CONTINENT) {
    console.log("setContinentMarkers");
    for (let i = 0; i < AllContinents.length; i++) {
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
  if (zoomLevel > CONSTANT.CONTINENT && zoomLevel <= CONSTANT.COUNTRY) {
    console.log("setCountriesMarkers");
    for (let i = 0; i < AllCountries.length; i++) {
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
  if (zoomLevel > CONSTANT.COUNTRY) {
    console.log("setVenuesMarkers");
    let template =
      '<div class="picker"><h2 class="cityName">{CITY}</h2><p class="companyName">{NAME}</p><p class="websiteName"><a href="{LINK}" target="_blank">{WEB}</a></p></div>';
    for (let i = 0; i < AllVenues.length; i++) {
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

        let html = template
          .replace("{NAME}", AllVenues[i].Name)
          .replace("{CITY}", AllVenues[i].City)
          .replace("{LINK}", AllVenues[i].Link)
          .replace("{WEB}", AllVenues[i].Link);

        infoWindows[i] = new google.maps.InfoWindow({
          content: html,
        });

        let marker = new google.maps.Marker({
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
  for (let i = 0; i < AllCountries.length; i++) {
    if (AllCountries[i].Name === name) {
      countrySelected = AllCountries[i].Name;
      countrySelectedId = AllCountries[i].Id;

      map.setCenter(AllCountries[i].Coord);
      map.setZoom(CONSTANT.COUNTRY);
      setMarks(CONSTANT.COUNTRY, filters);
      return;
    }
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
  setVenuesMarkers(CONSTANT_ZOOM_VENUE, GetFilters());
  for (let i = 0; i < AllVenues.length; i++) {
    if (AllVenues[i].Id === id) {
      map.setCenter(AllVenues[i].Coord);
      map.setZoom(CONSTANT_ZOOM_VENUE);
      return;
    }
  }
}

function GetTotalByTypeContinent(collection, filtri, code) {
  let total = 0;
  for (let i = 0; i < collection.length; i++) {
    if (
      (filtri.length == 0 || filtri.includes(collection[i].Type)) &&
      collection[i].Continent == code
    ) {
      total++;
    }
  }
  return total;
}

function resetMarkers() {
  markersS.forEach((marker) => marker.setMap(null));
  markersS = [];
}

/*
	EXPORT ZONE

*/
/*
export function GetFilters() {
  let filtri = [];
  let checkboxes = document.querySelectorAll('input[type="checkbox"]');
  checkboxes.forEach(function (checkbox) {
    if (checkbox.checked) {
      filtri.push(checkbox.id.replace("check", ""));
    }
  });
  return filtri;
}


export function setMarks(zoomLevel, filters) {
  resetMarkers();
  if (zoomLevel <= ZOOM.CONTINENT_THRESHOLD) {
    setContinentMarkers(filters);
  } else if (zoomLevel <= ZOOM.COUNTRY_THRESHOLD) {
    setCountriesMarkers(filters);
  } else {
    setVenuesMarkers(filters);
  }
  UpdateListOfVenues(filters);
}
*/