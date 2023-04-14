package com.adiacent.menarini.mhos.core.models;

import com.adiacent.menarini.mhos.core.utils.CustomNodeIterator;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.scripting.WCMBindingsConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import junitx.util.PrivateAccessor;
import org.apache.poi.ss.formula.FormulaShifter;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class LatestArticleCardModelTest {
    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    LatestArticleCardModel lastestArticleCardModel;
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
    void setUp() throws RepositoryException, NoSuchFieldException {


        //settaggio pagina
        InputStream is = LatestArticleCardModelTest.class.getResourceAsStream("LatestArticleData.json");
        ctx.load().json(is, "/content/mhos/en/en/scientific-library");

        //settaggio componente custom configurato nell'experience fragment
        InputStream is1 = LatestArticleCardModelTest.class.getResourceAsStream("contentFragment.json");
        ctx.load().json(is1, "/content/dam/mhos/content-fragments/en/cardiology");


        MockSlingHttpServletRequest request = spy(new MockSlingHttpServletRequest(ctx.resourceResolver(),ctx.bundleContext()));
        ResourceResolver resolver = spy(ctx.resourceResolver());

        lenient().when(request.getResourceResolver()).thenReturn(resolver);
        Resource currentRes = ctx.resourceResolver().getResource("/content/dam/mhos/content-fragments/en/cardiology");

        //impostazione pagina corrente
        ctx.currentPage("/content/mhos/en/en/scientific-library");

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
        bindings.put(SlingBindings.REQUEST, request);
        bindings.put(WCMBindingsConstants.NAME_CURRENT_PAGE, ctx.currentPage());
        bindings.put(WCMBindingsConstants.NAME_COMPONENT_CONTEXT, ctx.componentContext());
        bindings.put(SlingBindings.RESOLVER, ctx.resourceResolver());
        request.setResource(currentRes);
        request.setAttribute(SlingBindings.class.getName(), bindings);
        ModelFactory modelFactory = ctx.getService(ModelFactory.class);
        lastestArticleCardModel = modelFactory.createModel(request, LatestArticleCardModel.class);

    }

    private List<Resource> loadQueryResult(ResourceResolver resolver){
        List<Resource> list = new ArrayList<>();
        Resource resource = resolver.getResource("/content/dam/mhos/content-fragments/en/cardiology/article-1");
        list.add(resource);
        return list;
    }


    @Test
    void init() throws RepositoryException {
     lastestArticleCardModel.init();
    }

}