package com.adiacent.menarini.menarinimaster.core.servlets;

import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SearchResultServletTest {
    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    private MockSlingHttpServletRequest request;

    private MockSlingHttpServletResponse response;

    @Mock
    private SearchResultServlet servlet;

    @Mock
    private QueryManager queryManager;

    @Mock
    private Query query;

    @Mock
    private QueryResult queryResult;

    @Mock
    private Workspace workspace;

    @Mock
    private Session session;


    @BeforeEach
    public void setUp() throws RepositoryException {
        ctx.load().json("/com/adiacent/menarini/menarinimaster/core/models/pages.json", "/content/menarini-berlinchemie/de");
        Resource currentRes = ctx.resourceResolver().getResource("/content/menarini-berlinchemie/de/search");
        ctx.currentResource(currentRes);
        ctx.addModelsForClasses(SearchResultServlet.class);
        response = ctx.response();
        request = spy(ctx.request());
        servlet = ctx.registerInjectActivateService(new SearchResultServlet());
    }

    @Test
    @Order(1)
    public void testDoGet(){
        ctx.request().setParameterMap(ImmutableMap.of("fulltext", "test"));
        servlet.doGet(request,response);
        assertTrue(response.getBufferSize() > 0);
    }

    /*@Test
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
    }*/

}
