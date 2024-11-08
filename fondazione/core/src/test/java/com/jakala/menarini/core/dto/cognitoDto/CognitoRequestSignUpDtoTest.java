package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.dto.cognito.CognitoRequestSignUpDto;
import com.jakala.menarini.core.dto.cognito.CognitoRequestUserAttributeDto;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CognitoRequestSignUpDtoTest {

    @Test
    void testGettersAndSetters() {
        CognitoRequestSignUpDto dto = new CognitoRequestSignUpDto();
        ArrayList<CognitoRequestUserAttributeDto> userAttributes = new ArrayList<>();

        dto.setClientId("clientId");
        dto.setUsername("username");
        dto.setPassword("password");
        dto.setSecretHash("secretHash");
        dto.setUserAttributes(userAttributes);

        assertEquals("clientId", dto.getClientId());
        assertEquals("username", dto.getUsername());
        assertEquals("password", dto.getPassword());
        assertEquals("secretHash", dto.getSecretHash());
        assertEquals(userAttributes, dto.getUserAttributes());
    }
}