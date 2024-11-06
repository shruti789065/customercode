package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.dto.cognito.CognitoForgetPasswordDestinationDto;
import com.jakala.menarini.core.dto.cognito.CognitoForgetPasswordResponseDto;

import static org.junit.jupiter.api.Assertions.*;

class CognitoForgetPasswordResponseDtoTest {

    @Test
    void testGettersAndSetters() {
        CognitoForgetPasswordResponseDto dto = new CognitoForgetPasswordResponseDto();
        CognitoForgetPasswordDestinationDto codeDeliveryDetails = new CognitoForgetPasswordDestinationDto();

        dto.setCodeDeliveryDetails(codeDeliveryDetails);

        assertEquals(codeDeliveryDetails, dto.getCodeDeliveryDetails());
    }
}