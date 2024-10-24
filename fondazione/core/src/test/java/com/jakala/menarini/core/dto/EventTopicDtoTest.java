package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class EventTopicDtoTest {

    @Test
    void testId() {
        EventTopicDto dto = new EventTopicDto();
        long id = 1L;
        dto.setId(id);
        assertEquals(id, dto.getId());
    }

    @Test
    void testCreatedOn() {
        EventTopicDto dto = new EventTopicDto();
        Timestamp createdOn = new Timestamp(System.currentTimeMillis());
        dto.setCreatedOn(createdOn);
        assertEquals(createdOn, dto.getCreatedOn());
    }

    @Test
    void testLastUpdatedOn() {
        EventTopicDto dto = new EventTopicDto();
        Timestamp lastUpdatedOn = new Timestamp(System.currentTimeMillis());
        dto.setLastUpdatedOn(lastUpdatedOn);
        assertEquals(lastUpdatedOn, dto.getLastUpdatedOn());
    }

    @Test
    void testPriority() {
        EventTopicDto dto = new EventTopicDto();
        int priority = 1;
        dto.setPriority(priority);
        assertEquals(priority, dto.getPriority());
    }

    @Test
    void testEvent() {
        EventTopicDto dto = new EventTopicDto();
        EventDto event = new EventDto();
        dto.setEvent(event);
        assertEquals(event, dto.getEvent());
    }

    @Test
    void testTopic() {
        EventTopicDto dto = new EventTopicDto();
        TopicDto topic = new TopicDto();
        dto.setTopic(topic);
        assertEquals(topic, dto.getTopic());
    }
}