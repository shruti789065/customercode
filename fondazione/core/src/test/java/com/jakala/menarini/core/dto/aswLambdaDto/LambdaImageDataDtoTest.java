package com.jakala.menarini.core.dto.aswLambdaDto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LambdaImageDataDtoTest {

    @Test
    void testType() {
        LambdaImageDataDto dto = new LambdaImageDataDto();
        String type = "image/png";
        dto.setType(type);
        assertEquals(type, dto.getType());
    }

    @Test
    void testData() {
        LambdaImageDataDto dto = new LambdaImageDataDto();
        byte[] data = {1, 2, 3};
        dto.setData(data);
        assertArrayEquals(data, dto.getData());
    }
}