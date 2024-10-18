package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class RegisteredUserTopicDtoTest {

    @Test
    void testGettersAndSetters() {
        RegisteredUserTopicDto dto = new RegisteredUserTopicDto();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dto.setId("1");
        dto.setCreatedOn(timestamp);
        dto.setLastUpdatedOn(timestamp);
        dto.setSeqNo(1);
        RegisteredUserDto registeredUser = new RegisteredUserDto();
        dto.setRegisteredUser(registeredUser);
        TopicDto topic = new TopicDto();
        dto.setTopic(topic);

        assertEquals("1", dto.getId());
        assertEquals(timestamp, dto.getCreatedOn());
        assertEquals(timestamp, dto.getLastUpdatedOn());
        assertEquals(1, dto.getSeqNo());
        assertEquals(registeredUser, dto.getRegisteredUser());
        assertEquals(topic, dto.getTopic());
    }
}