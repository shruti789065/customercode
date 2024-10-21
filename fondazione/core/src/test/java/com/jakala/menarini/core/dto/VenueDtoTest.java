package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VenueDtoTest {

    @Test
    void testGettersAndSetters() {
        VenueDto dto = new VenueDto();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dto.setId(1L);
        dto.setCreatedOn(timestamp);
        dto.setDescription("Description");
        dto.setLastUpdatedOn(timestamp);
        dto.setName("Venue Name");
        dto.setNotes("Notes");
        List<EventDto> events = new ArrayList<>();
        dto.setEvents(events);
        List<EventSessionDateDto> eventSessionDates = new ArrayList<>();
        dto.setEventSessionDates(eventSessionDates);
        LocationDto location = new LocationDto();
        dto.setLocation(location);

        assertEquals(1L, dto.getId());
        assertEquals(timestamp, dto.getCreatedOn());
        assertEquals("Description", dto.getDescription());
        assertEquals(timestamp, dto.getLastUpdatedOn());
        assertEquals("Venue Name", dto.getName());
        assertEquals("Notes", dto.getNotes());
        assertEquals(events, dto.getEvents());
        assertEquals(eventSessionDates, dto.getEventSessionDates());
        assertEquals(location, dto.getLocation());
    }

    @Test
    void testAddAndRemoveEvent() {
        VenueDto dto = new VenueDto();
        EventDto event = new EventDto();
        List<EventDto> events = new ArrayList<>();
        dto.setEvents(events);

        dto.addEvent(event);
        assertEquals(1, dto.getEvents().size());
        assertEquals(dto, event.getVenue());

        dto.removeEvent(event);
        assertEquals(0, dto.getEvents().size());
        assertNull(event.getVenue());
    }

    @Test
    void testAddAndRemoveEventSessionDate() {
        VenueDto dto = new VenueDto();
        EventSessionDateDto eventSessionDate = new EventSessionDateDto();
        List<EventSessionDateDto> eventSessionDates = new ArrayList<>();
        dto.setEventSessionDates(eventSessionDates);

        dto.addEventSessionDateDto(eventSessionDate);
        assertEquals(1, dto.getEventSessionDates().size());
        assertEquals(dto, eventSessionDate.getVenue());

        dto.removeEventSessionDate(eventSessionDate);
        assertEquals(0, dto.getEventSessionDates().size());
        assertNull(eventSessionDate.getVenue());
    }
}