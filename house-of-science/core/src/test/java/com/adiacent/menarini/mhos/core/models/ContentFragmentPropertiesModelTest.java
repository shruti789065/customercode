package com.adiacent.menarini.mhos.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ContentFragmentPropertiesModelTest {

    private ContentFragmentPropertiesModel model;
    @BeforeEach
    void setUp() {
        model = new ContentFragmentPropertiesModel();
        model.setCqModel("cqmodeltest");
        model.setTitle("title");
        ContentFragmentElements el = new ContentFragmentElements();
        ContentFragmentElementValue src = new ContentFragmentElementValue();
        src.setType("text");
        src.setValue(new ArrayList<>());

        el.setSource(src);
        model.setElements(el);

        model.setDescription("abstract");
    }

    @Test
    void getCqModel() {
        assertNotNull(model.getCqModel());
    }

    @Test
    void getTitle() {
        assertNotNull(model.getTitle());
    }

    @Test
    void getDescription() {
        assertNotNull(model.getDescription());
    }

    @Test
    void getElements() {
        assertFalse(model.getElements().getSource().getValue().size()>0);
    }
}