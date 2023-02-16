package com.adiacent.menarini.menarinimaster.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;

import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SearchResultServletTest {
    private final AemContext aemContext = new AemContext();

    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;

    private BundleContext bundleContext;

    private SearchResultServlet searchResultServlet;


    @BeforeEach
    void setUp() {
        request = aemContext.request();
        response = aemContext.response();
        bundleContext= aemContext.bundleContext();
        searchResultServlet = aemContext.registerInjectActivateService(new SearchResultServlet());
    }

    @Test
    void doGet() {
        try{
            SlingHttpServletRequest request  = mock(SlingHttpServletRequest.class);
            Resource currentResource = mock(Resource.class);
            when(request.getResource()).thenReturn(currentResource);
            ResourceResolver resourceResolver =  mock(ResourceResolver.class);
            when(currentResource.getResourceResolver()).thenReturn(resourceResolver);
            PageManager pageManager = mock(PageManager.class);
            when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
            when(currentResource.getPath()).thenReturn("/content/menarinimaster/language-masters/en/consumer-healtcare");
            Page currentPage = mock(Page.class);
            when(pageManager.getContainingPage(currentResource.getPath())).thenReturn(currentPage);

            Page homePage = mock(Page.class);
            when(currentPage.getAbsoluteParent(3)).thenReturn(homePage);
            when(homePage.getPath()).thenReturn("/content/menarinimaster/language-masters/en");

            Session session = mock(Session.class);
            when(resourceResolver.adaptTo(Session.class)).thenReturn(session);

            QueryManager queryManager = mock(QueryManager.class);
            Workspace workspace = mock(Workspace.class);

            when(session.getWorkspace()).thenReturn(workspace);
            when(workspace.getQueryManager()).thenReturn(queryManager);
            Query query = mock(Query.class);
            when(queryManager.createQuery("SELECT * FROM [cq:Page] as p WHERE ISDESCENDANTNODE('/content/menarinimaster/language-masters/en')  AND contains(p.*, '*search*' ) ORDER BY p.[jcr:content/jcr:created] DESC", Query.JCR_SQL2)).thenReturn(query);
            QueryResult result = mock(QueryResult.class);
            when(query.execute()).thenReturn(result);


            NodeIterator it = mock(NodeIterator.class);
            when(result.getNodes()).thenReturn(it);
            when(it.hasNext()).thenReturn(false);

            when(request.getParameter("fulltext")).thenReturn("search");
            searchResultServlet.doGet(request, response);

        }catch (Exception e){

        }
    }
}
