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
import java.util.HashMap;
import java.util.Map;

import static com.adobe.cq.wcm.core.components.testing.mock.ContextPlugins.CORE_COMPONENTS;
import static junit.framework.Assert.assertNotNull;

@ExtendWith({AemContextExtension.class})
class NewsListModelTest {


    private final AemContext ctx = new AemContextBuilder(ResourceResolverType.JCR_MOCK).plugin(CORE_COMPONENTS).build();
    private NewsListI newsListModel = null;

    @BeforeEach
    void setUp() {

        //le seguenti istruzioni sono necessarie per aver accesso alla proprietà "delegate" del modello, ovvero alla risorsa padre, estesa dal componente
        Map p = new HashMap();
        p.put("sling:resourceSuperType","core/wcm/components/list/v3/list" );
        Resource r = ctx.create().resource("/apps/menarinimaster/components/news_list",p );

        InputStream is = MegamenuModelTest.class.getResourceAsStream("NewsListPage.json");
        ctx.load().json(is, "/content/menarini-demo/gb/en/news");
    }

    @Test
    @Order(1)
    public void testSorting(){
        Resource newsList = ctx.resourceResolver().getResource("/content/menarini-demo/gb/en/news/jcr:content/root/container/section_1876381274/wrapper/news_list");
        ctx.currentResource(newsList);
        ctx.addModelsForClasses(NewsListI.class);
        newsListModel =  ctx.request().adaptTo(NewsListI.class);
        assertNotNull(newsListModel.getListItems());

    }


}