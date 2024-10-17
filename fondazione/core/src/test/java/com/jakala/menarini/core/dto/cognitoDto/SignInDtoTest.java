package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignInDtoTest {

    @Test
    void testGettersAndSetters() {
        SignInDto dto = new SignInDto();

        dto.setEmail("email@example.com");
        dto.setPassword("password");
        dto.setRememberMe(true);

        assertEquals("email@example.com", dto.getEmail());
        assertEquals("password", dto.getPassword());
        assertTrue(dto.getRememberMe());
    }
}