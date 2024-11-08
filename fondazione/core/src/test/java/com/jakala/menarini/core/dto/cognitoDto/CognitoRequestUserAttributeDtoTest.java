package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.dto.cognito.CognitoRequestUserAttributeDto;

import static org.junit.jupiter.api.Assertions.*;

class CognitoRequestUserAttributeDtoTest {

    @Test
    void testGettersAndSetters() {
        CognitoRequestUserAttributeDto dto = new CognitoRequestUserAttributeDto();

        dto.setName("name");
        dto.setValue("value");

        assertEquals("name", dto.getName());
        assertEquals("value", dto.getValue());
    }
}