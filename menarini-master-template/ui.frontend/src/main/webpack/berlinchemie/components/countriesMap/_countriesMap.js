/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable max-len */

let map;
let domainName;
let port;
let protocol;

document.addEventListener("DOMContentLoaded", function() {
  var mapContainer = document.getElementById("mapWithFilterView");
  if (mapContainer) {
    initMap();
  } else {
        console.log("No mapWithFilterView");
      }

  var checkboxes = document.querySelectorAll('input[type="checkbox"]');
  checkboxes.forEach(function(checkbox) {
    checkbox.addEventListener('change', function() {
      setMarks(map.getZoom(), GetFilters());
    });
  });
});

function initMap() {
    map = new google.maps.Map(document.getElementById("mapWithFilterView"), {
        center: {
            lat: 33.37754075,
            lng: 54.4043076
        },
        zoom: 3,
    });
    google.maps.event.addListener(map, 'zoom_changed', function() {
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
    const lang = document.documentElement.getAttribute('lang');

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
              item.Icon = "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-ds.svg";
              break;
            case "LO":
              item.Icon = "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-lo.svg";
              break;
            case "ML":
              item.Icon = "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-ml.svg";
              break;
            case "MS":
              item.Icon = "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-ms.svg";
              break;
            case "RC":
              item.Icon = "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-rc.svg";
              break;
            default:
              // Set a default icon URL if the Type doesn't match any specific case
              item.Icon = "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg";
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

if (AllVenues.length === 0) {
  copyDataFromJson();
}


let AllContinents = [
    {
        "Continent": "Europe",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 48.341646,
            lng: 11.014086
        }
    },
    {
        "Continent": "Asia",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 54.977614,
            lng: 79.842848
        }
    },
    {
        "Continent": "Africa",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 8.407168,
            lng: 24.876509
        }
    },
    {
        "Continent": "North-America",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 44.087585,
            lng: -100.363308
        }
    },
    {
        "Continent": "South-America",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: -17.978733,
            lng: -53.926073
        }
    },
    {
        "Continent": "Oceania",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: -29.53523,
            lng: 137.451626
        }
    }
];
let AllCountries = [
    {
        "Continent": "Europe",
        "Country": "Albania",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 41.3275489807129,
            lng: 19.8187007904053
        }
    },
    {
        "Continent": "Europe",
        "Country": "Armenia",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 40.1791801452637,
            lng: 44.4990997314453
        }
    },
    {
        "Continent": "Europe",
        "Country": "Azerbaijan",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 40.1431007385254,
            lng: 47.5769309997559
        }
    },
    {
        "Continent": "Europe",
        "Country": "Bulgaria",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 42.6977081298828,
            lng: 23.321870803833
        }
    },
    {
        "Continent": "Europe",
        "Country": "Bosnia and Herzegovina",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 43.856258392334,
            lng: 18.4130802154541
        }
    },
    {
        "Continent": "Europe",
        "Country": "Belarus",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 53.904541015625,
            lng: 27.5615291595459
        }
    },
    {
        "Continent": "Europe",
        "Country": "Czech Republic",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 50.0755386352539,
            lng: 14.4378004074097
        }
    },
    {
        "Continent": "Europe",
        "Country": "Germany",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 52.5200119018555,
            lng: 13.4049501419067
        }
    },
    {
        "Continent": "Europe",
        "Country": "Estonia",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 59.4369583129883,
            lng: 24.7535705566406
        }
    },
    {
        "Continent": "Europe",
        "Country": "Finland",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 60.1698608398438,
            lng: 24.9383792877197
        }
    },
    {
        "Continent": "Europe",
        "Country": "Georgia",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 41.7151412963867,
            lng: 44.8270988464355
        }
    },
    {
        "Continent": "Europe",
        "Country": "Croatia",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 45.8150100708008,
            lng: 15.9819202423096
        }
    },
    {
        "Continent": "Europe",
        "Country": "Hungary",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 47.4979095458984,
            lng: 19.0402393341064
        }
    },
    {
        "Continent": "Asia",
        "Country": "Kazakhstan",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 48.0195693969727,
            lng: 66.9236831665039
        }
    },
    {
        "Continent": "Asia",
        "Country": "Kyrgyzstan",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 42.8746185302734,
            lng: 74.5697631835938
        }
    },
    {
        "Continent": "Europe",
        "Country": "Lithuania",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 54.6871604919434,
            lng: 25.2796497344971
        }
    },
    {
        "Continent": "Europe",
        "Country": "Latvia",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 56.949649810791,
            lng: 24.1051902770996
        }
    },
    {
        "Continent": "Europe",
        "Country": "Moldova",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 47.0104484558105,
            lng: 28.8638095855713
        }
    },
    {
        "Continent": "Europe",
        "Country": "Montenegro",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 42.430419921875,
            lng: 19.259370803833
        }
    },
    {
        "Continent": "Asia",
        "Country": "Mongolia",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 47.8863983154297,
            lng: 106.905700683594
        }
    },
    {
        "Continent": "Europe",
        "Country": "Poland",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 52.229679107666,
            lng: 21.0122299194336
        }
    },
    {
        "Continent": "Europe",
        "Country": "Romania",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 44.4267692565918,
            lng: 26.1025390625
        }
    },
    {
        "Continent": "Asia",
        "Country": "Russia",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 55.7558288574219,
            lng: 37.6172981262207
        }
    },
    {
        "Continent": "Europe",
        "Country": "Serbia",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 44.7865715026855,
            lng: 20.4489192962646
        }
    },
    {
        "Continent": "Europe",
        "Country": "Slovakia",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 48.163332397922,
            lng: 17.1775930116079
        }
    },
    {
        "Continent": "Europe",
        "Country": "Slovenia",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 46.0569496154785,
            lng: 14.5057497024536
        }
    },
    {
        "Continent": "Asia",
        "Country": "Turkmenistan",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 37.9600791931152,
            lng: 58.3260612487793
        }
    },
    {
        "Continent": "Europe",
        "Country": "Ukraine",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 50.4500999450684,
            lng: 30.5233993530273
        }
    },
    {
        "Continent": "Asia",
        "Country": "Uzbekistan",
        "Name": "",
        "Type": "all",
        "Icon": "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/images/icons/marker-all.svg",
        "Coord": {
            lat: 41.2994995117188,
            lng: 69.2400665283203
        }
    }
];


console.log('AllContinents:' + AllContinents.length);
console.log('AllCountries:' + AllCountries.length);
console.log('AllVenues:' + AllVenues.length);

let markersS = [];
let infoWindows = [];
var globalInfoWindows = null;
var countrySelected = null;
var countrySelectedId = null;

function resetMarkers() {
    markersS.forEach(function(marker) {
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

        if ((filtri.length == 0 || filtri.includes(collection[i].Type)) && collection[i].Continent == code) {
            total++;
        }
    }
    return total;
}

function GetTotalByTypeCountry(collection, filtri, code) {
    let total = 0;
    for (var i = 0; i < collection.length; i++) {

        if ((filtri.length == 0 || filtri.includes(collection[i].Type)) && collection[i].Country == code) {
            total++;
        }
    }
    return total;
}

function GetFilters() {
  let filtri = [];
  let checkboxes = document.querySelectorAll('input[type="checkbox"]');
  checkboxes.forEach(function(checkbox) {
    if (checkbox.checked) {
      filtri.push(checkbox.id.replace("check", ""));
    }
  });

  console.log("Filtri: " + filtri);
  console.log("Zoom: " + map.getZoom());
  return filtri;
}

let CONSTANT_CONTINENT = 3;
let CONSTANT_COUNTRY = 5;

function setContinentMarkers(zoomLevel, filters) {
    if (zoomLevel <= CONSTANT_CONTINENT) {
        console.log('setContinentMarkers');
        for (var i = 0; i < AllContinents.length; i++) {
            let numberOfChildren = GetTotalByTypeContinent(AllVenues, filters, AllContinents[i].Continent);
            if (numberOfChildren > 0) {
                markersS.push(new google.maps.Marker({
                    map: map,
                    icon: AllContinents[i].Icon,
                    title: AllContinents[i].Name,
                    label: {
                        text: numberOfChildren.toString(),
                        color: 'white'
                    },
                    position: AllContinents[i].Coord,
                    animation: google.maps.Animation.DROP
                }));
            }
        }
    }
}

function setCountriesMarkers(zoomLevel, filters) {
    if (zoomLevel > CONSTANT_CONTINENT && zoomLevel <= CONSTANT_COUNTRY) {
        console.log('setCountriesMarkers');
        for (var i = 0; i < AllCountries.length; i++) {

            if (countrySelected != null && countrySelected != '' && AllCountries[i].Name != countrySelected)
                continue;

            let numberOfChildren = GetTotalByTypeCountry(AllVenues, filters, AllCountries[i].Country);
            if (numberOfChildren > 0) {
                markersS.push(new google.maps.Marker({
                    map: map,
                    icon: AllCountries[i].Icon,
                    title: AllCountries[i].Name,
                    label: {
                        text: numberOfChildren.toString(),
                        color: 'white'
                    },
                    position: AllCountries[i].Coord,
                    animation: google.maps.Animation.DROP
                }));
            }
        }
    }
}

function setVenuesMarkers(zoomLevel, filters) {
    if (zoomLevel > CONSTANT_COUNTRY) {
        console.log('setVenuesMarkers');
        var template = '<div class="picker"><h2 class="companyName">{NAME}</h2><p class="cityName">{CITY}</p><p class="websiteName"><a href="{LINK}" target="_blank">{WEB}</a></p></div>';
        for (var i = 0; i < AllVenues.length; i++) {

            console.log('countrySelected:' + countrySelectedId);
            if (countrySelectedId != null && countrySelectedId != '' && AllVenues[i].Country != countrySelectedId)
                continue;

            if (filters.length == 0 || filters.includes(AllVenues[i].Type)) {

                if (globalInfoWindows == null)
                    globalInfoWindows = new google.maps.InfoWindow({
                        content: ''
                    });

                var html = template
                    .replace('{NAME}', AllVenues[i].Name)
                    .replace('{CITY}', AllVenues[i].City)
                    .replace('{LINK}', AllVenues[i].Link)
                    .replace('{WEB}', AllVenues[i].Link);

                infoWindows[i] = new google.maps.InfoWindow({
                    content: html
                });

                var marker = new google.maps.Marker({
                    map: map,
                    icon: AllVenues[i].Icon,
                    title: AllVenues[i].Name,
                    label: {
                        text: '1',
                        color: 'white'
                    },
                    position: AllVenues[i].Coord,
                    animation: google.maps.Animation.DROP
                });
                google.maps.event.addListener(marker, 'click', (function(marker, i) {
                    return function() {
                        globalInfoWindows.close();
                        globalInfoWindows.setContent(infoWindows[i].content);
                        globalInfoWindows.open(map, marker);
                    }
                })(marker, i));
                markersS.push(marker);
            }
        }
    }
}

function UpdateListOfVenues(filters) {
    if (typeof ASPxCallbackPanelResultAfterFiltering !== 'undefined') {
        ASPxCallbackPanelResultAfterFiltering.PerformCallback(filters.join(','));
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
    if (name == null || name == '') {
        map.setCenter({
            lat: 22.7592994,
            lng: 10.7933395
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
    var inputValue = document.getElementById('country_search');
    inputValue.value = '';
}

function countrySelectedChangedByAnchor() {
    countrySelectedChanged(ASPxComboBoxCountrySearch.GetText());
}

function ASPxComboBoxCountrySearch_SelectedIndexChanged(s, e) {
    e.processOnServer = false;
    countrySelectedChanged(s.GetText());
}

let CONSTANT_ZOOM_VENUE = 20;

function setFocusOnMapByVenueId(id) {
    if (id == null || id == '') {
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