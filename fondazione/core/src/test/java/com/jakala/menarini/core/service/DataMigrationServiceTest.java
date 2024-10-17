package com.jakala.menarini.core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.QueryManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.adobe.cq.dam.cfm.VariationDef;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.jakala.menarini.core.service.interfaces.FileReaderServiceInterface;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class DataMigrationServiceTest {

    private final AemContext ctx = new AemContext();

    @InjectMocks
    private DataMigrationService dataMigrationService;

    @Mock
    private ResourceResolverFactory resolverFactory;

    @Mock
    private SlingRepository slingRepository;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Session session;

    @Mock
    private Workspace workspace;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private QueryManager queryManager;

    @Mock
    private Query query;

    @Mock
    private Resource parentResource;

    @Mock
    private Resource modelResource;

    @Mock
    private FragmentTemplate template;

    @Mock
    private ContentFragment contentFragment;

    @Mock
    private AssetManager assetManager;

    @Mock
    private FileReaderServiceInterface fileReaderService;

    @Mock
    private SearchResult result;

    @BeforeEach
    void setUp() throws Exception {
        ctx.registerService(ResourceResolverFactory.class, resolverFactory, org.osgi.framework.Constants.SERVICE_RANKING, Integer.MAX_VALUE);
        ctx.registerService(ResourceResolver.class, resourceResolver, org.osgi.framework.Constants.SERVICE_RANKING, Integer.MAX_VALUE);
        when(resolverFactory.getServiceResourceResolver(any())).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getQueryManager()).thenReturn(queryManager);
    }

    void beforeData(){
        when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(queryBuilder);
        when(queryBuilder.createQuery(any(), any())).thenReturn(query);
        when(query.getResult()).thenReturn(result);
        when(result.getHits()).thenReturn(Collections.emptyList());
        when(contentFragment.getName()).thenReturn("TEST");
        when(contentFragment.listAllVariations()).thenReturn(new ArrayList<VariationDef>().iterator());
        when(contentFragment.getElements()).thenReturn(new ArrayList<ContentElement>().iterator());
    }


    /** TOPICS */
    @Test
    void testTopicsMigrateDataModelNull() {
        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(null);

        // Execute the method and verify IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            dataMigrationService.migrateData("topics", null, "false");
        });
    }

    @Test
    void testTopicsMigrateData() throws Exception {
        
        beforeData();

        // Mock CSV content
        String csvContent = "id_disciplina,name_it,name_en\n1,Prova,Test Name";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());

        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(template);

        // Mock content fragment creation
        when(template.createFragment(any(), anyString(), anyString())).thenReturn(contentFragment);
    

        when(fileReaderService.getFileAsStream(anyString())).thenReturn(inputStream);
            
        // Execute the method
        dataMigrationService.migrateData("topics", null, "false");
        
        // Verify that the content fragment was created and updated
        verify(template).createFragment(any(), anyString(), anyString());
        verify(contentFragment, atLeastOnce()).getElement(anyString());
    }

    /** SUBSCRIPTION TYPES */
    @Test
    void testSubscriptionMigrateDataModelNull() {
        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(null);

        // Execute the method and verify IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            dataMigrationService.migrateData("subscriptiontypes", null, "false");
        });
    }

    @Test
    void testSubscriptionsMigrateData() throws Exception {

        beforeData();

        // Mock CSV content
        String csvContent = "id,type_it,type_en\n1,Prova,Test Name";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());

        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(template);

        // Mock content fragment creation
        when(template.createFragment(any(), anyString(), anyString())).thenReturn(contentFragment);
    

        when(fileReaderService.getFileAsStream(anyString())).thenReturn(inputStream);
            
        // Execute the method
        dataMigrationService.migrateData("subscriptiontypes", null, "false");
        
        // Verify that the content fragment was created and updated
        verify(template).createFragment(any(), anyString(), anyString());
        verify(contentFragment, atLeastOnce()).getElement(anyString());
    }

    /** SPEAKERS */
    @Test
    void testSpeakersMigrateDataModelNull() {
        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(null);

        // Execute the method and verify IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            dataMigrationService.migrateData("speakers", null, "false");
        });
    }

    @Test
    void testSpeakersMigrateData() throws Exception {

        beforeData();

        // Mock CSV content
        String csvContent = "\"id_relatore\",\"name\",\"surname\",\"company\",\"role\",\"photo\",\"curriculum\"\n\"1\",\"Alan John\",\"Camm\",\"\",\"\",\"\",\"\"";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());

        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(template);

        // Mock content fragment creation
        when(template.createFragment(any(), anyString(), anyString())).thenReturn(contentFragment);
    

        when(fileReaderService.getFileAsStream(anyString())).thenReturn(inputStream);
            
        // Execute the method
        dataMigrationService.migrateData("speakers", null, "false");
        
        // Verify that the content fragment was created and updated
        verify(template).createFragment(any(), anyString(), anyString());
        verify(contentFragment, atLeastOnce()).getElement(anyString());
    }

    /** EVENTS */
    @Test
    void testEventsMigrateDataModelNull() {
        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(null);

        // Pass validation
        when(modelResource.hasChildren()).thenReturn(true);
        List<Resource> children = new ArrayList<>();
        children.add(parentResource);
        when(modelResource.getChildren()).thenReturn(children);
        when(parentResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);

        // Execute the method and verify IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            dataMigrationService.migrateData("events", null, "false");
        });
    }

    @Test
    void testEventsMigrateData() throws Exception {

        beforeData();

        // Mock CSV content
        String csvContent = "id_evento,title_it,title_en,startDate,endDate,description_it,description_en,city,nation,subscriptionType,venue,evidenceImageUrl,presentationDescription_it,presentationDescription_en,programCover,programPDF,address,eventType,supplier,ecm,externalSubscribeLink,subscription,format\n\"13\",\"International congress - Cardiac lung\",\"International congress - Cardiac lung\",\"1976-12-08 00:00:00.0\",\"1976-12-10 23:59:59.0\",,,\"24\",\"1\",\"2\",,,,,,,,\"event\",\"15\",,,\"none\",";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());

        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(template);

        // Pass validation
        when(modelResource.hasChildren()).thenReturn(true);
        List<Resource> children = new ArrayList<>();
        children.add(parentResource);
        when(modelResource.getChildren()).thenReturn(children);
        when(parentResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);

        // Mock content fragment creation
        when(template.createFragment(any(), anyString(), anyString())).thenReturn(contentFragment);
    

        when(fileReaderService.getFileAsStream(anyString())).thenReturn(inputStream);
            
        // Execute the method
        dataMigrationService.migrateData("events", null, "false");
        
        // Verify that the content fragment was created and updated
        verify(template).createFragment(any(), anyString(), anyString());
        verify(contentFragment, atLeastOnce()).getElement(anyString());
    }

    /** MEDIA */
    @Test
    void testMediaMigrateDataModelNull() {
        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(null);

        // Pass validation
        when(modelResource.hasChildren()).thenReturn(true);
        List<Resource> children = new ArrayList<>();
        children.add(parentResource);
        when(modelResource.getChildren()).thenReturn(children);
        when(parentResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        
        // Execute the method and verify IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            dataMigrationService.migrateData("media", null, "false");
        });
    }

    @Test
    void testMediaMigrateData() throws Exception {

        beforeData();

        // Mock CSV content
        String csvContent = "\"id\",\"event\",\"speaker\",\"order\",\"date\",\"title_it\",\"title_en\",\"description_it\",\"description_en\",\"videoCode\",\"videoPath\",\"miniaturePath\",\"type\",\"discipline\"\n\"79\",\"400\",\"246\",\"24\",\"2013-11-16 00:00:00.0\",\"\",\"\",\"Conclusions\",\"Conclusions\",\"\",\"https://vimeo.com/184367077\",\"https://i.vimeocdn.com/video/593786829_421x240.jpg\",\"VS\",\"4\"";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());

        // Pass validation
        when(modelResource.hasChildren()).thenReturn(true);
        List<Resource> children = new ArrayList<>();
        children.add(parentResource);
        when(modelResource.getChildren()).thenReturn(children);
        when(parentResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);

        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(template);

        // Mock content fragment creation
        when(template.createFragment(any(), anyString(), anyString())).thenReturn(contentFragment);
    

        when(fileReaderService.getFileAsStream(anyString())).thenReturn(inputStream);
            
        // Execute the method
        dataMigrationService.migrateData("media", null, "false");
        
        // Verify that the content fragment was created and updated
        verify(template).createFragment(any(), anyString(), anyString());
        verify(contentFragment, atLeastOnce()).getElement(anyString());
    }

    /** SUPPLIERS */
    @Test
    void testSuppliersMigrateDataModelNull() {
        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(null);

        // Execute the method and verify IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            dataMigrationService.migrateData("suppliers", null, "false");
        });
    }

    @Test
    void testSuppliersMigrateData() throws Exception {

        beforeData();

        // Mock CSV content
        String csvContent = "\"id_fornitore\",\"username\",\"company\",\"name\",\"surname\",\"email\"\n\"15\",\"testfornitore\",\"test\",\"yest\",\"gest\",\"testfornitore\"";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());

        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(template);

        // Mock content fragment creation
        when(template.createFragment(any(), anyString(), anyString())).thenReturn(contentFragment);
    

        when(fileReaderService.getFileAsStream(anyString())).thenReturn(inputStream);
            
        // Execute the method
        dataMigrationService.migrateData("suppliers", null, "false");
        
        // Verify that the content fragment was created and updated
        verify(template).createFragment(any(), anyString(), anyString());
        verify(contentFragment, atLeastOnce()).getElement(anyString());
    }

    /** CITIES */
    @Test
    void testCitiesMigrateDataModelNull() {
        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(null);

        // Pass validation
        when(modelResource.hasChildren()).thenReturn(true);
        List<Resource> children = new ArrayList<>();
        children.add(parentResource);
        when(modelResource.getChildren()).thenReturn(children);
        when(parentResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);

        // Execute the method and verify IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            dataMigrationService.migrateData("cities", null, "false");
        });
    }

    @Test
    void testCitiesMigrateData() throws Exception {

        beforeData();

        // Mock CSV content
        String csvContent = "id_citta,nation,name_it,name_en\n1,1,Prova,Test Name";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());

        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(template);

        // Pass validation
        when(modelResource.hasChildren()).thenReturn(true);
        List<Resource> children = new ArrayList<>();
        children.add(parentResource);
        when(modelResource.getChildren()).thenReturn(children);
        when(parentResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);

        // Mock content fragment creation
        when(template.createFragment(any(), anyString(), anyString())).thenReturn(contentFragment);
    

        when(fileReaderService.getFileAsStream(anyString())).thenReturn(inputStream);
            
        // Execute the method
        dataMigrationService.migrateData("cities", null, "false");
        
        // Verify that the content fragment was created and updated
        verify(template).createFragment(any(), anyString(), anyString());
        verify(contentFragment, atLeastOnce()).getElement(anyString());
    }

    /** NATIONS */
    @Test
    void testNationsMigrateDataModelNull() {
        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(null);

        // Execute the method and verify IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            dataMigrationService.migrateData("nations", null, "false");
        });
    }

    @Test
    void testNationsMigrateData() throws Exception {

        beforeData();
        
        // Mock CSV content
        String csvContent = "id_nazione,name_it,name_en\n1,Prova,Test Name";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());

        // Mock resource creation
        when(resourceResolver.getResource(anyString())).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(template);

        // Mock content fragment creation
        when(template.createFragment(any(), anyString(), anyString())).thenReturn(contentFragment);
    

        when(fileReaderService.getFileAsStream(anyString())).thenReturn(inputStream);
            
        // Execute the method
        dataMigrationService.migrateData("nations", null, "false");
        
        // Verify that the content fragment was created and updated
        verify(template).createFragment(any(), anyString(), anyString());
        verify(contentFragment, atLeastOnce()).getElement(anyString());
    }

    // @Test
    // void testLoadLinkSpeakerImages() throws Exception {
    //     // Mock existing content fragments
    //     Resource fragmentResource = mock(Resource.class);
    //     when(parentResource.listChildren()).thenReturn(Collections.singletonList(fragmentResource).iterator());
    //     when(fragmentResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);

    //     // Mock asset loading
    //     Asset asset = mock(Asset.class);
    //     when(resourceResolver.adaptTo(AssetManager.class)).thenReturn(assetManager);
    //     when(assetManager.createAsset(anyString(), any(InputStream.class), anyString(), anyBoolean())).thenReturn(asset);

    //     // Execute the method
    //     dataMigrationService.migrateData("speakerimages", null, null);

    //     // Verify that assets were created and linked to content fragments
    //     verify(assetManager).createAsset(anyString(), any(InputStream.class), anyString(), anyBoolean());
    //     verify(contentFragment).getElement("photo");
    // }

    // @Test
    // void testFindFragmentById() throws Exception {
    //     // Mock query execution
    //     Query query = mock(Query.class);
    //     QueryResult queryResult = mock(QueryResult.class);
    //     RowIterator rowIterator = mock(RowIterator.class);
    //     Row row = mock(Row.class);

    //     when(queryManager.createQuery(anyString(), anyString())).thenReturn(query);
    //     when(query.execute()).thenReturn(queryResult);
    //     when(queryResult.getRows()).thenReturn(rowIterator);
    //     when(rowIterator.hasNext()).thenReturn(true, false);
    //     when(rowIterator.nextRow()).thenReturn(row);
    //     when(row.getPath()).thenReturn("/content/dam/test/fragment");

    //     // Mock resource resolution
    //     Resource fragmentResource = mock(Resource.class);
    //     when(resourceResolver.getResource("/content/dam/test/fragment")).thenReturn(fragmentResource);
    //     when(fragmentResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);

    //     // Execute the method
    //     ContentFragment result = dataMigrationService.findFragmentByIdOld("testId", "/content/dam/test", resourceResolver);

    //     // Verify the result
    //     assertNotNull(result);
    //     assertEquals(contentFragment, result);
    // }

}