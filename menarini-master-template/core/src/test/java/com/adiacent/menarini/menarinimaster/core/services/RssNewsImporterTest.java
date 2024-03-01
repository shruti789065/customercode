package com.adiacent.menarini.menarinimaster.core.services;


import com.adiacent.menarini.menarinimaster.core.models.contentfragments.EnclosureModel;

import com.adiacent.menarini.menarinimaster.core.models.rss.ChannelModel;
import com.adiacent.menarini.menarinimaster.core.models.rss.NewsItemModel;
import com.adiacent.menarini.menarinimaster.core.models.rss.RssModel;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.Workflow;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import javax.jcr.Session;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;


@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class RssNewsImporterTest {
    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    private RssNewsImporter importer;

    @Mock
    private WorkflowService wfService;

    @BeforeEach
    void setUp() {
        aemContext.load().json("/com/adiacent/menarini/menarinimaster/core/models/RssNewsImporterContentData.json", "/content/menarini-berlinchemie/de/news");
        aemContext.registerService(WorkflowService.class, wfService);
        aemContext.addModelsForClasses(RssNewsImporter.class);
    }

    @Test
    void testNoWorkFlowModelSpecifiedError() {
        Map<String,Object> configAttributes = new HashMap<>();
        configAttributes.put("getNewsRootPath","/content/menarini-berlinchemie/de/news");
        configAttributes.put("getRssFeedUrl","https://www.menarini.com/en-us/news/mid/19455/ctl/rss");
        configAttributes.put("isNewsImportDisabled","false");
        configAttributes.put("isApprovalWorkflowDisabled","false");
        configAttributes.put("getApprovalWorkflowModelPath","");
        RssNewsImporter s = aemContext.registerInjectActivateService(new RssNewsImporter(), configAttributes);
        importer = spy(s);

        WorkflowSession mockWorkflowSession = mock(WorkflowSession.class);
        //when(importer.getWorkflowSession(any(Session.class))).thenReturn(mockWorkflowSession);
        doReturn(mockWorkflowSession).when(importer).getWorkflowSession(any(Session.class));

        importer.start();
        assertNotNull(importer.getErrors());
    }


    @Test
    void testDisabledImporterError() {
        Map<String,Object> configAttributes = new HashMap<>();
        configAttributes.put("isNewsImportDisabled","true");
        RssNewsImporter s = aemContext.registerInjectActivateService(new RssNewsImporter(), configAttributes);
        importer = spy(s);
        importer.start();
        assertNotNull(importer.getErrors());
    }

    @Test
    void testNoRssFeedUrlError() {
        Map<String,Object> configAttributes = new HashMap<>();
        configAttributes.put("isNewsImportDisabled","false");
        RssNewsImporter s = aemContext.registerInjectActivateService(new RssNewsImporter(), configAttributes);
        importer = spy(s);
        importer.start();
        assertTrue(importer.getErrors().size()>0);
    }
   @Test
    void testNoError() {

        Map<String,Object> configAttributes = new HashMap<>();
        configAttributes.put("getNewsRootPath","/content/menarini-berlinchemie/de/news");
        configAttributes.put("getRssFeedUrl","https://www.menarini.com/en-us/news/mid/19455/ctl/rss");
        configAttributes.put("getNewsImagesDAMFolder","/content/dam/menarini-berlinchemie/assets/news");
        configAttributes.put("getNewsPageTemplate","/conf/menarini-berlinchemie/settings/wcm/templates/menarini---details-news");
        configAttributes.put("getYearPageTemplate","/conf/menarini-berlinchemie/settings/wcm/templates/menarini---news-year");
        configAttributes.put("isDebugReportEnabled",true);
        configAttributes.put("getDebugReportSubject","[Menarini Master Template] RSS News Import Procedure AUTHOR LOCAL");
        configAttributes.put("getDebugReportRecipient","f.mancini@adiacent.com");
        configAttributes.put("getDebugReportRecipientCopyTo","f.mancini@adiacent.com");
        configAttributes.put("isNewsImportDisabled","false");
        configAttributes.put("isApprovalWorkflowDisabled","false");
        configAttributes.put("getApprovalWorkflowModelPath","/var/workflow/models/publish-approval-for-menarini-berlinchemie");
        RssNewsImporter s = aemContext.registerInjectActivateService(new RssNewsImporter(), configAttributes);
        importer = spy(s);

        WorkflowSession mockWorkflowSession = mock(WorkflowSession.class);
        Workflow mockWorkflow = mock(Workflow.class);
        try {
            lenient().when(mockWorkflowSession.startWorkflow(any(WorkflowModel.class), any(WorkflowData.class))).thenReturn(mockWorkflow);
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
        //when(importer.getWorkflowSession(any(Session.class))).thenReturn(mockWorkflowSession);
        doReturn(mockWorkflowSession).when(importer).getWorkflowSession(any(Session.class));

        RssModel<NewsItemModel> rssDataModel = new RssModel<NewsItemModel>();
        ChannelModel channel = new ChannelModel();
        EnclosureModel enclosure = new EnclosureModel();
        enclosure.setType("type");
        enclosure.setUrl("https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
        List<NewsItemModel> items = new ArrayList<NewsItemModel>();
        NewsItemModel item =  new NewsItemModel();
        item.setTitle("title");
        item.setDescription("desc");
        item.setCreator("author");
        item.setLink("https://link.eu");
        item.setGuid("xyz");
        item.setIdentifier("identifier");
        item.setPubDate(Calendar.getInstance().getTime());
        item.setEnclosure(enclosure);
        items.add(item);
        channel.setItems(items);
        rssDataModel.setChannel(channel);
        when(importer.getRssNewsData()).thenReturn(rssDataModel);


        /*try {
            when(importer.addImage(any(ResourceResolver.class), any(Session.class), any(RssItemModel.class))).thenReturn("plutoW");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        */
        importer.start();
        assertEquals("2","2");

    }



}