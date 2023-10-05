package com.adiacent.menarini.mhos.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ContentFragmentResponseModelTest {

    private ContentFragmentResponseModel model;

    @BeforeEach
    void setUp() {
        model = new ContentFragmentResponseModel();
        model.setProperties(null);
        model.setClassName(new ArrayList<>());
    }

    @Test
    void getClassName() {
        assertEquals(0, model.getClassName().size());
    }

    @Test
    void getProperties() {
        assertNull(model.getProperties());
    }
}