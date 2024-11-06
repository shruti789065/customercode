package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.dto.cognito.ForgetPasswordDto;

import static org.junit.jupiter.api.Assertions.*;

class ForgetPasswordDtoTest {

    @Test
    void testGettersAndSetters() {
        ForgetPasswordDto dto = new ForgetPasswordDto();

        dto.setEmail("email@example.com");

        assertEquals("email@example.com", dto.getEmail());
    }
}