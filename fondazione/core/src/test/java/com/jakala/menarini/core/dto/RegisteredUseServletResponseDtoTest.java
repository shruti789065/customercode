package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisteredUseServletResponseDtoTest {

    @Test
    void testGettersAndSetters() {
        RegisteredUseServletResponseDto dto = new RegisteredUseServletResponseDto();
        RegisteredUserDto registeredUser = new RegisteredUserDto();

        dto.setSuccess(true);
        dto.setUpdatedUser(registeredUser);
        dto.setErrorMessage("Error");
        dto.setIslogOut(true);

        assertTrue(dto.isSuccess());
        assertEquals(registeredUser, dto.getUpdatedUser());
        assertEquals("Error", dto.getErrorMessage());
        assertTrue(dto.getIslogOut());
    }
}