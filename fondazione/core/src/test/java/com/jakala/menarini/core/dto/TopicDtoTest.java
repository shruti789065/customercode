package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TopicDtoTest {

    @Test
    void testGettersAndSetters() {
        TopicDto dto = new TopicDto();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dto.setId("1");
        dto.setCreatedOn(timestamp);
        dto.setLastUpdatedOn(timestamp);
        dto.setName("Topic Name");
        List<EventTopicDto> eventTopics = new ArrayList<>();
        dto.setEventTopics(eventTopics);
        List<RegisteredUserTopicDto> registeredUserTopics = new ArrayList<>();
        dto.setRegisteredUserTopics(registeredUserTopics);

        assertEquals("1", dto.getId());
        assertEquals(timestamp, dto.getCreatedOn());
        assertEquals(timestamp, dto.getLastUpdatedOn());
        assertEquals("Topic Name", dto.getName());
        assertEquals(eventTopics, dto.getEventTopics());
        assertEquals(registeredUserTopics, dto.getRegisteredUserTopics());
    }

    @Test
    void testAddAndRemoveEventTopic() {
        TopicDto dto = new TopicDto();
        EventTopicDto eventTopic = new EventTopicDto();
        List<EventTopicDto> eventTopics = new ArrayList<>();
        dto.setEventTopics(eventTopics);

        dto.addEventTopic(eventTopic);
        assertEquals(1, dto.getEventTopics().size());
        assertEquals(dto, eventTopic.getTopic());

        dto.removeEventTopic(eventTopic);
        assertEquals(0, dto.getEventTopics().size());
        assertNull(eventTopic.getTopic());
    }

    @Test
    void testAddAndRemoveRegisteredUserTopic() {
        TopicDto dto = new TopicDto();
        RegisteredUserTopicDto registeredUserTopic = new RegisteredUserTopicDto();
        List<RegisteredUserTopicDto> registeredUserTopics = new ArrayList<>();
        dto.setRegisteredUserTopics(registeredUserTopics);

        dto.addRegisteredUserTopic(registeredUserTopic);
        assertEquals(1, dto.getRegisteredUserTopics().size());
        assertEquals(dto, registeredUserTopic.getTopic());

        dto.removeRegisteredUserTopic(registeredUserTopic);
        assertEquals(0, dto.getRegisteredUserTopics().size());
        assertNull(registeredUserTopic.getTopic());
    }
}