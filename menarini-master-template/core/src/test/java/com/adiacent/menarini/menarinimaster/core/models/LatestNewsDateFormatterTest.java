package com.adiacent.menarini.menarinimaster.core.models;

import com.adiacent.menarini.menarinimaster.core.utils.CustomNodeIterator;
import com.day.cq.wcm.scripting.WCMBindingsConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class LatestNewsDateFormatterTest {
    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    private LatestNewsDateFormatter latestNewsDateFormatter = null;

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

        ctx.load().json(LatestNewsDateFormatterTest.class.getResourceAsStream("LatestNewsDateData.json"), "/content/menarinimaster/en/en");

        Resource currentRes = ctx.resourceResolver().getResource("/content/menarinimaster/en/en/jcr:content/root/container/container/latestnews");
        ctx.currentResource(currentRes);
        ctx.addModelsForClasses(LatestNewsDateFormatter.class);

        MockSlingHttpServletRequest request = spy(new MockSlingHttpServletRequest(ctx.resourceResolver(),ctx.bundleContext()));

        ResourceResolver resolver = spy(ctx.resourceResolver());
        session = spy(ctx.resourceResolver().adaptTo(Session.class));
        lenient().when(request.getResourceResolver()).thenReturn(resolver);
        doAnswer(i->{
            if(i.getArgument(0)==Session.class){
                return session;
            }else{
                return ctx.resourceResolver().adaptTo( i.getArgument(0));
            }
        }).when(resolver).adaptTo(any());
        doReturn(workspace).when(session).getWorkspace();
        doReturn(queryManager).when(workspace).getQueryManager();

        doReturn(query).when(queryManager).createQuery(anyString(), eq(Query.JCR_SQL2));
        when(query.execute()).thenReturn(queryResult);
        when(queryResult.getNodes()).thenReturn(new CustomNodeIterator(loadQueryResult(ctx.resourceResolver())));

        SlingBindings bindings = new SlingBindings();
        bindings.put(SlingBindings.RESOURCE, currentRes);
        bindings.put(SlingBindings.REQUEST, request);
        bindings.put(WCMBindingsConstants.NAME_CURRENT_PAGE, ctx.currentPage());
        bindings.put(WCMBindingsConstants.NAME_COMPONENT_CONTEXT, ctx.componentContext());
        bindings.put(SlingBindings.RESOLVER, ctx.resourceResolver());
        bindings.put("item", "/content/menarinimaster/en/en/content-news-test/news-details-test");
        request.setResource(currentRes);
        request.setAttribute(SlingBindings.class.getName(), bindings);
        ModelFactory modelFactory = ctx.getService(ModelFactory.class);
        latestNewsDateFormatter = modelFactory.createModel(request, LatestNewsDateFormatter.class);
    }

    private List<Resource> loadQueryResult(ResourceResolver resolver){
        List<Resource> list = new ArrayList<>();
        Resource resource = resolver.getResource("/content/menarinimaster/en/en/content-news-test/news-details-test/jcr:content/root/container/container/news_data");
        list.add(resource);
        return list;
    }

    @Test
    void testGetFormattedValue() {
        assertNotNull(latestNewsDateFormatter.getFormattedValue());
    }
}
