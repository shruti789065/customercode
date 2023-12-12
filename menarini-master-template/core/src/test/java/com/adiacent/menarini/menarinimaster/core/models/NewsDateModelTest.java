package com.adiacent.menarini.menarinimaster.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static junit.framework.Assert.assertNotNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class NewsDateModelTest {
    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    private NewsDateModel newsDateModel = null;

    @BeforeEach
    public void setUp()  {

        ctx.load().json(NewsDateModelTest.class.getResourceAsStream("NewsDetailPage.json"), "/content/menarinimaster/en/en/content-news/detail-news");

    }

    @Test
    @Order(1)
    public void testNewsDateValue(){
        Resource newsDate = ctx.resourceResolver().getResource("/content/menarinimaster/en/en/content-news/detail-news/jcr:content/root/container/container/news_data");
        ctx.currentResource(newsDate);
        ctx.addModelsForClasses(NewsDateModel.class);
        newsDateModel = ctx.request().adaptTo(NewsDateModel.class);
        assertNotNull(newsDateModel.getFormattedValue());
    }

    @Test
    @Order(2)
    public void testPublishDateValue(){
        Resource newsDetailPage = ctx.resourceResolver().getResource("/content/menarinimaster/en/en/content-news/detail-news");
        ctx.currentResource(newsDetailPage);
        ctx.addModelsForClasses(NewsDateModel.class);
        newsDateModel = ctx.request().adaptTo(NewsDateModel.class);
        assertNotNull(newsDateModel.getFormattedValue());
    }
}
