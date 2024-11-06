package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegisteredUserPermissionDtoTest {

    private RegisteredUserPermissionDto registeredUserPermissionDto;

    @BeforeEach
    void setUp() {
        registeredUserPermissionDto = new RegisteredUserPermissionDto();
    }

    @Test
    void testIsAndSetMagazineSubscription() {
        registeredUserPermissionDto.setMagazineSubscription(true);
        assertTrue(registeredUserPermissionDto.isMagazineSubscription());
    }

    @Test
    void testIsAndSetMaterialAccess() {
        registeredUserPermissionDto.setMaterialAccess(true);
        assertTrue(registeredUserPermissionDto.isMaterialAccess());
    }

    @Test
    void testIsAndSetEventSubscription() {
        registeredUserPermissionDto.setEventSubscription(true);
        assertTrue(registeredUserPermissionDto.isEventSubscription());
    }
}