package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventDtoTest {

    @Test
    void testId() {
        EventDto dto = new EventDto();
        long id = 1L;
        dto.setId(id);
        assertEquals(id, dto.getId());
    }

    @Test
    void testCoordinators() {
        EventDto dto = new EventDto();
        String coordinators = "John Doe";
        dto.setCoordinators(coordinators);
        assertEquals(coordinators, dto.getCoordinators());
    }

    @Test
    void testCreatedOn() {
        EventDto dto = new EventDto();
        Timestamp createdOn = new Timestamp(System.currentTimeMillis());
        dto.setCreatedOn(createdOn);
        assertEquals(createdOn, dto.getCreatedOn());
    }

    @Test
    void testDescription() {
        EventDto dto = new EventDto();
        String description = "Event Description";
        dto.setDescription(description);
        assertEquals(description, dto.getDescription());
    }

    @Test
    void testEndDate() {
        EventDto dto = new EventDto();
        Date endDate = new Date();
        dto.setEndDate(endDate);
        assertEquals(endDate, dto.getEndDate());
    }

    @Test
    void testEventType() {
        EventDto dto = new EventDto();
        String eventType = "Conference";
        dto.setEventType(eventType);
        assertEquals(eventType, dto.getEventType());
    }

    @Test
    void testLastUpdatedOn() {
        EventDto dto = new EventDto();
        Timestamp lastUpdatedOn = new Timestamp(System.currentTimeMillis());
        dto.setLastUpdatedOn(lastUpdatedOn);
        assertEquals(lastUpdatedOn, dto.getLastUpdatedOn());
    }

    @Test
    void testStartDate() {
        EventDto dto = new EventDto();
        Date startDate = new Date();
        dto.setStartDate(startDate);
        assertEquals(startDate, dto.getStartDate());
    }

    @Test
    void testSubscription() {
        EventDto dto = new EventDto();
        String subscription = "Free";
        dto.setSubscription(subscription);
        assertEquals(subscription, dto.getSubscription());
    }

    @Test
    void testTitle() {
        EventDto dto = new EventDto();
        String title = "Event Title";
        dto.setTitle(title);
        assertEquals(title, dto.getTitle());
    }

    @Test
    void testLocation() {
        EventDto dto = new EventDto();
        LocationDto location = new LocationDto();
        dto.setLocation(location);
        assertEquals(location, dto.getLocation());
    }

    @Test
    void testVenue() {
        EventDto dto = new EventDto();
        VenueDto venue = new VenueDto();
        dto.setVenue(venue);
        assertEquals(venue, dto.getVenue());
    }

    @Test
    void testEventEnrollments() {
        EventDto dto = new EventDto();
        List<EventEnrollment> eventEnrollments = List.of(new EventEnrollment());
        dto.setEventEnrollments(eventEnrollments);
        assertEquals(eventEnrollments, dto.getEventEnrollments());
    }

    @Test
    void testEventSessionDates() {
        EventDto dto = new EventDto();
        List<EventSessionDateDto> eventSessionDates = List.of(new EventSessionDateDto());
        dto.setEventSessionDates(eventSessionDates);
        assertEquals(eventSessionDates, dto.getEventSessionDates());
    }

    @Test
    void testEventSpeakers() {
        EventDto dto = new EventDto();
        List<EventSpeakerDto> eventSpeakers = List.of(new EventSpeakerDto());
        dto.setEventSpeakers(eventSpeakers);
        assertEquals(eventSpeakers, dto.getEventSpeakers());
    }

    @Test
    void testEventTopics() {
        EventDto dto = new EventDto();
        List<EventTopicDto> eventTopics = List.of(new EventTopicDto());
        dto.setEventTopics(eventTopics);
        assertEquals(eventTopics, dto.getEventTopics());
    }
}