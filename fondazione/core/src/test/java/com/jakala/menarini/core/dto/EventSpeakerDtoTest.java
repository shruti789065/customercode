package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class EventSpeakerDtoTest {

    @Test
    void testId() {
        EventSpeakerDto dto = new EventSpeakerDto();
        long id = 1L;
        dto.setId(id);
        assertEquals(id, dto.getId());
    }

    @Test
    void testCreatedOn() {
        EventSpeakerDto dto = new EventSpeakerDto();
        Timestamp createdOn = new Timestamp(System.currentTimeMillis());
        dto.setCreatedOn(createdOn);
        assertEquals(createdOn, dto.getCreatedOn());
    }

    @Test
    void testLastUpdatedOn() {
        EventSpeakerDto dto = new EventSpeakerDto();
        Timestamp lastUpdatedOn = new Timestamp(System.currentTimeMillis());
        dto.setLastUpdatedOn(lastUpdatedOn);
        assertEquals(lastUpdatedOn, dto.getLastUpdatedOn());
    }

    @Test
    void testEvent() {
        EventSpeakerDto dto = new EventSpeakerDto();
        EventDto event = new EventDto();
        dto.setEvent(event);
        assertEquals(event, dto.getEvent());
    }

    @Test
    void testSpeaker() {
        EventSpeakerDto dto = new EventSpeakerDto();
        SpeakerDto speaker = new SpeakerDto();
        dto.setSpeaker(speaker);
        assertEquals(speaker, dto.getSpeaker());
    }
}