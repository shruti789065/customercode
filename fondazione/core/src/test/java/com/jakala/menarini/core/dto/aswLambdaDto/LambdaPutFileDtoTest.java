package com.jakala.menarini.core.dto.aswLambdaDto;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.dto.awslambda.LambdaPutFileDto;

import static org.junit.jupiter.api.Assertions.*;

class LambdaPutFileDtoTest {

    @Test
    void testUsername() {
        LambdaPutFileDto dto = new LambdaPutFileDto();
        String username = "testUser";
        dto.setUsername(username);
        assertEquals(username, dto.getUsername());
    }

    @Test
    void testImageData() {
        LambdaPutFileDto dto = new LambdaPutFileDto();
        String imageData = "image data";
        dto.setImageData(imageData);
        assertEquals(imageData, dto.getImageData());
    }
}