package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.dto.cognito.CognitoSignInErrorResponseDto;

import static org.junit.jupiter.api.Assertions.*;

class CognitoSignInErrorResponseDtoTest {

    @Test
    void testGettersAndSetters() {
        CognitoSignInErrorResponseDto dto = new CognitoSignInErrorResponseDto();

        dto.set__type("type");
        dto.setMessage("message");

        assertEquals("type", dto.get__type());
        assertEquals("message", dto.getMessage());
    }
}