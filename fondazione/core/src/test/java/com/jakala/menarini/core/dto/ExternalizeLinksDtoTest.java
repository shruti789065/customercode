package com.jakala.menarini.core.dto;

import com.jakala.menarini.core.dto.enums.ExternalizeOp;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExternalizeLinksDtoTest {

    @Test
    void testGetAndSetOp() {
        ExternalizeLinksDto dto = new ExternalizeLinksDto();
        ExternalizeOp op = ExternalizeOp.SOCIAL;
        dto.setOp(op);
        assertEquals(op, dto.getOp());
    }

    @Test
    void testGetAndSetPrevLink() {
        ExternalizeLinksDto dto = new ExternalizeLinksDto();
        dto.setPrevLink("http://example.com/prev");
        assertEquals("http://example.com/prev", dto.getPrevLink());
    }

    @Test
    void testGetAndSetTargetLink() {
        ExternalizeLinksDto dto = new ExternalizeLinksDto();
        dto.setTargetLink("http://example.com/target");
        assertEquals("http://example.com/target", dto.getTargetLink());
    }

    @Test
    void testGetAndSetAbsolute() {
        ExternalizeLinksDto dto = new ExternalizeLinksDto();
        dto.setAbsolute(true);
        assertTrue(dto.getAbsolute());
    }
}