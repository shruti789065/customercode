package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class RegisteredUserRoleDtoTest {

    @Test
    void testGettersAndSetters() {
        RegisteredUserRoleDto dto = new RegisteredUserRoleDto();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dto.setId(1L);
        dto.setCreatedOn(timestamp);
        dto.setLastUpdatedOn(timestamp);
        RegisteredUserDto registeredUser = new RegisteredUserDto();
        dto.setRegisteredUser(registeredUser);
        RoleDto role = new RoleDto();
        dto.setRole(role);

        assertEquals(1L, dto.getId());
        assertEquals(timestamp, dto.getCreatedOn());
        assertEquals(timestamp, dto.getLastUpdatedOn());
        assertEquals(registeredUser, dto.getRegisteredUser());
        assertEquals(role, dto.getRole());
    }
}