package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.dto.cognito.CognitoRefreshAuthParamDto;

import static org.junit.jupiter.api.Assertions.*;

class CognitoRefreshAuthParamDtoTest {

    @Test
    void testGettersAndSetters() {
        CognitoRefreshAuthParamDto dto = new CognitoRefreshAuthParamDto();

        dto.setREFRESH_TOKEN("refreshToken");
        dto.setSECRET_HASH("secretHash");

        assertEquals("refreshToken", dto.getREFRESH_TOKEN());
        assertEquals("secretHash", dto.getSECRET_HASH());
    }
}