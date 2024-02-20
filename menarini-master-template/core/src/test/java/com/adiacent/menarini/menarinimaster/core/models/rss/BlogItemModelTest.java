package com.adiacent.menarini.menarinimaster.core.models.rss;

import com.adiacent.menarini.menarinimaster.core.models.EnclosureModel;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class BlogItemModelTest {
    @Test
    void testFull() {
        BlogItemModel item = new BlogItemModel();
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
        item.setCategory("category");
        item.setCommentRss("comment");
        item.setContent("content");

        assertNotNull(item.getEnclosure());
        assertNotNull(item.getEnclosure().getUrl());
        assertNotNull(item.getEnclosure().getType());
        assertNotNull(item.getPubDate());
        assertEquals(0, item.getTitle().compareTo("item_title"));
        assertEquals(0,item.getDescription().compareTo("item_descr"));
        assertEquals(0, item.getCreator().compareTo("author"));
        assertEquals(0, item.getLink().compareTo("item_link"));
        assertEquals(0, item.getGuid().compareTo("guid"));
        assertEquals(1, item.getCategories().size());
        assertNotNull(item.getContent());
        assertNotNull(item.getCommentRss());
    }
}