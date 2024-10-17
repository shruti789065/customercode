package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class EventSessionDateDtoTest {

    @Test
    void testId() {
        EventSessionDateDto dto = new EventSessionDateDto();
        long id = 1L;
        dto.setId(id);
        assertEquals(id, dto.getId());
    }

    @Test
    void testCreatedOn() {
        EventSessionDateDto dto = new EventSessionDateDto();
        Timestamp createdOn = new Timestamp(System.currentTimeMillis());
        dto.setCreatedOn(createdOn);
        assertEquals(createdOn, dto.getCreatedOn());
    }

    @Test
    void testLastUpdatedOn() {
        EventSessionDateDto dto = new EventSessionDateDto();
        Timestamp lastUpdatedOn = new Timestamp(System.currentTimeMillis());
        dto.setLastUpdatedOn(lastUpdatedOn);
        assertEquals(lastUpdatedOn, dto.getLastUpdatedOn());
    }

    @Test
    void testSeqNo() {
        EventSessionDateDto dto = new EventSessionDateDto();
        int seqNo = 1;
        dto.setSeqNo(seqNo);
        assertEquals(seqNo, dto.getSeqNo());
    }

    @Test
    void testSession() {
        EventSessionDateDto dto = new EventSessionDateDto();
        String session = "Morning Session";
        dto.setSession(session);
        assertEquals(session, dto.getSession());
    }

    @Test
    void testSessionDate() {
        EventSessionDateDto dto = new EventSessionDateDto();
        Date sessionDate = new Date();
        dto.setSessionDate(sessionDate);
        assertEquals(sessionDate, dto.getSessionDate());
    }

    @Test
    void testEvent() {
        EventSessionDateDto dto = new EventSessionDateDto();
        EventDto event = new EventDto();
        dto.setEvent(event);
        assertEquals(event, dto.getEvent());
    }

    @Test
    void testLocation() {
        EventSessionDateDto dto = new EventSessionDateDto();
        LocationDto location = new LocationDto();
        dto.setLocation(location);
        assertEquals(location, dto.getLocation());
    }

    @Test
    void testVenue() {
        EventSessionDateDto dto = new EventSessionDateDto();
        VenueDto venue = new VenueDto();
        dto.setVenue(venue);
        assertEquals(venue, dto.getVenue());
    }
}