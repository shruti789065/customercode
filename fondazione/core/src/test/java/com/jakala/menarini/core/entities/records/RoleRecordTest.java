package com.jakala.menarini.core.entities.records;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

class RoleRecordTest {

    @Test
    void testSettersAndGetters() {
        RoleRecord record = new RoleRecord();
        Long id = 1L;
        String name = "Admin";
        String description = "Administrator role";
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime lastUpdatedOn = LocalDateTime.now();

        record.setId(id);
        record.setName(name);
        record.setDescription(description);
        record.setCreatedOn(createdOn);
        record.setLastUpdatedOn(lastUpdatedOn);

        assertEquals(id, record.getId());
        assertEquals(name, record.getName());
        assertEquals(description, record.getDescription());
        assertEquals(createdOn, record.getCreatedOn());
        assertEquals(lastUpdatedOn, record.getLastUpdatedOn());
    }

    @Test
    void testConstructor() {
        Long id = 1L;
        String name = "Admin";
        String description = "Administrator role";
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime lastUpdatedOn = LocalDateTime.now();

        RoleRecord record = new RoleRecord(id, name, description, createdOn, lastUpdatedOn);

        assertEquals(id, record.getId());
        assertEquals(name, record.getName());
        assertEquals(description, record.getDescription());
        assertEquals(createdOn, record.getCreatedOn());
        assertEquals(lastUpdatedOn, record.getLastUpdatedOn());
    }
}