package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoleDtoTest {

    @Test
    void testGettersAndSetters() {
        RoleDto dto = new RoleDto();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dto.setId(1L);
        dto.setCreatedOn(timestamp);
        dto.setDescription("Description");
        dto.setLastUpdatedOn(timestamp);
        dto.setName("Name");
        List<RegisteredUserRoleDto> registeredUserRoles = new ArrayList<>();
        dto.setRegisteredUserRoles(registeredUserRoles);

        assertEquals(1L, dto.getId());
        assertEquals(timestamp, dto.getCreatedOn());
        assertEquals("Description", dto.getDescription());
        assertEquals(timestamp, dto.getLastUpdatedOn());
        assertEquals("Name", dto.getName());
        assertEquals(registeredUserRoles, dto.getRegisteredUserRoles());
    }

    @Test
    void testAddAndRemoveRegisteredUserRole() {
        RoleDto dto = new RoleDto();
        RegisteredUserRoleDto registeredUserRole = new RegisteredUserRoleDto();
        List<RegisteredUserRoleDto> registeredUserRoles = new ArrayList<>();
        dto.setRegisteredUserRoles(registeredUserRoles);

        dto.addRegisteredUserRole(registeredUserRole);
        assertEquals(1, dto.getRegisteredUserRoles().size());
        assertEquals(dto, registeredUserRole.getRole());

        dto.removeRegisteredUserRole(registeredUserRole);
        assertEquals(0, dto.getRegisteredUserRoles().size());
        assertNull(registeredUserRole.getRole());
    }
}