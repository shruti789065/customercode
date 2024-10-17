package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CognitoForgetPasswordDestinationDtoTest {

    @Test
    void testGetAndSetAttributeName() {
        CognitoForgetPasswordDestinationDto dto = new CognitoForgetPasswordDestinationDto();
        dto.setAttributeName("email");
        assertEquals("email", dto.getAttributeName());
    }

    @Test
    void testGetAndSetDeliveryMedium() {
        CognitoForgetPasswordDestinationDto dto = new CognitoForgetPasswordDestinationDto();
        dto.setDeliveryMedium("SMS");
        assertEquals("SMS", dto.getDeliveryMedium());
    }

    @Test
    void testGetAndSetDestination() {
        CognitoForgetPasswordDestinationDto dto = new CognitoForgetPasswordDestinationDto();
        dto.setDestination("example@example.com");
        assertEquals("example@example.com", dto.getDestination());
    }
}
