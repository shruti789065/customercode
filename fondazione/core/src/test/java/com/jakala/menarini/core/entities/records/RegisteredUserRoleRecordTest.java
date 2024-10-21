package com.jakala.menarini.core.entities.records;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

public class RegisteredUserRoleRecordTest {

    @Test
    public void testSettersAndGetters() {
        RegisteredUserRoleRecord record = new RegisteredUserRoleRecord();
        Long id = 1L;
        Long registeredUserId = 2L;
        Long roleId = 3L;
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime lastUpdatedOn = LocalDateTime.now();

        record.setId(id);
        record.setRegisteredUserId(registeredUserId);
        record.setRoleId(roleId);
        record.setCreatedOn(createdOn);
        record.setLastUpdatedOn(lastUpdatedOn);

        assertEquals(id, record.getId());
        assertEquals(registeredUserId, record.getRegisteredUserId());
        assertEquals(roleId, record.getRoleId());
        assertEquals(createdOn, record.getCreatedOn());
        assertEquals(lastUpdatedOn, record.getLastUpdatedOn());
    }

    @Test
    public void testConstructor() {
        Long id = 1L;
        Long registeredUserId = 2L;
        Long roleId = 3L;
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime lastUpdatedOn = LocalDateTime.now();

        RegisteredUserRoleRecord record = new RegisteredUserRoleRecord(id, registeredUserId, roleId, createdOn, lastUpdatedOn);

        assertEquals(id, record.getId());
        assertEquals(registeredUserId, record.getRegisteredUserId());
        assertEquals(roleId, record.getRoleId());
        assertEquals(createdOn, record.getCreatedOn());
        assertEquals(lastUpdatedOn, record.getLastUpdatedOn());
    }
}