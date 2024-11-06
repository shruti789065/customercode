package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class EventModelDtoTest {

    private EventModelDto eventModelDto;

    @BeforeEach
    void setUp() {
        eventModelDto = new EventModelDto(
                "1",
                "Event Title",
                "Event Description",
                "/content/path",
                "2023-01-01",
                "2023-12-31",
                "Topic1, Topic2",
                "EventType",
                "Location",
                "presentationImage.jpg",
                "subscription"
        );
    }

    @Test
    void testGetId() {
        assertEquals("1", eventModelDto.getId());
    }

    @Test
    void testGetTitle() {
        assertEquals("Event Title", eventModelDto.getTitle());
    }

    @Test
    void testGetDescription() {
        assertEquals("Event Description", eventModelDto.getDescription());
    }

    @Test
    void testGetPath() {
        assertEquals("/content/path", eventModelDto.getPath());
    }

    @Test
    void testGetStartDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date expectedDate = null;
        try {
            expectedDate = dateFormat.parse("2023-01-01");
        } catch (Exception e) {
            fail("Date parsing failed");
        }
        assertEquals(expectedDate, eventModelDto.getStartDate());
    }

    @Test
    void testGetEndDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date expectedDate = null;
        try {
            expectedDate = dateFormat.parse("2023-12-31");
        } catch (Exception e) {
            fail("Date parsing failed");
        }
        assertEquals(expectedDate, eventModelDto.getEndDate());
    }

    @Test
    void testGetStartDateText() {
        assertEquals("01/01/2023", eventModelDto.getStartDateText());
    }

    @Test
    void testGetEndDateText() {
        assertEquals("31/12/2023", eventModelDto.getEndDateText());
    }

    @Test
    void testGetTopics() {
        assertEquals("Topic1, Topic2", eventModelDto.getTopics());
    }

    @Test
    void testGetEventType() {
        assertEquals("EventType", eventModelDto.getEventType());
    }

    @Test
    void testGetLocation() {
        assertEquals("Location", eventModelDto.getLocation());
    }

    @Test
    void testGetPresentationImage() {
        assertEquals("presentationImage.jpg", eventModelDto.getPresentationImage());
    }

    @Test
    void testGetSubscription() {
        assertEquals("subscription", eventModelDto.getSubscription());
    }

    @Test
    void testSetSubscription() {
        eventModelDto.setSubscription("newSubscription");
        assertEquals("newSubscription", eventModelDto.getSubscription());
    }
}