package com.jakala.menarini.core.dto.aswLambdaDto;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.dto.awslambda.PutImageDto;

import static org.junit.jupiter.api.Assertions.*;

class PutImageDtoTest {

    @Test
    void testImageData() {
        PutImageDto dto = new PutImageDto();
        String imageData = "image data";
        dto.setImageData(imageData);
        assertEquals(imageData, dto.getImageData());
    }
}