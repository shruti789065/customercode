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
class LanguageNavigationModelTest {

    private final AemContext ctx = new AemContextBuilder(ResourceResolverType.JCR_MOCK).plugin(CORE_COMPONENTS).build();

    private LanguageNavigationI model;

    @BeforeEach
    void setUp() {


        Map p = new HashMap();
        p.put("sling:resourceSuperType","core/wcm/components/languagenavigation/v2/languagenavigation" );
        ctx.create().resource("/apps/menarinimaster/components/languagenavigation",p );

        //carico l'alberatura delle pagine
        InputStream isData = LanguageNavigationModelTest.class.getResourceAsStream("LanguageNavigationPages.json");
        ctx.load().json(isData, "/content/menarini-apac");



    }

    @Test
    @Order(1)
    void test() {

        //carico la risorsa da testare
        ctx.load().json(LanguageNavigationModelTest.class.getResourceAsStream("LanguageNavigationData.json"),"/content/experience-fragments/menarini-apac/en/site/header/master/");



        Resource r = ctx.resourceResolver().getResource("/content/experience-fragments/menarini-apac/en/site/header/master/jcr:content/root/section_navbar/wrapper/languagenavigation");
        ctx.currentResource(r);
        ctx.addModelsForClasses(LanguageNavigationI.class);
        model =  ctx.request().adaptTo(LanguageNavigationI.class);

        assertNotNull(model.getItems());


    }
}