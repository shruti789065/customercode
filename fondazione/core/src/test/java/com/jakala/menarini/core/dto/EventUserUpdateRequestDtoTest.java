package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventUserUpdateRequestDtoTest {

    private EventUserUpdateRequestDto eventUserUpdateRequestDto;

    @BeforeEach
    void setUp() {
        eventUserUpdateRequestDto = new EventUserUpdateRequestDto();
    }

    @Test
    void testGetAndSetEnrollmentId() {
        eventUserUpdateRequestDto.setEnrollmentId(1);
        assertEquals(1, eventUserUpdateRequestDto.getEnrollmentId());
    }

    @Test
    void testGetAndSetDates() {
        String[] dates = {"2023-01-01", "2023-01-02"};
        eventUserUpdateRequestDto.setDates(dates);
        assertArrayEquals(dates, eventUserUpdateRequestDto.getDates());
    }

    @Test
    void testGetAndSetPhone() {
        eventUserUpdateRequestDto.setPhone("1234567890");
        assertEquals("1234567890", eventUserUpdateRequestDto.getPhone());
    }
}