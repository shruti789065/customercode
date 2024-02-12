package com.adiacent.menarini.menarinimaster.core.models.rssnews;

import com.adiacent.menarini.menarinimaster.core.models.EnclosureModel;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class RssItemModelTest {

    @Test
    void testFull() {
        RssItemModel item = new RssItemModel();
        item.setDescription("item_descr");
        item.setGuid("guid");
        item.setCreator("author");
        item.setTitle("item_title");
        item.setPubDate(Calendar.getInstance().getTime());
        item.setLink("item_link");
        EnclosureModel enclosure = new EnclosureModel();
        enclosure.setUrl("url");
        enclosure.setType("type");
        item.setEnclosure(enclosure);
        item.setImage(new byte[0]);


        assertNotNull(item.getEnclosure());
        assertNotNull(item.getEnclosure().getUrl());
        assertNotNull(item.getEnclosure().getType());
        assertNotNull(item.getPubDate());
        assertEquals(0, item.getTitle().compareTo("item_title"));
        assertEquals(0,item.getDescription().compareTo("item_descr"));
        assertEquals(0, item.getCreator().compareTo("author"));
        assertEquals(0, item.getLink().compareTo("item_link"));
        assertEquals(0, item.getGuid().compareTo("guid"));
        assertEquals(0, item.getImage().length);
    }


}