package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExternalizeLinkResponseDtoTest {

    @Test
    void testGetAndSetLink() {
        ExternalizeLinkResponseDto dto = new ExternalizeLinkResponseDto();
        dto.setLink("http://example.com");
        assertEquals("http://example.com", dto.getLink());
    }
}