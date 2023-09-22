package com.adiacent.menarini.mhos.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ContentFragmentResponsePropertiesModelTest {


    private ContentFragmentResponsePropertiesModel model;

    @BeforeEach
    void setUp() throws Exception {
        model = new ContentFragmentResponsePropertiesModel();
        model.setCreate(true);
        model.setPath("/path-test");
        model.setTitle("Title");
        model.setParentLocation("parent_location");
        model.setStatusMessage("message");

    }
    @Test
    void testGetters() {
        assertNotNull(model.getTitle());
        assertNotNull(model.getParentLocation());
        assertNotNull(model.getPath());
        assertNotNull(model.getStatusMessage());
        assertEquals(true, model.isCreate());
    }

}