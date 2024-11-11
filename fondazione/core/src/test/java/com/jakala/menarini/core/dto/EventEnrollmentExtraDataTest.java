package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EventEnrollmentExtraDataTest {

    private EventEnrollmentExtraData eventEnrollmentExtraData;

    @BeforeEach
    void setUp() {
        eventEnrollmentExtraData = new EventEnrollmentExtraData();
    }

    @Test
    void testGetAndSetEnrollmentDates() {
        List<Timestamp> dates = Arrays.asList(Timestamp.valueOf("2023-01-01 00:00:00"), Timestamp.valueOf("2023-01-02 00:00:00"));
        eventEnrollmentExtraData.setEnrollmentDates(dates);
        assertEquals(dates, eventEnrollmentExtraData.getEnrollmentDates());
    }

    @Test
    void testGetAndSetPhone() {
        eventEnrollmentExtraData.setPhone("1234567890");
        assertEquals("1234567890", eventEnrollmentExtraData.getPhone());
    }
}