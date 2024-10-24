package com.jakala.menarini.core.dto.aswLambdaDto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LambdaGetFileDtoTest {

    @Test
    void testUsername() {
        LambdaGetFileDto dto = new LambdaGetFileDto();
        String username = "testUser";
        dto.setEmail(username);
        assertEquals(username, dto.getEmail());
    }
}