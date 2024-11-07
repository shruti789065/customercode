package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.dto.cognito.CognitoAuthParametersDto;
import com.jakala.menarini.core.dto.cognito.CognitoRequestSignInDto;

import static org.junit.jupiter.api.Assertions.*;

class CognitoRequestSignInDtoTest {

    @Test
    void testGettersAndSetters() {
        CognitoRequestSignInDto dto = new CognitoRequestSignInDto();
        CognitoAuthParametersDto authParameters = new CognitoAuthParametersDto();

        dto.setClientId("clientId");
        dto.setAuthParameters(authParameters);

        assertEquals("clientId", dto.getClientId());
        assertEquals(authParameters, dto.getAuthParameters());
    }
}