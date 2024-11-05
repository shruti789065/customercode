package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.dto.cognito.CognitoSignInErrorResponseDto;
import com.jakala.menarini.core.dto.cognito.ResetPasswordResponseDto;

import static org.junit.jupiter.api.Assertions.*;

class ResetPasswordResponseDtoTest {

    @Test
    void testGettersAndSetters() {
        ResetPasswordResponseDto dto = new ResetPasswordResponseDto();
        CognitoSignInErrorResponseDto error = new CognitoSignInErrorResponseDto();

        dto.setSuccess(true);
        dto.setError(error);

        assertTrue(dto.isSuccess());
        assertEquals(error, dto.getError());
    }
}