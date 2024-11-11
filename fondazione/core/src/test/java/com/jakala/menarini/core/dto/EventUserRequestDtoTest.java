package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventUserRequestDtoTest {

    private EventUserRequestDto eventUserRequestDto;

    @BeforeEach
    void setUp() {
        eventUserRequestDto = new EventUserRequestDto();
    }

    @Test
    void testGetAndSetEventId() {
        eventUserRequestDto.setEventId("event123");
        assertEquals("event123", eventUserRequestDto.getEventId());
    }

    @Test
    void testGetAndSetDates() {
        String[] dates = {"2023-01-01", "2023-01-02"};
        eventUserRequestDto.setDates(dates);
        assertArrayEquals(dates, eventUserRequestDto.getDates());
    }

    @Test
    void testGetAndSetPhone() {
        eventUserRequestDto.setPhone("1234567890");
        assertEquals("1234567890", eventUserRequestDto.getPhone());
    }

    @Test
    void testGetAndSetLang() {
        eventUserRequestDto.setLang("en");
        assertEquals("en", eventUserRequestDto.getLang());
    }
}