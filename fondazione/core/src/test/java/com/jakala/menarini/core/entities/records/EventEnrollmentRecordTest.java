package com.jakala.menarini.core.entities.records;

import org.jooq.JSON;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;


class EventEnrollmentRecordTest {

    private EventEnrollmentRecord record;

    @BeforeEach
    void setUp() {
        record = new EventEnrollmentRecord();
    }

    @Test
    void testGettersAndSetters() {
        Long id = 1L;
        String eventId = "event1";
        Long registeredUserId = 2L;
        String isLiveStream = "yes";
        LocalDateTime liveStreamRegistrationTs = LocalDateTime.now();
        String isResidential = "no";
        LocalDateTime residentialRegistrationTs = LocalDateTime.now();
        JSON inPersonParticipationDateList = JSON.json("{\"dates\":[]}");
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime lastUpdatedOn = LocalDateTime.now();

        record.setId(id);
        record.setEventId(eventId);
        record.setRegisteredUserId(registeredUserId);
        record.setIsLiveStream(isLiveStream);
        record.setLiveStreamRegistrationTs(liveStreamRegistrationTs);
        record.setIsResidential(isResidential);
        record.setResidentialRegistrationTs(residentialRegistrationTs);
        record.setInPersonParticipationDateList(inPersonParticipationDateList);
        record.setCreatedOn(createdOn);
        record.setLastUpdatedOn(lastUpdatedOn);

        assertEquals(id, record.getId());
        assertEquals(eventId, record.getEventId());
        assertEquals(registeredUserId, record.getRegisteredUserId());
        assertEquals(isLiveStream, record.getIsLiveStream());
        assertEquals(liveStreamRegistrationTs, record.getLiveStreamRegistrationTs());
        assertEquals(isResidential, record.getIsResidential());
        assertEquals(residentialRegistrationTs, record.getResidentialRegistrationTs());
        assertEquals(inPersonParticipationDateList, record.getInPersonParticipationDateList());
        assertEquals(createdOn, record.getCreatedOn());
        assertEquals(lastUpdatedOn, record.getLastUpdatedOn());
    }

    @Test
    void testConstructors() {
        Long id = 1L;
        String eventId = "event1";
        Long registeredUserId = 2L;
        String isLiveStream = "yes";
        LocalDateTime liveStreamRegistrationTs = LocalDateTime.now();
        String isResidential = "no";
        LocalDateTime residentialRegistrationTs = LocalDateTime.now();
        JSON inPersonParticipationDateList = JSON.json("{\"dates\":[]}");
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime lastUpdatedOn = LocalDateTime.now();

        EventEnrollmentRecord record = new EventEnrollmentRecord(id, eventId, registeredUserId, isLiveStream, liveStreamRegistrationTs, isResidential, residentialRegistrationTs, inPersonParticipationDateList, createdOn, lastUpdatedOn);

        assertEquals(id, record.getId());
        assertEquals(eventId, record.getEventId());
        assertEquals(registeredUserId, record.getRegisteredUserId());
        assertEquals(isLiveStream, record.getIsLiveStream());
        assertEquals(liveStreamRegistrationTs, record.getLiveStreamRegistrationTs());
        assertEquals(isResidential, record.getIsResidential());
        assertEquals(residentialRegistrationTs, record.getResidentialRegistrationTs());
        assertEquals(inPersonParticipationDateList, record.getInPersonParticipationDateList());
        assertEquals(createdOn, record.getCreatedOn());
        assertEquals(lastUpdatedOn, record.getLastUpdatedOn());
    }

    @Test
    void testValuesMethods() {
        Long id = 1L;
        String eventId = "event1";
        Long registeredUserId = 2L;
        String isLiveStream = "yes";
        LocalDateTime liveStreamRegistrationTs = LocalDateTime.now();
        String isResidential = "no";
        LocalDateTime residentialRegistrationTs = LocalDateTime.now();
        JSON inPersonParticipationDateList = JSON.json("{\"dates\":[]}");
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime lastUpdatedOn = LocalDateTime.now();

        record.values(id, eventId, registeredUserId, isLiveStream, liveStreamRegistrationTs, isResidential, residentialRegistrationTs, inPersonParticipationDateList, createdOn, lastUpdatedOn);

        assertEquals(id, record.value1());
        assertEquals(eventId, record.value2());
        assertEquals(registeredUserId, record.value3());
        assertEquals(isLiveStream, record.value4());
        assertEquals(liveStreamRegistrationTs, record.value5());
        assertEquals(isResidential, record.value6());
        assertEquals(residentialRegistrationTs, record.value7());
        assertEquals(inPersonParticipationDateList, record.value8());
        assertEquals(createdOn, record.value9());
        assertEquals(lastUpdatedOn, record.value10());
    }
}