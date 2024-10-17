package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class CognitoAuthParametersDtoTest {

    @Test
    void testGettersAndSetters() {
        CognitoAuthParametersDto dto = new CognitoAuthParametersDto();

        dto.setUSERNAME("testUser");
        dto.setPASSWORD("testPassword");
        dto.setSECRET_HASH("testSecretHash");

        assertEquals("testUser", dto.getUSERNAME());
        assertEquals("testPassword", dto.getPASSWORD());
        assertEquals("testSecretHash", dto.getSECRET_HASH());
    }
}