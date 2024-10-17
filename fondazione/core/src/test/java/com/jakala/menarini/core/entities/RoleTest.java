package com.jakala.menarini.core.entities;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.entities.utils.DbSchema;
import com.jakala.menarini.core.entities.utils.Keys;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testFields() {
        Role role = Role.ROLE;

        assertNotNull(role.ID);
        assertNotNull(role.NAME);
        assertNotNull(role.DESCRIPTION);
        assertNotNull(role.CREATED_ON);
        assertNotNull(role.LAST_UPDATED_ON);
    }

    @Test
    void testGetSchema() {
        Role role = Role.ROLE;
        assertEquals(DbSchema.MENARINI, role.getSchema());
    }

    @Test
    void testGetPrimaryKey() {
        Role role = Role.ROLE;
        assertEquals(Keys.KEY_ROLE_PRIMARY, role.getPrimaryKey());
    }

    @Test
    void testGetIdentity() {
        Role role = Role.ROLE;
        assertNotNull(role.getIdentity());
    }

    @Test
    void testAs() {
        Role role = Role.ROLE.as("alias");
        assertEquals("alias", role.getName());
    }

    @Test
    void testRename() {
        Role role = Role.ROLE.rename("new_name");
        assertEquals("new_name", role.getName());
    }
}