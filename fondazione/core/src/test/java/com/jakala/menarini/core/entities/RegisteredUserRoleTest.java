package com.jakala.menarini.core.entities;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.entities.utils.DbSchema;
import com.jakala.menarini.core.entities.utils.Keys;

import static org.junit.jupiter.api.Assertions.*;

class RegisteredUserRoleTest {

    @Test
    void testFields() {
        RegisteredUserRole registeredUserRole = RegisteredUserRole.REGISTERED_USER_ROLE;

        assertNotNull(registeredUserRole.ID);
        assertNotNull(registeredUserRole.REGISTERED_USER_ID);
        assertNotNull(registeredUserRole.ROLE_ID);
        assertNotNull(registeredUserRole.CREATED_ON);
        assertNotNull(registeredUserRole.LAST_UPDATED_ON);
    }

    @Test
    void testGetSchema() {
        RegisteredUserRole registeredUserRole = RegisteredUserRole.REGISTERED_USER_ROLE;
        assertEquals(DbSchema.MENARINI, registeredUserRole.getSchema());
    }

    @Test
    void testGetPrimaryKey() {
        RegisteredUserRole registeredUserRole = RegisteredUserRole.REGISTERED_USER_ROLE;
        assertEquals(Keys.KEY_REGISTERED_USER_ROLE_PRIMARY, registeredUserRole.getPrimaryKey());
    }

    @Test
    void testGetIdentity() {
        RegisteredUserRole registeredUserRole = RegisteredUserRole.REGISTERED_USER_ROLE;
        assertNotNull(registeredUserRole.getIdentity());
    }

    @Test
    void testGetIndexes() {
        RegisteredUserRole registeredUserRole = RegisteredUserRole.REGISTERED_USER_ROLE;
        assertEquals(2, registeredUserRole.getIndexes().size());
    }

    @Test
    void testGetReferences() {
        RegisteredUserRole registeredUserRole = RegisteredUserRole.REGISTERED_USER_ROLE;
        assertEquals(2, registeredUserRole.getReferences().size());
    }

    @Test
    void testRegisteredUser() {
        RegisteredUserRole registeredUserRole = RegisteredUserRole.REGISTERED_USER_ROLE;
        assertNotNull(registeredUserRole.registeredUser());
    }

    @Test
    void testRole() {
        RegisteredUserRole registeredUserRole = RegisteredUserRole.REGISTERED_USER_ROLE;
        assertNotNull(registeredUserRole.role());
    }

    @Test
    void testAs() {
        RegisteredUserRole registeredUserRole = RegisteredUserRole.REGISTERED_USER_ROLE.as("alias");
        assertEquals("alias", registeredUserRole.getName());
    }

    @Test
    void testRename() {
        RegisteredUserRole registeredUserRole = RegisteredUserRole.REGISTERED_USER_ROLE.rename("new_name");
        assertEquals("new_name", registeredUserRole.getName());
    }
}