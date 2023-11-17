package com.adiacent.menarini.menarinimaster.core.servlets;
import static org.mockito.Mockito.*;

import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.QueryResult;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SearchResultServletTest {

    @Mock
    private SlingHttpServletRequest request;


    @Mock
    private Resource currentResource;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private PageManager pageManager;

    @Mock
    private Page currentPage;

    @Mock
    private Page homepage;

    @Mock
    private QueryResult queryResult;

    @InjectMocks
    private SearchResultServlet servlet;

    @Test
    void testDoGet() {
        SlingHttpServletRequest request  = mock(SlingHttpServletRequest.class);
        Resource currentResource = mock(Resource.class);
        when(request.getResource()).thenReturn(currentResource);
        ResourceResolver resourceResolver =  mock(ResourceResolver.class);
        when(currentResource.getResourceResolver()).thenReturn(resourceResolver);
        PageManager pageManager = mock(PageManager.class);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(currentResource.getPath()).thenReturn("/content/menarinimaster/language-masters/en/consumer-healtcare");
        Page currentPage = mock(Page.class);

        SlingHttpServletResponse response = mock(SlingHttpServletResponse.class);
        when(request.getResource()).thenReturn(currentResource);
        when(currentResource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(anyString())).thenReturn(currentPage);
        servlet.doGet(request, response);
    }

    /*@Test
    void testGetResult() throws Exception {
        when(request.getParameter("fulltext")).thenReturn("test");
        when(resourceResolver.adaptTo(Session.class)).thenReturn(mock(Session.class));
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(anyString())).thenReturn(currentPage);
        when(queryResult.getNodes()).thenReturn(mock(NodeIterator.class));

        JSONObject result = servlet.getResult(request, resourceResolver);
        assertEquals("test", result.getJSONArray("results").toString());
    }*/
}
