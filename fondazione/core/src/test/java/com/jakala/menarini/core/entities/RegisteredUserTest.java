package com.jakala.menarini.core.entities;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.entities.utils.DbSchema;
import com.jakala.menarini.core.entities.utils.Keys;

import static org.junit.jupiter.api.Assertions.*;

class RegisteredUserTest {

    @Test
    void testFields() {
        RegisteredUser registeredUser = RegisteredUser.REGISTERED_USER;

        assertNotNull(registeredUser.ID);
        assertNotNull(registeredUser.LASTNAME);
        assertNotNull(registeredUser.FIRSTNAME);
        assertNotNull(registeredUser.EMAIL);
        assertNotNull(registeredUser.USERNAME);
        assertNotNull(registeredUser.OCCUPATION);
        assertNotNull(registeredUser.GENDER);
        assertNotNull(registeredUser.BIRTH_DATE);
        assertNotNull(registeredUser.PHONE);
        assertNotNull(registeredUser.TAX_ID_CODE);
        assertNotNull(registeredUser.LINKEDIN_PROFILE);
        assertNotNull(registeredUser.COUNTRY);
        assertNotNull(registeredUser.PERSONAL_DATA_PROCESSING_CONSENT);
        assertNotNull(registeredUser.PERSONAL_DATA_PROCESSING_CONSENT_TS);
        assertNotNull(registeredUser.PROFILING_CONSENT);
        assertNotNull(registeredUser.PROFILING_CONSENT_TS);
        assertNotNull(registeredUser.NEWSLETTER_SUBSCRIPTION);
        assertNotNull(registeredUser.NEWSLETTER_SUBSCRIPTION_TS);
        assertNotNull(registeredUser.LEGACY_ID);
        assertNotNull(registeredUser.REGISTRATION_STATUS);
        assertNotNull(registeredUser.CREATED_ON);
        assertNotNull(registeredUser.LAST_UPDATED_ON);
    }

    @Test
    void testGetSchema() {
        RegisteredUser registeredUser = RegisteredUser.REGISTERED_USER;
        assertEquals(DbSchema.MENARINI, registeredUser.getSchema());
    }

    @Test
    void testGetPrimaryKey() {
        RegisteredUser registeredUser = RegisteredUser.REGISTERED_USER;
        assertEquals(Keys.KEY_REGISTERED_USER_PRIMARY, registeredUser.getPrimaryKey());
    }

    @Test
    void testGetIdentity() {
        RegisteredUser registeredUser = RegisteredUser.REGISTERED_USER;
        assertNotNull(registeredUser.getIdentity());
    }

    @Test
    void testAs() {
        RegisteredUser registeredUser = RegisteredUser.REGISTERED_USER.as("alias");
        assertEquals("alias", registeredUser.getName());
    }

    @Test
    void testRename() {
        RegisteredUser registeredUser = RegisteredUser.REGISTERED_USER.rename("new_name");
        assertEquals("new_name", registeredUser.getName());
    }
}