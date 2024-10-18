package com.jakala.menarini.core.dto.aswLambdaDto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageProfileServiceResponseDtoTest {

    @Test
    void testSuccess() {
        ImageProfileServiceResponseDto dto = new ImageProfileServiceResponseDto();
        dto.setSuccess(true);
        assertTrue(dto.isSuccess());

        dto.setSuccess(false);
        assertFalse(dto.isSuccess());
    }

    @Test
    void testErrorMessage() {
        ImageProfileServiceResponseDto dto = new ImageProfileServiceResponseDto();
        String errorMessage = "Error occurred";
        dto.setErrorMessage(errorMessage);
        assertEquals(errorMessage, dto.getErrorMessage());
    }

    @Test
    void testImageData() {
        ImageProfileServiceResponseDto dto = new ImageProfileServiceResponseDto();
        String imageData = "image data";
        dto.setImageData(imageData);
        assertEquals(imageData, dto.getImageData());
    }
}