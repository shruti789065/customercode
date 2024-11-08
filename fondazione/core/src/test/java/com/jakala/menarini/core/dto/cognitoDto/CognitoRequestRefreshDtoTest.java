package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.dto.cognito.CognitoRefreshAuthParamDto;
import com.jakala.menarini.core.dto.cognito.CognitoRequestRefreshDto;

import static org.junit.jupiter.api.Assertions.*;

class CognitoRequestRefreshDtoTest {

    @Test
    void testGettersAndSetters() {
        CognitoRequestRefreshDto dto = new CognitoRequestRefreshDto();
        CognitoRefreshAuthParamDto authParameters = new CognitoRefreshAuthParamDto();

        dto.setClientId("clientId");
        dto.setAuthParameters(authParameters);

        assertEquals("clientId", dto.getClientId());
        assertEquals(authParameters, dto.getAuthParameters());
    }
}