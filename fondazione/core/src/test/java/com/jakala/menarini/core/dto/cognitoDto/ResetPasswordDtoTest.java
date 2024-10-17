package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResetPasswordDtoTest {

    @Test
    void testGettersAndSetters() {
        ResetPasswordDto dto = new ResetPasswordDto();

        dto.setAccessToken("accessToken");
        dto.setPreviousPassword("previousPassword");
        dto.setProposedPassword("proposedPassword");

        assertEquals("accessToken", dto.getAccessToken());
        assertEquals("previousPassword", dto.getPreviousPassword());
        assertEquals("proposedPassword", dto.getProposedPassword());
    }
}