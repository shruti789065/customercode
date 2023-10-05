package com.adiacent.menarini.mhos.core.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImportLibraryExceptionTest {

    private ImportLibraryException model;
    @BeforeEach
    void setUp() {
        model = new ImportLibraryException("exception msg");
    }

    @Test
    void testExceòtion(){
        String r = null;
        if(r == null)
            try {
                throw model;
            } catch (ImportLibraryException e) {
                assertEquals(0, e.getMessage().compareTo("exception msg"));

            }
    }
}