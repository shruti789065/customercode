package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EventUserResponseDtoTest {

    private EventUserResponseDto eventUserResponseDto;

    @BeforeEach
    void setUp() {
        eventUserResponseDto = new EventUserResponseDto();
    }

    @Test
    void testGetAndSetId() {
        eventUserResponseDto.setId(1L);
        assertEquals(1L, eventUserResponseDto.getId());
    }

    @Test
    void testGetAndSetEventId() {
        eventUserResponseDto.setEventId(2L);
        assertEquals(2L, eventUserResponseDto.getEventId());
    }

    @Test
    void testGetAndSetRegisterOn() {
        Timestamp timestamp = Timestamp.valueOf("2023-01-01 00:00:00");
        eventUserResponseDto.setRegisterOn(timestamp);
        assertEquals(timestamp, eventUserResponseDto.getRegisterOn());
    }

    @Test
    void testIsAndSetResidential() {
        eventUserResponseDto.setResidential(true);
        assertTrue(eventUserResponseDto.isResidential());
    }

    @Test
    void testGetAndSetDates() {
        List<Timestamp> dates = Arrays.asList(Timestamp.valueOf("2023-01-01 00:00:00"), Timestamp.valueOf("2023-01-02 00:00:00"));
        eventUserResponseDto.setDates(dates);
        assertEquals(dates, eventUserResponseDto.getDates());
    }

    @Test
    void testGetAndSetPhone() {
        eventUserResponseDto.setPhone("1234567890");
        assertEquals("1234567890", eventUserResponseDto.getPhone());
    }
}