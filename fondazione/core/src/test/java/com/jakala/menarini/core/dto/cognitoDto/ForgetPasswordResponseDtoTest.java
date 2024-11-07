package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.dto.cognito.CognitoForgetPasswordResponseDto;
import com.jakala.menarini.core.dto.cognito.CognitoSignInErrorResponseDto;
import com.jakala.menarini.core.dto.cognito.ForgetPasswordResponseDto;

import static org.junit.jupiter.api.Assertions.*;

class ForgetPasswordResponseDtoTest {

    @Test
    void testGettersAndSetters() {
        ForgetPasswordResponseDto dto = new ForgetPasswordResponseDto();
        CognitoForgetPasswordResponseDto cognitoForgetPasswordResponseDto = new CognitoForgetPasswordResponseDto();
        CognitoSignInErrorResponseDto cognitoError = new CognitoSignInErrorResponseDto();

        dto.setSuccess(true);
        dto.setCognitoForgetPasswordResponseDto(cognitoForgetPasswordResponseDto);
        dto.setCognitoError(cognitoError);

        assertTrue(dto.isSuccess());
        assertEquals(cognitoForgetPasswordResponseDto, dto.getCognitoForgetPasswordResponseDto());
        assertEquals(cognitoError, dto.getCognitoError());
    }
}