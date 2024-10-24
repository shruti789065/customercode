package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocationDtoTest {

    @Test
    void testGettersAndSetters() {
        LocationDto dto = new LocationDto();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dto.setId(1L);
        dto.setCity("City");
        dto.setCountry("Country");
        dto.setCreatedOn(timestamp);
        dto.setLastUpdatedOn(timestamp);
        dto.setNotes("Notes");
        List<EventDto> events = new ArrayList<>();
        dto.setEvents(events);
        List<EventSessionDateDto> eventSessionDates = new ArrayList<>();
        dto.setEventSessionDates(eventSessionDates);
        List<VenueDto> venues = new ArrayList<>();
        dto.setVenues(venues);

        assertEquals(1L, dto.getId());
        assertEquals("City", dto.getCity());
        assertEquals("Country", dto.getCountry());
        assertEquals(timestamp, dto.getCreatedOn());
        assertEquals(timestamp, dto.getLastUpdatedOn());
        assertEquals("Notes", dto.getNotes());
        assertEquals(events, dto.getEvents());
        assertEquals(eventSessionDates, dto.getEventSessionDates());
        assertEquals(venues, dto.getVenues());
    }

    @Test
    void testAddAndRemoveEvent() {
        LocationDto dto = new LocationDto();
        EventDto event = new EventDto();
        List<EventDto> events = new ArrayList<>();
        dto.setEvents(events);

        dto.addEvent(event);
        assertEquals(1, dto.getEvents().size());
        assertEquals(dto, event.getLocation());

        dto.removeEvent(event);
        assertEquals(0, dto.getEvents().size());
        assertNull(event.getLocation());
    }

    @Test
    void testAddAndRemoveEventSessionDate() {
        LocationDto dto = new LocationDto();
        EventSessionDateDto eventSessionDate = new EventSessionDateDto();
        List<EventSessionDateDto> eventSessionDates = new ArrayList<>();
        dto.setEventSessionDates(eventSessionDates);

        dto.addEventSessionDate(eventSessionDate);
        assertEquals(1, dto.getEventSessionDates().size());
        assertEquals(dto, eventSessionDate.getLocation());

        dto.removeEventSessionDate(eventSessionDate);
        assertEquals(0, dto.getEventSessionDates().size());
        assertNull(eventSessionDate.getLocation());
    }

    @Test
    void testAddAndRemoveVenue() {
        LocationDto dto = new LocationDto();
        VenueDto venue = new VenueDto();
        List<VenueDto> venues = new ArrayList<>();
        dto.setVenues(venues);

        dto.addVenue(venue);
        assertEquals(1, dto.getVenues().size());
        assertEquals(dto, venue.getLocation());

        dto.removeVenue(venue);
        assertEquals(0, dto.getVenues().size());
        assertNull(venue.getLocation());
    }
}