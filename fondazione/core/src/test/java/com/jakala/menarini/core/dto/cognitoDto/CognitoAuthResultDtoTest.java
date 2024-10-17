package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class CognitoAuthResultDtoTest {

    @Test
    void testGettersAndSetters() {
        CognitoAuthResultDto dto = new CognitoAuthResultDto();

        dto.setAccessToken("accessToken");
        dto.setIdToken("idToken");
        dto.setExpiresIn(3600);
        dto.setRefreshToken("refreshToken");
        dto.setTokenType("tokenType");

        assertEquals("accessToken", dto.getAccessToken());
        assertEquals("idToken", dto.getIdToken());
        assertEquals(3600, dto.getExpiresIn());
        assertEquals("refreshToken", dto.getRefreshToken());
        assertEquals("tokenType", dto.getTokenType());
    }
}