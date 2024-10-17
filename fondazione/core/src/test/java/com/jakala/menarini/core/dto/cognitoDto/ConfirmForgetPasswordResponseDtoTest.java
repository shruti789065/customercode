package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfirmForgetPasswordResponseDtoTest {

    @Test
    void testGettersAndSetters() {
        ConfirmForgetPasswordResponseDto dto = new ConfirmForgetPasswordResponseDto();
        CognitoSignInErrorResponseDto cognitoError = new CognitoSignInErrorResponseDto();

        dto.setSuccess(true);
        dto.setCognitoError(cognitoError);

        assertTrue(dto.isSuccess());
        assertEquals(cognitoError, dto.getCognitoError());
    }
}