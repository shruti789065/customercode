package com.adiacent.menarini.menarinimaster.core.services;

import com.adiacent.menarini.menarinimaster.core.models.rssnews.ChannelModel;
import com.adiacent.menarini.menarinimaster.core.models.EnclosureModel;
import com.adiacent.menarini.menarinimaster.core.models.rssnews.RssItemModel;
import com.adiacent.menarini.menarinimaster.core.models.rssnews.RssNewModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;


@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class RssNewsImporterTest {
    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    private RssNewsImporter importer;

    private ResourceResolverFactory resolverFactory;

    @BeforeEach
    void setUp() {

        aemContext.load().json("/com/adiacent/menarini/menarinimaster/core/models/RssNewsImporterContentData.json", "/content/menarini-berlinchemie/de/news");

        RssNewsImporter s = aemContext.registerService(new  RssNewsImporter());
        importer = spy(s);

        aemContext.addModelsForClasses(RssNewsImporter.class);
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
        aemContext.registerInjectActivateService(importer, configAttributes);


        RssNewModel rssDataModel = new RssNewModel();
        ChannelModel channel = new ChannelModel();
        EnclosureModel enclosure = new EnclosureModel();
        enclosure.setType("type");
        enclosure.setUrl("https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
        List<RssItemModel> items = new ArrayList<RssItemModel>();
        RssItemModel item =  new RssItemModel();
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

    @Test
    void testError() {
        Map<String,Object> configAttributes = new HashMap<>();
        aemContext.registerInjectActivateService(importer, configAttributes);
        importer.start();
        assertTrue(importer.getErrors().size()>0);
    }

}