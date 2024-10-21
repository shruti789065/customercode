package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpeakerDtoTest {

    @Test
    void testGettersAndSetters() {
        SpeakerDto dto = new SpeakerDto();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dto.setId(1L);
        dto.setBio("Bio");
        dto.setCreatedOn(timestamp);
        dto.setCurriculum("Curriculum");
        dto.setFirstname("First Name");
        dto.setLastUpdatedOn(timestamp);
        dto.setLastname("Last Name");
        List<EventSpeakerDto> eventSpeakers = new ArrayList<>();
        dto.setEventSpeakers(eventSpeakers);

        assertEquals(1L, dto.getId());
        assertEquals("Bio", dto.getBio());
        assertEquals(timestamp, dto.getCreatedOn());
        assertEquals("Curriculum", dto.getCurriculum());
        assertEquals("First Name", dto.getFirstname());
        assertEquals(timestamp, dto.getLastUpdatedOn());
        assertEquals("Last Name", dto.getLastname());
        assertEquals(eventSpeakers, dto.getEventSpeakers());
    }

    @Test
    void testAddAndRemoveEventSpeaker() {
        SpeakerDto dto = new SpeakerDto();
        EventSpeakerDto eventSpeaker = new EventSpeakerDto();
        List<EventSpeakerDto> eventSpeakers = new ArrayList<>();
        dto.setEventSpeakers(eventSpeakers);

        dto.addEventSpeaker(eventSpeaker);
        assertEquals(1, dto.getEventSpeakers().size());
        assertEquals(dto, eventSpeaker.getSpeaker());

        dto.removeEventSpeaker(eventSpeaker);
        assertEquals(0, dto.getEventSpeakers().size());
        assertNull(eventSpeaker.getSpeaker());
    }
}