package com.jakala.menarini.core.entities.records;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.entities.RegisteredUserTopic;
import com.jakala.menarini.core.entities.utils.DbSchema;
import com.jakala.menarini.core.entities.utils.Keys;


class RegisteredUserTopicTest {

    @Test
    void testFields() {
        RegisteredUserTopic table = RegisteredUserTopic.REGISTERED_USER_TOPIC;

        assertNotNull(table.ID);
        assertNotNull(table.SEQ_NO);
        assertNotNull(table.TOPIC_ID);
        assertNotNull(table.REGISTERED_USER_ID);
        assertNotNull(table.CREATED_ON);
        assertNotNull(table.LAST_UPDATED_ON);
    }

    @Test
    void testGetRecordType() {
        RegisteredUserTopic table = RegisteredUserTopic.REGISTERED_USER_TOPIC;
        assertEquals(RegisteredUserTopicRecord.class, table.getRecordType());
    }

    @Test
    void testGetSchema() {
        RegisteredUserTopic table = RegisteredUserTopic.REGISTERED_USER_TOPIC;
        assertEquals(DbSchema.MENARINI, table.getSchema());
    }

    @Test
    void testGetPrimaryKey() {
        RegisteredUserTopic table = RegisteredUserTopic.REGISTERED_USER_TOPIC;
        assertEquals(Keys.KEY_REGISTERED_USER_TOPIC_PRIMARY, table.getPrimaryKey());
    }

    @Test
    void testGetIdentity() {
        RegisteredUserTopic table = RegisteredUserTopic.REGISTERED_USER_TOPIC;
        assertNotNull(table.getIdentity());
    }

    @Test
    void testAs() {
        RegisteredUserTopic table = RegisteredUserTopic.REGISTERED_USER_TOPIC;
        RegisteredUserTopic alias = table.as("alias");
        assertEquals("alias", alias.getName());
    }

    @Test
    void testRename() {
        RegisteredUserTopic table = RegisteredUserTopic.REGISTERED_USER_TOPIC;
        RegisteredUserTopic renamed = table.rename("new_name");
        assertEquals("new_name", renamed.getName());
    }
}