package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.dto.cognito.RefreshDto;

import static org.junit.jupiter.api.Assertions.*;

class RefreshDtoTest {

    @Test
    void testGettersAndSetters() {
        RefreshDto dto = new RefreshDto();

        dto.setRefreshToken("refreshToken");
        dto.setEmail("email@example.com");

        assertEquals("refreshToken", dto.getRefreshToken());
        assertEquals("email@example.com", dto.getEmail());
    }
}