package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExternalSocialLinkResponseDtoTest {

    @Test
    void testGetAndSetRedirect() {
        ExternalSocialLinkResponseDto dto = new ExternalSocialLinkResponseDto();
        dto.setRedirect("http://example.com/redirect");
        assertEquals("http://example.com/redirect", dto.getRedirect());
    }

    @Test
    void testGetAndSetType() {
        ExternalSocialLinkResponseDto dto = new ExternalSocialLinkResponseDto();
        dto.setType("social");
        assertEquals("social", dto.getType());
    }
}