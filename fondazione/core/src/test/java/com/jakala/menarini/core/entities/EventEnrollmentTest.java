package com.jakala.menarini.core.entities;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.entities.utils.DbSchema;
import com.jakala.menarini.core.entities.utils.Keys;

import static org.junit.jupiter.api.Assertions.*;

class EventEnrollmentTest {

    @Test
    void testFields() {
        EventEnrollment eventEnrollment = EventEnrollment.EVENT_ENROLLMENT;

        assertNotNull(eventEnrollment.ID);
        assertNotNull(eventEnrollment.EVENT_ID);
        assertNotNull(eventEnrollment.REGISTERED_USER_ID);
        assertNotNull(eventEnrollment.IS_LIVE_STREAM);
        assertNotNull(eventEnrollment.LIVE_STREAM_REGISTRATION_TS);
        assertNotNull(eventEnrollment.IS_RESIDENTIAL);
        assertNotNull(eventEnrollment.RESIDENTIAL_REGISTRATION_TS);
        assertNotNull(eventEnrollment.IN_PERSON_PARTICIPATION_DATE_LIST);
        assertNotNull(eventEnrollment.CREATED_ON);
        assertNotNull(eventEnrollment.LAST_UPDATED_ON);
    }

    @Test
    void testGetSchema() {
        EventEnrollment eventEnrollment = EventEnrollment.EVENT_ENROLLMENT;
        assertEquals(DbSchema.MENARINI, eventEnrollment.getSchema());
    }

    @Test
    void testGetPrimaryKey() {
        EventEnrollment eventEnrollment = EventEnrollment.EVENT_ENROLLMENT;
        assertEquals(Keys.KEY_EVENT_ENROLLMENT_PRIMARY, eventEnrollment.getPrimaryKey());
    }

    @Test
    void testGetIdentity() {
        EventEnrollment eventEnrollment = EventEnrollment.EVENT_ENROLLMENT;
        assertNotNull(eventEnrollment.getIdentity());
    }

    @Test
    void testGetIndexes() {
        EventEnrollment eventEnrollment = EventEnrollment.EVENT_ENROLLMENT;
        assertEquals(2, eventEnrollment.getIndexes().size());
    }

    @Test
    void testGetReferences() {
        EventEnrollment eventEnrollment = EventEnrollment.EVENT_ENROLLMENT;
        assertEquals(1, eventEnrollment.getReferences().size());
    }

    @Test
    void testRegisteredUser() {
        EventEnrollment eventEnrollment = EventEnrollment.EVENT_ENROLLMENT;
        assertNotNull(eventEnrollment.registeredUser());
    }

    @Test
    void testAs() {
        EventEnrollment eventEnrollment = EventEnrollment.EVENT_ENROLLMENT.as("alias");
        assertEquals("alias", eventEnrollment.getName());
    }

    @Test
    void testRename() {
        EventEnrollment eventEnrollment = EventEnrollment.EVENT_ENROLLMENT.rename("new_name");
        assertEquals("new_name", eventEnrollment.getName());
    }
}