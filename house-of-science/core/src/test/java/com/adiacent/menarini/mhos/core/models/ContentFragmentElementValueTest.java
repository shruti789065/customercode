package com.adiacent.menarini.mhos.core.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContentFragmentElementValueTest {

    private ContentFragmentElementValue model;

    @BeforeEach
    void setUp() {
        model = new ContentFragmentElementValue();
        model.setType("typology");
        List<Object> objList = new ArrayList<Object>();
        objList.add("elementString");
        objList.add(200);
        model.setValue(objList);

    }

    @Test
    void getType() {
        assertEquals("typology", model.getType());
    }



    @Test
    void getValue() {
        assertTrue(model.getValue().size()> 0);
    }

}