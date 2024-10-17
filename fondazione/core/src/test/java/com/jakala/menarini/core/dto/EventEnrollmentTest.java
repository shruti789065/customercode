package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class EventEnrollmentTest {

    @Test
    void testId() {
        EventEnrollment dto = new EventEnrollment();
        long id = 1L;
        dto.setId(id);
        assertEquals(id, dto.getId());
    }

    @Test
    void testCreatedOn() {
        EventEnrollment dto = new EventEnrollment();
        Timestamp createdOn = new Timestamp(System.currentTimeMillis());
        dto.setCreatedOn(createdOn);
        assertEquals(createdOn, dto.getCreatedOn());
    }

    @Test
    void testInPersonParticipationDateList() {
        EventEnrollment dto = new EventEnrollment();
        Object inPersonParticipationDateList = new Object();
        dto.setInPersonParticipationDateList(inPersonParticipationDateList);
        assertEquals(inPersonParticipationDateList, dto.getInPersonParticipationDateList());
    }

    @Test
    void testIsLiveStream() {
        EventEnrollment dto = new EventEnrollment();
        String isLiveStream = "Yes";
        dto.setIsLiveStream(isLiveStream);
        assertEquals(isLiveStream, dto.getIsLiveStream());
    }

    @Test
    void testIsResidential() {
        EventEnrollment dto = new EventEnrollment();
        String isResidential = "Yes";
        dto.setIsResidential(isResidential);
        assertEquals(isResidential, dto.getIsResidential());
    }

    @Test
    void testLastUpdatedOn() {
        EventEnrollment dto = new EventEnrollment();
        Timestamp lastUpdatedOn = new Timestamp(System.currentTimeMillis());
        dto.setLastUpdatedOn(lastUpdatedOn);
        assertEquals(lastUpdatedOn, dto.getLastUpdatedOn());
    }

    @Test
    void testLiveStreamRegistrationTs() {
        EventEnrollment dto = new EventEnrollment();
        Timestamp liveStreamRegistrationTs = new Timestamp(System.currentTimeMillis());
        dto.setLiveStreamRegistrationTs(liveStreamRegistrationTs);
        assertEquals(liveStreamRegistrationTs, dto.getLiveStreamRegistrationTs());
    }

    @Test
    void testResidentialRegistrationTs() {
        EventEnrollment dto = new EventEnrollment();
        Timestamp residentialRegistrationTs = new Timestamp(System.currentTimeMillis());
        dto.setResidentialRegistrationTs(residentialRegistrationTs);
        assertEquals(residentialRegistrationTs, dto.getResidentialRegistrationTs());
    }

    @Test
    void testEvent() {
        EventEnrollment dto = new EventEnrollment();
        EventDto event = new EventDto();
        dto.setEvent(event);
        assertEquals(event, dto.getEvent());
    }

    @Test
    void testRegisteredUser() {
        EventEnrollment dto = new EventEnrollment();
        RegisteredUserDto registeredUser = new RegisteredUserDto();
        dto.setRegisteredUser(registeredUser);
        assertEquals(registeredUser, dto.getRegisteredUser());
    }
}