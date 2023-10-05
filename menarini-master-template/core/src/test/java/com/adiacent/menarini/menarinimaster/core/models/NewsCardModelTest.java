package com.adiacent.menarini.menarinimaster.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.InputStream;

import static com.adobe.cq.wcm.core.components.testing.mock.ContextPlugins.CORE_COMPONENTS;
import static junit.framework.Assert.assertNotNull;

@ExtendWith({AemContextExtension.class})
public class NewsCardModelTest {

    private final AemContext ctx = new AemContextBuilder(ResourceResolverType.JCR_MOCK).plugin(CORE_COMPONENTS).build();


    @BeforeEach
    void setUp() {

        InputStream is = TeaserModelTest.class.getResourceAsStream("newsCardData.json");

        ctx.create().resource("/apps/menarinimaster/components/news_card","sling:resourceSuperType", "core/wcm/components/teaser/v2/teaser");

        ctx.load().json(is, "/content/menarinimaster/en/en/content-news-test");
    }

    @Test
    @Order(1)
    public void testNewsDateValue(){
        Resource newsCard = ctx.resourceResolver().getResource("/content/menarinimaster/en/en/content-news-test/jcr:content/root/container/container/container/news_card");
        ctx.currentResource(newsCard);
        NewsCardI newsCardI = ctx.request().adaptTo(NewsCardI.class);
        assertNotNull(newsCardI.getFormattedValue());
    }

    @Test
    @Order(2)
    public void testComponentDate(){
        Resource newsCard = ctx.resourceResolver().getResource("/content/menarinimaster/en/en/content-news-test/jcr:content/root/container/container/container/news_card_due");
        ctx.currentResource(newsCard);
        NewsCardI newsCardI = ctx.request().adaptTo(NewsCardI.class);
        assertNotNull(newsCardI.getFormattedValue());
    }
}
