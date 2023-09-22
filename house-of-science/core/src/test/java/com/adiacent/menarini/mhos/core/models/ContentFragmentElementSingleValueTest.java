package com.adiacent.menarini.mhos.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContentFragmentElementSingleValueTest {
    ContentFragmentElementSingleValue model;

    @BeforeEach
    void setUp() throws Exception {
        model = new ContentFragmentElementSingleValue();
        model.setValue("test");
        model.setType("author");

    }
    @Test
    void testGetters() {
        assertNotNull(model.getValue());
        assertNotNull(model.getType());
       
    }
}