package com.jakala.menarini.core.entities.records;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
class RegisteredUserTopicRecordTest {

    @Test
    void testSettersAndGetters() {
        RegisteredUserTopicRecord record = new RegisteredUserTopicRecord();
        Long id = 1L;
        Integer seqNo = 1;
        String topicId = "topic1";
        Long registeredUserId = 1L;
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime lastUpdatedOn = LocalDateTime.now();

        record.setId(id);
        record.setSeqNo(seqNo);
        record.setTopicId(topicId);
        record.setRegisteredUserId(registeredUserId);
        record.setCreatedOn(createdOn);
        record.setLastUpdatedOn(lastUpdatedOn);

        assertEquals(id, record.getId());
        assertEquals(seqNo, record.getSeqNo());
        assertEquals(topicId, record.getTopicId());
        assertEquals(registeredUserId, record.getRegisteredUserId());
        assertEquals(createdOn, record.getCreatedOn());
        assertEquals(lastUpdatedOn, record.getLastUpdatedOn());
    }

    @Test
    void testKey() {
        RegisteredUserTopicRecord record = new RegisteredUserTopicRecord();
        record.setId(1L);
        assertNotNull(record.key());
        assertEquals(1L, record.key().value1());
    }

    @Test
    void testFieldsRow() {
        RegisteredUserTopicRecord record = new RegisteredUserTopicRecord();
        assertNotNull(record.fieldsRow());
    }

    @Test
    void testValuesRow() {
        RegisteredUserTopicRecord record = new RegisteredUserTopicRecord();
        assertNotNull(record.valuesRow());
    }

    @Test
    void testValues() {
        RegisteredUserTopicRecord record = new RegisteredUserTopicRecord();
        Long id = 1L;
        Integer seqNo = 1;
        String topicId = "topic1";
        Long registeredUserId = 1L;
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime lastUpdatedOn = LocalDateTime.now();

        record.values(id, seqNo, topicId, registeredUserId, createdOn, lastUpdatedOn);

        assertEquals(id, record.getId());
        assertEquals(seqNo, record.getSeqNo());
        assertEquals(topicId, record.getTopicId());
        assertEquals(registeredUserId, record.getRegisteredUserId());
        assertEquals(createdOn, record.getCreatedOn());
        assertEquals(lastUpdatedOn, record.getLastUpdatedOn());
    }
}