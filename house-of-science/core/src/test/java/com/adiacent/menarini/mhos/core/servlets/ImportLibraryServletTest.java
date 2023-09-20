package com.adiacent.menarini.mhos.core.servlets;

import com.adiacent.menarini.mhos.core.resources.ImportLibraryResource;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagException;
import com.day.cq.tagging.TagManager;
import io.wcm.testing.mock.aem.MockTagManager;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ImportLibraryServletTest {

    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    private ImportLibraryServlet servlet;
    private MockSlingHttpServletRequest request;

    private MockSlingHttpServletResponse response;

    @Mock
    private Session session;

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

        session = spy( aemContext.resourceResolver().adaptTo(Session.class));
        //https://www.youtube.com/watch?v=g5x6F8bUHj8
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

        aemContext.addModelsForClasses(ImportLibraryServlet.class);
        ImportLibraryServlet s = aemContext.registerService(new  ImportLibraryServlet());
        InputStream is = getClass().getResourceAsStream("/com/adiacent/menarini/mhos/core/models/dnnImportLibraryDS.xlsx");
        //spy la servlet per poter fare l'ovverride di alcuni metodi
        servlet = spy(s);
        //when(servlet.getCustomConfig()).thenReturn(config);
        when(servlet.getFileInputStream(any(Resource.class))).thenReturn(is);


    }

    @Test
    @Order(1)
    public void cleanData() throws ServletException, IOException {

        ImportLibraryResource.Config config = mock(ImportLibraryResource.Config.class);
        when(config.getTagsRootPath()).thenReturn("/content/cq:tags/house-of-science");
        when(config.getSourceFilePath()).thenReturn("/content/dam/mhos/importlibrary/dnnImportLibraryDS.xlsx");
//        when(config.isHeaderRowPresent()).thenReturn(true);
        when(config.isImportTagEnabled()).thenReturn(false);
        when(config.isImportArticleEnabled()).thenReturn(false);
//        when(config.getCategoryPath()).thenReturn("/content/dam/mhos/content-fragments/en/infectivology");
//        when(config.getDamRootPath()).thenReturn("/content/dam");

        when(servlet.getCustomConfig()).thenReturn(config);


       Session sessione = mock(Session.class);
        when(servlet.getCustomSession()).thenReturn(sessione);
        try {
            doNothing().when(sessione).save();
            doNothing().when(sessione).move(any(String.class), any(String.class));

        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }



        TagManager tagManager = mock(TagManager.class);
        when(servlet.getTagManager()).thenReturn(tagManager);
        try {

            lenient().when(tagManager.createTag(any(String.class),any(String.class),eq(null),eq(false))).thenAnswer(new Answer<Tag>() {
                @Override
                public Tag answer(InvocationOnMock invocation) throws Throwable {
                    return mock(Tag.class);
                }}
            );

        } catch (InvalidTagFormatException e) {
            throw new RuntimeException(e);
        }

        Tag t = mock(Tag.class);
        when(t.adaptTo(Node.class)).thenReturn(mock(Node.class));
        try {
            lenient().when(tagManager.moveTag(any(Tag.class), any(String.class))).thenAnswer(new Answer<Tag>() {
                @Override
                public Tag answer(InvocationOnMock invocation) throws Throwable {
                    return t;
                }}
            );
        } catch (InvalidTagFormatException e) {
            throw new RuntimeException(e);
        } catch (TagException e) {
            throw new RuntimeException(e);
        }




        servlet.doGet(request, response);

        assertEquals("{\"result\":\"OK\"}", response.getOutputAsString());

    }


}
