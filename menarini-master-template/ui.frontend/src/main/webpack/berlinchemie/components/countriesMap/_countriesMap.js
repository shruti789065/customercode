console.log('countriesMap');

    let map;

    let AllContinents = [<%= AllContinentsScripts  %>];
    let AllCountries = [<%= AllCountriesScripts  %>];
    let AllVenues = [<%= AllVenuesScripts  %>];

    console.log('AllContinents:' + AllContinents.length);
    console.log('AllCountries:' + AllCountries.length);
    console.log('AllVenues:' + AllVenues.length);

    let markersS = [];
    let infoWindows = [];
    var globalInfoWindows = null;
    var countrySelected = null;
    var countrySelectedId = null;

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
        let ds = document.getElementById("checkDS");
        if (ds.checked) {
            filtri.push("DS");
        }
        let lo = document.getElementById("checkLO");
        if (lo.checked) {
            filtri.push("LO");
        }
        let ml = document.getElementById("checkML");
        if (ml.checked) {
            filtri.push("ML");
        }
        let ms = document.getElementById("checkMS");
        if (ms.checked) {
            filtri.push("MS");
        }
        let rc = document.getElementById("checkRC");
        if (rc.checked) {
            filtri.push("RC");
        }
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
                        globalInfoWindows = new google.maps.InfoWindow({ content: '' });

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
                    google.maps.event.addListener(marker, 'click', (function (marker, i) {
                        return function () {
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
            map.setCenter({ lat: 22.7592994, lng: 10.7933395 }); //rimette al centro
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
