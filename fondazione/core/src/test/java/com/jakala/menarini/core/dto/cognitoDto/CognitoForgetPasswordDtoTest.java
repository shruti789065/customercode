package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CognitoForgetPasswordDtoTest {

    @Test
    void testGettersAndSetters() {
        CognitoForgetPasswordDto dto = new CognitoForgetPasswordDto();

        dto.setUsername("testUsername");
        assertEquals("testUsername", dto.getUsername());

        dto.setClientId("testClientId");
        assertEquals("testClientId", dto.getClientId());

        dto.setSecretHash("testSecretHash");
        assertEquals("testSecretHash", dto.getSecretHash());
    }
}