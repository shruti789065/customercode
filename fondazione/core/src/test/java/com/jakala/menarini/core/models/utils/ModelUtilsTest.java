package com.jakala.menarini.core.models.utils;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.JsonObject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Locale;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ModelUtilsTest {

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private PageManager pageManager;

    @Mock
    private Page page;

    @Mock
    private Node node;

    @Mock
    private Session session;

    @Mock
    private TagManager tagManager;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private Query query;

    @Mock
    private SearchResult searchResult;

    @Mock
    private Hit hit;

    @Mock
    private Property property;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetNodeName() {
        String input = "Test Label (with special characters)";
        String expected = "test-label-with-special-characters";
        assertEquals(expected, ModelUtils.getNodeName(input));
    }

    @Test
    void testCreatePage() throws Exception {
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.create(anyString(), anyString(), anyString(), anyString())).thenReturn(page);
        when(page.adaptTo(Node.class)).thenReturn(node);
        when(node.addNode(JcrConstants.JCR_CONTENT,  "cq:PageContent")).thenReturn(node);
        Page result = ModelUtils.createPage(resourceResolver, session, "/content/test", "test-page", "Test Page", "template", "resourceType");
        assertNotNull(result);
        verify(pageManager).create("/content/test", "test-page", "template", "Test Page");
    }

    @Test
    void testFindPageByParentTemplate() {
        when(page.getParent()).thenReturn(page);
        when(page.getContentResource()).thenReturn(mock(Resource.class));
        when(page.getContentResource().adaptTo(Node.class)).thenReturn(node);
        try {
            when(node.hasProperty(anyString())).thenReturn(true);
            when(node.getProperty(anyString())).thenReturn(property);
            when(property.getString()).thenReturn("templateName");
        } catch (RepositoryException e) {
            fail("RepositoryException should not occur");
        }

        Page result = ModelUtils.findPageByParentTemplate(page, "templateName");
        assertNotNull(result);
    }

    @Test
    void testFindChildComponentByResourceType() {
        Resource resource = mock(Resource.class);
        Node node = mock(Node.class);
        try {
            when(node.getProperty(Constants.RESOURCE_TYPE_PROPERTY)).thenReturn(property);
        } catch (PathNotFoundException e) {
            fail("Page not found");
        } catch (RepositoryException e) {
            fail("RepositoryException should not occur");
        }
        when(resource.adaptTo(Node.class)).thenReturn(node);

        Resource result = ModelUtils.findChildComponentByResourceType(resource, "componentType");
        assertNull(result);
    }

    @Test
    void testGetResourceLocale() {
        when(page.getLanguage(false)).thenReturn(new Locale("en", "US"));
        Locale result = ModelUtils.getResourceLocale(page);
        assertEquals(new Locale("en", "US"), result);
    }

    @Test
    void testEncryptDecrypt() throws Exception {
        String key = "1234567890123456";
        String iv = "1234567890123456";
        String value = "HelloWorld";
        String transformation = "AES/CBC/PKCS5Padding";

        String encrypted = ModelUtils.encrypt(key, iv, value, transformation);
        assertNotNull(encrypted);

        String decrypted = ModelUtils.decrypt(key, iv, encrypted, transformation);
        assertEquals(value, decrypted);
    }

    @Test
    void testIsValidEmailFormat() {
        assertTrue(ModelUtils.isValidEmailFormat("test@example.com"));
        assertFalse(ModelUtils.isValidEmailFormat("invalid-email"));
    }

    @Test
    void testExtractIntAsString() {
        String input = "abc123def456";
        String expected = "123456";
        assertEquals(expected, ModelUtils.extractIntAsString(input));
    }

    @Test
    void testConvertJsonObjectToMap() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("key1", "value1");
        jsonObject.addProperty("key2", "value2");

        Map<String, Object> result = ModelUtils.convertJsonObjectToMap(jsonObject);
        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }
}
