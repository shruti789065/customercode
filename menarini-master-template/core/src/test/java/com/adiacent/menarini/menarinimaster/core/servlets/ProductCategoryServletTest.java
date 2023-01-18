package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.utils.CustomNodeIterator;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
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
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ProductCategoryServletTest {

    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

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

    @Mock
    private ProductCategoryServlet productCategoryServlet;

    private MockSlingHttpServletRequest request;

    private MockSlingHttpServletResponse response;

    @BeforeEach
    public void setUp() throws RepositoryException {

        ctx.load().json("/com/adiacent/menarini/menarinimaster/core/models/productCategoryData.json", "/conf");
        ctx.load().json("/com/adiacent/menarini/menarinimaster/core/models/productCategoryAreaXFData.json", "/content/experience-fragments/menarinimaster/language-masters/en/site/product-category-area-xf/master");

        Resource currentRes = ctx.resourceResolver().getResource("/content/experience-fragments/menarinimaster/language-masters/en/site/product-category-area-xf/master/jcr:content/root/productcategoryarea");
        ctx.currentResource(currentRes);
        ctx.addModelsForClasses(ProductCategoryServlet.class);

        response = ctx.response();
        request = spy(ctx.request());

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

        productCategoryServlet = ctx.registerInjectActivateService(new ProductCategoryServlet());

    }

    private List<Resource> loadQueryResult(ResourceResolver resolver){
        List<Resource> list = new ArrayList<>();
        Resource resource = resolver.getResource("/conf/menarinimaster/settings/wcm/templates/menarini---product-list-pharmaceutical");
        list.add(resource);
        return list;
    }

    @Test
    @Order(1)
    public void testDropdown() throws ServletException, IOException {
        request.addRequestParameter("resourceType","menarinimaster/components/productcategoryarea");
        ctx.requestPathInfo().setSuffix("/content/experience-fragments/menarinimaster/language-masters/en/site/product-category-area-xf/master");
        productCategoryServlet.doGet(request,response);
        assertTrue(response.getBufferSize() > 0);
    }
}

