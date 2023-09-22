package com.adiacent.menarini.mhos.core.servlets;

import com.adiacent.menarini.mhos.core.models.ContentFragmentModel;
import com.adiacent.menarini.mhos.core.resources.ImportLibraryResource;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagException;
import com.day.cq.tagging.TagManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

//tutorial :  //https://www.youtube.com/watch?v=g5x6F8bUHj8
@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ImportLibraryServletTest {

    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    private ImportLibraryServlet servlet;
    private MockSlingHttpServletRequest request;

    private MockSlingHttpServletResponse response;


    @Mock
    TagManager tagManager;

    @BeforeEach
    void setUp() {

        //caricamento pagine sotto /content/mhos/en
        aemContext.load().json("/com/adiacent/menarini/mhos/core/models/ImpLibraryContentData.json", "/content/mhos/en");
        //caricamento content fragment
        aemContext.load().json("/com/adiacent/menarini/mhos/core/models/ImpLibraryContentFragmentData.json", "/content/dam/mhos/content-fragments/en");
        //caricamento tags sotto  /content/cq:tags
        aemContext.load().json("/com/adiacent/menarini/mhos/core/models/ImpLibraryTagsData.json", "/content/cq:tags/house-of-science");

        aemContext.load().binaryFile("/com/adiacent/menarini/mhos/core/models/dnnImportLibraryDS.xlsx", "/content/dam/mhos/importlibrary/dnnImportLibraryDS.xlsx/jcr:content/renditions/original");
        //aemContext.load().json("/com/adiacent/menarini/mhos/core/models/ImportLibraryXLSX.json", "/content/dam/mhos/importlibrary/");

        //impostazione risorsa corrente per recupero resourceresolver
        Resource currentResource = aemContext.resourceResolver().getResource("/content/mhos/en");
        aemContext.currentResource(currentResource);

        response = spy(aemContext.response());
        request = spy(aemContext.request());


        //necessario per il caricamento della configurazione di test
        aemContext.registerInjectActivateService(new ImportLibraryResource());
        /*ImportLibraryResource.Config config = mock(ImportLibraryResource.Config.class);
        when(config.getTagsRootPath()).thenReturn("/content/cq:tags/house-of-science");
        when(config.getSourceFilePath()).thenReturn("/content/dam/mhos/importlibrary/dnnImportLibraryDS.xlsx");
        when(config.isHeaderRowPresent()).thenReturn(true);
        when(config.isImportTagEnabled()).thenReturn(true);
        when(config.isImportArticleEnabled()).thenReturn(true);
        when(config.getCategoryPath()).thenReturn("/content/dam/mhos/content-fragments/en/infectivology");
        when(config.getDamRootPath()).thenReturn("/content/dam");
        */

        //spy la servlet per poter fare l'override di alcuni metodi
        ImportLibraryServlet s = aemContext.registerService(new  ImportLibraryServlet());
        servlet = spy(s);

        InputStream is = getClass().getResourceAsStream("/com/adiacent/menarini/mhos/core/models/dnnImportLibraryDS.xlsx");
        when(servlet.getFileInputStream(any(Resource.class))).thenReturn(is);


        //gestione sessione mocckata
        Session session = mock(Session.class);
        when(servlet.getCustomSession(any(ResourceResolver.class))).thenReturn(session);
        try {
            doNothing().when(session).save();
            doNothing().when(session).move(any(String.class), any(String.class));
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }

        //gestione tagmanager mocckato
        when(servlet.getTagManager(any(ResourceResolver.class))).thenReturn(tagManager);
        Tag mockedTag = mock(Tag.class);
        try {

            lenient().when(tagManager.createTag(any(String.class),any(String.class),eq(null),eq(false))).thenAnswer(new Answer<Tag>() {
                @Override
                public Tag answer(InvocationOnMock invocation) throws Throwable {
                    return mockedTag;
                }}
            );

            lenient().when(tagManager.moveTag(any(Tag.class), any(String.class))).thenAnswer(new Answer<Tag>() {
                @Override
                public Tag answer(InvocationOnMock invocation) throws Throwable {
                    return mockedTag;
                }}
            );

        } catch (InvalidTagFormatException e) {
            throw new RuntimeException(e);
        } catch (TagException e) {
            throw new RuntimeException(e);
        }


        aemContext.addModelsForClasses(ImportLibraryServlet.class);

    }

    @Test
    @Order(1)
    public void cleanData() throws ServletException, IOException {

        ImportLibraryResource.Config config = mock(ImportLibraryResource.Config.class);
        when(config.getTagsRootPath()).thenReturn("/content/cq:tags/house-of-science");
        when(config.getSourceFilePath()).thenReturn("/content/dam/mhos/importlibrary/dnnImportLibraryDS.xlsx");
        when(config.isImportTagEnabled()).thenReturn(false);
        when(config.isImportArticleEnabled()).thenReturn(false);

        when(servlet.getCustomConfig()).thenReturn(config);
        servlet.doGet(request, response);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        ImportLibraryServlet.Response res = gson.fromJson(response.getOutputAsString(),ImportLibraryServlet.Response.class);

        assertEquals(AbstractJsonServlet.OK_RESULT, res.getResult());

    }


    @Test
    @Order(2)
    public void importTags() throws ServletException, IOException {

        ImportLibraryResource.Config config = mock(ImportLibraryResource.Config.class);
        when(config.getTagsRootPath()).thenReturn("/content/cq:tags/house-of-science");
        when(config.getSourceFilePath()).thenReturn("/content/dam/mhos/importlibrary/dnnImportLibraryDS.xlsx");
        when(config.isHeaderRowPresent()).thenReturn(true);
        when(config.isImportTagEnabled()).thenReturn(true);
        when(config.isImportArticleEnabled()).thenReturn(false);

        when(servlet.getCustomConfig()).thenReturn(config);


        Node mockedNode = mock(Node.class);
        Tag mockedTag = mock(Tag.class);
        when(mockedTag.adaptTo(Node.class)).thenReturn(mockedNode);
        try {
            lenient().when(tagManager.moveTag(any(Tag.class), any(String.class))).thenAnswer(new Answer<Tag>() {
                @Override
                public Tag answer(InvocationOnMock invocation) throws Throwable {
                    return mockedTag;
                }}
            );
        } catch (InvalidTagFormatException e) {
            throw new RuntimeException(e);
        } catch (TagException e) {
            throw new RuntimeException(e);
        }


        doAnswer(i->{
            if(i.getArgument(0)==InputStream.class){
                InputStream is =(InputStream)i.getArgument(0);
                if(is.markSupported()) {
                    is.mark(Integer.MAX_VALUE);
                    is.reset();
                }
            }
            return null;
        }).when(servlet).resetInputStream(any(InputStream.class));



        servlet.doGet(request, response);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        ImportLibraryServlet.Response res = gson.fromJson(response.getOutputAsString(),ImportLibraryServlet.Response.class);

        assertEquals(AbstractJsonServlet.OK_RESULT, res.getResult());

    }


    @Test
    @Order(3)
    public void importArticles() throws ServletException, IOException {

        ImportLibraryResource.Config config = mock(ImportLibraryResource.Config.class);
        when(config.getTagsRootPath()).thenReturn("/content/cq:tags/house-of-science");
        when(config.getSourceFilePath()).thenReturn("/content/dam/mhos/importlibrary/dnnImportLibraryDS.xlsx");
        when(config.isHeaderRowPresent()).thenReturn(true);
        when(config.isImportTagEnabled()).thenReturn(false);
        when(config.isImportArticleEnabled()).thenReturn(true);
        when(config.getCategoryPath()).thenReturn("/content/dam/mhos/content-fragments/en/infectivology");
        when(config.getDamRootPath()).thenReturn("/content/dam");

        when(servlet.getCustomConfig()).thenReturn(config);

        Node node = mock(Node.class);
        when(servlet.createNode(any(String.class), any(String.class), any(Session.class) )).thenReturn(node);

        doNothing().when(servlet).storeContentFragment(anyBoolean(), anyString(), anyInt(), anyString(),any(ContentFragmentModel.class));

        servlet.doGet(request, response);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        ImportLibraryServlet.Response res = gson.fromJson(response.getOutputAsString(),ImportLibraryServlet.Response.class);

        assertEquals(AbstractJsonServlet.OK_RESULT, res.getResult());

    }
}
