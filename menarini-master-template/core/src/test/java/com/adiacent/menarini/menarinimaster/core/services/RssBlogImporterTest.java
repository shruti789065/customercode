package com.adiacent.menarini.menarinimaster.core.services;


import com.adiacent.menarini.menarinimaster.core.models.ContentFragmentM;
import com.adiacent.menarini.menarinimaster.core.models.rssblog.ChannelModel;
import com.adiacent.menarini.menarinimaster.core.models.rssblog.RssBlogModel;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentFragmentException;
import com.day.cq.commons.Externalizer;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class RssBlogImporterTest {

    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    private RssBlogImporter importer;

    @Mock
    Externalizer externalizer;

    @Mock
    Session session;

    @Mock
    TagManager tagManager;

    @Mock
    QueryBuilder qBuilder;

    @BeforeEach
    void setUp() {
        aemContext.load().json("/com/adiacent/menarini/menarinimaster/core/models/RssBlogImporterDamData.json", "/content/dam/menarini-berlinchemie");
        aemContext.load().json("/com/adiacent/menarini/menarinimaster/core/models/RssBlogImporterTagsData.json", "/content/cq:tags/menarini-berlinchemie");
        aemContext.addModelsForClasses(RssBlogImporter.class);
    }

    @Test
    void testNoBlogUrlError(){
        HashMap<String, String> configAttributes = new HashMap<String, String>();
        configAttributes.put("getRssBlogUrl","");
        importer = spy(aemContext.registerInjectActivateService(new RssBlogImporter(), configAttributes));
        importer.start();
        assertTrue(importer.getErrors().size()>0);
    }

   @Test
    void testNoBlogItemToImportError(){

        ResourceResolver resolver = spy(aemContext.resourceResolver());
        when(resolver.adaptTo(Session.class)).thenReturn(session);

        HashMap<String, String> configAttributes = new HashMap<String, String>();
        configAttributes.put("getRssBlogUrl","https://menariniblog.com/feed");
        importer = spy(aemContext.registerInjectActivateService(new RssBlogImporter(), configAttributes));
        when(importer.getResourceResolver()).thenReturn(resolver);
        when(importer.getRssBlogData()).then(new Answer<RssBlogModel>() {
            @Override
            public RssBlogModel answer(InvocationOnMock invocationOnMock) throws Throwable {
                RssBlogModel model = new RssBlogModel();
                ChannelModel channel = new ChannelModel();
                channel.setItems(new ArrayList<>());
                model.setChannel(channel);
                return model;
            }
        });
        importer.start();

        assertTrue(importer.getErrors().size()>0);
    }

    @Test
    void testNoTagRootPathError(){
        ResourceResolver resolver = spy(aemContext.resourceResolver());
        when(resolver.adaptTo(Session.class)).thenReturn(session);

        HashMap<String, String> configAttributes = new HashMap<String, String>();
        configAttributes.put("getRssBlogUrl","https://menariniblog.com/feed");
        configAttributes.put("getTagsRootPath","");
        configAttributes.put("isCategoryImportDisabled", "false");
        importer = spy(aemContext.registerInjectActivateService(new RssBlogImporter(), configAttributes));
        when(importer.getResourceResolver()).thenReturn(resolver);
        importer.start();

        assertTrue(importer.getErrors().size()>0);
    }

    @Test
    void testFullImport(){

        //override mock instance tagmanager
        Tag mockedTag = mock(Tag.class);
        try {
            lenient().when(tagManager.createTag(any(String.class),any(String.class),eq(null),eq(true))).thenAnswer(new Answer<Tag>() {
                @Override
                public Tag answer(InvocationOnMock invocation) throws Throwable {
                    return mockedTag;
                }}
            );
        } catch (InvalidTagFormatException e) {
            e.printStackTrace();
        }

        //override mock instance of externalizer
        when(externalizer.authorLink(any(ResourceResolver.class),anyString())).thenReturn("http://example-hostname.com");


        //override mock instance of  query builder
        Query query = mock(Query.class);
        SearchResult sr = mock(SearchResult.class);
        when(qBuilder.createQuery(any(PredicateGroup.class),any(Session.class))).thenReturn(query);
        when(query.getResult()).thenReturn(sr);
        List<Hit> listHits = new ArrayList<>();
        Hit hit = mock(Hit.class);
        listHits.add(hit);
        when(sr.getHits()).thenReturn(listHits);
        try {
            Resource mockResource = mock(Resource.class);
            ContentFragment mockContentFragment = aemContext.create().contentFragmentStructured("/content/dam/menarini-berlinchemie/area-content-fragments/blog-items/cf-to-delete", "main", "Hello There"); // mock(ContentFragment.class);

            when(mockResource.adaptTo(ContentFragment.class)).thenReturn(mockContentFragment);
            when(hit.getResource()).thenReturn(mockResource);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        //resource resolver
        ResourceResolver resolver = spy(aemContext.resourceResolver());
        when(resolver.adaptTo(Session.class)).thenReturn(session);
        when(resolver.adaptTo(TagManager.class)).thenReturn(tagManager);
        when(resolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(resolver.adaptTo(QueryBuilder.class)).thenReturn(qBuilder);



        HashMap<String, String> configAttributes = new HashMap<String, String>();
        configAttributes.put("isBlogImportDisabled","false");
        configAttributes.put("getBlogItemRootPath","/content/dam/menarini-berlinchemie/area-content-fragments/blog-items");
        configAttributes.put("getRssBlogUrl","https://menariniblog.com/feed");
        configAttributes.put("isCategoryImportDisabled", "false");
        configAttributes.put("isBlogItemDeletionDisabled", "false");
        configAttributes.put("getCategoryParentTag", "Menarini-berlin Blog Tag");
        configAttributes.put("getTagsRootPath","/content/cq:tags/menarini-berlinchemie/");
        configAttributes.put("getTagNamespace","menarini-berlinchemie:");
        configAttributes.put("getContentFragmentModel", "/conf/menarini-berlinchemie/settings/dam/cfm/models/blog-item");
        configAttributes.put("getDamRootPath","/content/dam");
        configAttributes.put("getImportUsername","admin");
        configAttributes.put("getImportPwd","admin");
        configAttributes.put("isDebugReportEnabled","true");
        configAttributes.put("getDebugReportSubject","[Menarini Master Template] RSS Blog Import Procedure AUTHOR DEv");
        configAttributes.put("getDebugReportRecipient","f.mancini@adiacent.com");
        importer = spy(aemContext.registerInjectActivateService(new RssBlogImporter(), configAttributes));
        doReturn(true).when(importer).storeContentFragment(anyBoolean(), eq("http://example-hostname.com"), anyString(),any(ContentFragmentM.class));
        doNothing().when(importer).deleteContentFragment(anyString(), anyString());
        when(importer.getResourceResolver()).thenReturn(resolver);
        try {
            lenient().doNothing().when(importer).generateNode(anyString(),anyString(),any(Session.class));
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        importer.start();

        assertNull(importer.getErrors());
        assertTrue(importer.getImportedItems().size()> 0);
        assertTrue(importer.getCancelledItems().size()> 0);


    }
}