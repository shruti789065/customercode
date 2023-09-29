package com.adiacent.menarini.mhos.core.services;

import com.adiacent.menarini.mhos.core.models.ContentFragmentModel;
import com.adiacent.menarini.mhos.core.resources.ImportLibraryResource;
import com.day.cq.commons.Externalizer;
import com.day.cq.mailer.MailService;
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
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.apache.sling.testing.resourceresolver.MockResourceResolverFactory;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//tutorial :  //https://www.youtube.com/watch?v=g5x6F8bUHj8
@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class LibraryImporterTest {

    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    private LibraryImporter importer;
    @Mock
    TagManager tagManager;
    @Mock
    Externalizer externalizer;

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

        //spy l'importer per poter fare l'override di alcuni metodi
        LibraryImporter s = aemContext.registerService(new  LibraryImporter());
        importer = spy(s);

        InputStream is = getClass().getResourceAsStream("/com/adiacent/menarini/mhos/core/models/dnnImportLibraryDS.xlsx");
        when(importer.getFileInputStream(any(Resource.class))).thenReturn(is);

        MailService mockedMailService = mock( MailService.class);
        when(importer.getMailService()).thenReturn(mockedMailService);

        //gestione tagmanager mocckato
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

            lenient().when(tagManager.resolve(any(String.class))).thenAnswer(new Answer<Tag>() {
                @Override
                public Tag answer(InvocationOnMock invocation) throws Throwable {
                    return mockedTag;
                }}
            );

            lenient().doNothing().when(tagManager).deleteTag(any(Tag.class));

            lenient().doNothing().when(tagManager).setTags(any(Resource.class),any(Tag[].class));

        } catch (InvalidTagFormatException e) {
            throw new RuntimeException(e);
        } catch (TagException e) {
            throw new RuntimeException(e);
        }


        //gestione sessione mocckata
        Session session = mock(Session.class);
        //when(servlet.getCustomSession(any(ResourceResolver.class))).thenReturn(session);
        /*try {
            doNothing().when(session).save();
            doNothing().when(session).move(any(String.class), any(String.class));
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }*/



        //gestione resource resolver
        ResourceResolver resolver = spy(aemContext.resourceResolver());
        when(resolver.adaptTo(Session.class)).thenReturn(session);
        when(resolver.adaptTo(TagManager.class)).thenReturn(tagManager);
        when(resolver.adaptTo( Externalizer.class)).thenReturn(externalizer);
        doNothing().when(resolver).close();
        Resource mockedResource= mock(Resource.class);
        lenient().when(resolver.getResource(eq("/content/dam/mhos/importlibrary/dnnImportLibraryDS.xlsx"))).thenReturn(mockedResource);
       /* doAnswer(i->{
            if(i.getArgument(0)==String.class){
                String path =(String)i.getArgument(0);
                if(path.compareTo("/content/dam/mhos/importlibrary/dnnImportLibraryDS.xlsx") == 0) {
                   return mockedResource;
                }
                return resolver.getResource(i.getArgument(0));
            }
            return resolver.getResource(i.getArgument(0));
        }).when(resolver).getResource(any(String.class));
*/

        when(importer.getResourceResolver()).thenReturn(resolver);

        aemContext.addModelsForClasses(LibraryImporter.class);

    }

    @Test
    @Order(1)
    public void cleanData() throws ServletException, IOException {

        ImportLibraryResource.Config config = mock(ImportLibraryResource.Config.class);
        when(config.getTagsRootPath()).thenReturn("/content/cq:tags/house-of-science");
        when(config.getSourceFilePath()).thenReturn("/content/dam/mhos/importlibrary/dnnImportLibraryDS.xlsx");
        when(config.isImportTagEnabled()).thenReturn(false);
        when(config.isImportArticleEnabled()).thenReturn(false);
        when(config.isProcedureEnabled()).thenReturn(true);
        when(config.getEmailTo()).thenReturn("test@test.it");
        when(importer.getCustomConfig()).thenReturn(config);
        importer.start();


        assertTrue(importer.getErrors() == null || importer.getErrors().size() == 0);

    }


    @Test
    @Order(2)
    public void importTags() throws ServletException, IOException {

        ImportLibraryResource.Config config = mock(ImportLibraryResource.Config.class);
        when(config.getTagsRootPath()).thenReturn("/content/cq:tags/house-of-science");
        when(config.getSourceFilePath()).thenReturn("/content/dam/mhos/importlibrary/dnnImportLibraryDS.xlsx");
        when(config.isHeaderRowPresent()).thenReturn(true);
        when(config.isImportTagEnabled()).thenReturn(true);
        when(config.isProcedureEnabled()).thenReturn(true);
        when(config.getEmailTo()).thenReturn("test@test.it");
        lenient().when(config.isImportArticleEnabled()).thenReturn(false);

        when(importer.getCustomConfig()).thenReturn(config);


        /*Node mockedNode = mock(Node.class);
        Tag mockedTag = mock(Tag.class);
        lenient().when(mockedTag.adaptTo(Node.class)).thenReturn(mockedNode);
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
        */

        lenient().doAnswer(i->{
            if(i.getArgument(0)==InputStream.class){
                InputStream is =(InputStream)i.getArgument(0);
                if(is.markSupported()) {
                    is.mark(Integer.MAX_VALUE);
                    is.reset();
                }
            }
            return null;
        }).when(importer).resetInputStream(any(InputStream.class));



        importer.start();

        assertTrue(importer.getErrors() == null || importer.getErrors().size() == 0);

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
        when(config.isProcedureEnabled()).thenReturn(true);
        when(config.getCategoryPath()).thenReturn("/content/dam/mhos/content-fragments/en/infectivology");
        when(config.getDamRootPath()).thenReturn("/content/dam");
        when(config.getEmailTo()).thenReturn("test@test.it");

        when(importer.getCustomConfig()).thenReturn(config);

        Node node = mock(Node.class);
        when(importer.createNode(any(String.class), any(String.class), any(Session.class) )).thenReturn(node);

        when(externalizer.authorLink(any(ResourceResolver.class),anyString())).thenReturn("http://example-hostname.com");
        doNothing().when(importer).storeContentFragment(anyBoolean(), eq("http://example-hostname.com"), anyString(),any(ContentFragmentModel.class));

        importer.start();

        assertFalse(importer.getErrors() == null || importer.getErrors().size() == 0);

    }
}