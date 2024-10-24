package com.jakala.menarini.core.entities;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.entities.utils.DbSchema;
import com.jakala.menarini.core.entities.utils.Keys;

import static org.junit.jupiter.api.Assertions.*;

class RegisteredUserTopicTest {

    @Test
    void testFields() {
        RegisteredUserTopic registeredUserTopic = RegisteredUserTopic.REGISTERED_USER_TOPIC;

        assertNotNull(registeredUserTopic.ID);
        assertNotNull(registeredUserTopic.SEQ_NO);
        assertNotNull(registeredUserTopic.TOPIC_ID);
        assertNotNull(registeredUserTopic.REGISTERED_USER_ID);
        assertNotNull(registeredUserTopic.CREATED_ON);
        assertNotNull(registeredUserTopic.LAST_UPDATED_ON);
    }

    @Test
    void testGetSchema() {
        RegisteredUserTopic registeredUserTopic = RegisteredUserTopic.REGISTERED_USER_TOPIC;
        assertEquals(DbSchema.MENARINI, registeredUserTopic.getSchema());
    }

    @Test
    void testGetPrimaryKey() {
        RegisteredUserTopic registeredUserTopic = RegisteredUserTopic.REGISTERED_USER_TOPIC;
        assertEquals(Keys.KEY_REGISTERED_USER_TOPIC_PRIMARY, registeredUserTopic.getPrimaryKey());
    }

    @Test
    void testGetIdentity() {
        RegisteredUserTopic registeredUserTopic = RegisteredUserTopic.REGISTERED_USER_TOPIC;
        assertNotNull(registeredUserTopic.getIdentity());
    }

    @Test
    void testGetIndexes() {
        RegisteredUserTopic registeredUserTopic = RegisteredUserTopic.REGISTERED_USER_TOPIC;
        assertEquals(2, registeredUserTopic.getIndexes().size());
    }

    @Test
    void testGetReferences() {
        RegisteredUserTopic registeredUserTopic = RegisteredUserTopic.REGISTERED_USER_TOPIC;
        assertEquals(1, registeredUserTopic.getReferences().size());
    }

    @Test
    void testRegisteredUser() {
        RegisteredUserTopic registeredUserTopic = RegisteredUserTopic.REGISTERED_USER_TOPIC;
        assertNotNull(registeredUserTopic.registeredUser());
    }

    @Test
    void testAs() {
        RegisteredUserTopic registeredUserTopic = RegisteredUserTopic.REGISTERED_USER_TOPIC.as("alias");
        assertEquals("alias", registeredUserTopic.getName());
    }

    @Test
    void testRename() {
        RegisteredUserTopic registeredUserTopic = RegisteredUserTopic.REGISTERED_USER_TOPIC.rename("new_name");
        assertEquals("new_name", registeredUserTopic.getName());
    }
}