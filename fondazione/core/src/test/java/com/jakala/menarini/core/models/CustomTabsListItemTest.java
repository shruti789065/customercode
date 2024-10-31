package com.jakala.menarini.core.models;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomTabsListItemTest {

    @Mock
    private ListItem wrappedListItem;

    private CustomTabsListItem customTabsListItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customTabsListItem = new CustomTabsListItem(wrappedListItem);
    }

    @Test
    void testGetTitle() {
        when(wrappedListItem.getTitle()).thenReturn("Title");
        assertEquals("Title", customTabsListItem.getTitle());
    }

    @Test
    void testGetDescription() {
        when(wrappedListItem.getDescription()).thenReturn("Description");
        assertEquals("Description", customTabsListItem.getDescription());
    }

    @Test
    void testGetURL() {
        when(wrappedListItem.getURL()).thenReturn("http://example.com");
        assertEquals("http://example.com", customTabsListItem.getURL());
    }

    @Test
    void testGetId() {
        when(wrappedListItem.getId()).thenReturn("123");
        assertEquals("megamenu-123", customTabsListItem.getId());
    }

    @Test
    void testGetLink() {
        Link link = mock(Link.class);
        when(wrappedListItem.getLink()).thenReturn(link);
        assertEquals(link, customTabsListItem.getLink());
    }

    @Test
    void testGetLastModified() {
        Calendar calendar = Calendar.getInstance();
        when(wrappedListItem.getLastModified()).thenReturn(calendar);
        assertEquals(calendar, customTabsListItem.getLastModified());
    }

    @Test
    void testGetPath() {
        when(wrappedListItem.getPath()).thenReturn("/content/path");
        assertEquals("/content/path", customTabsListItem.getPath());
    }

    @Test
    void testGetName() {
        when(wrappedListItem.getName()).thenReturn("name");
        assertEquals("name", customTabsListItem.getName());
    }

    @Test
    void testGetTeaserResource() {
        Resource resource = mock(Resource.class);
        when(wrappedListItem.getTeaserResource()).thenReturn(resource);
        assertEquals(resource, customTabsListItem.getTeaserResource());
    }

    @Test
    void testGetData() {
        ComponentData componentData = mock(ComponentData.class);
        when(wrappedListItem.getData()).thenReturn(componentData);
        assertEquals(componentData, customTabsListItem.getData());
    }
}