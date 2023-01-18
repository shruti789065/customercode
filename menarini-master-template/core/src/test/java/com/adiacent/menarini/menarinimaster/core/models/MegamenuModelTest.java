package com.adiacent.menarini.menarinimaster.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.services.MockSlingSettingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@ExtendWith({AemContextExtension.class})
public class MegamenuModelTest {
    private final AemContext ctx = new AemContextBuilder(ResourceResolverType.JCR_MOCK).build();

    private MegamenuI megamenuModel = null;
    @BeforeEach
    void setUp() {

        InputStream is = MegamenuModelTest.class.getResourceAsStream("expFragHeaderData.json");


        ctx.load().json(is, "/content/experience-fragments/menarinimaster/language-masters/en/site/header/master");
    }


    @Test
    @Order(1)
    public void testCreateLogo(){

        MockSlingSettingService settingService = (MockSlingSettingService)ctx.getService(SlingSettingsService.class);
        Set<String> runModes = new HashSet<>();
        Collections.addAll(runModes, "author", "qa");
        settingService.setRunModes(runModes);


        Resource megamenu = ctx.resourceResolver().getResource("/content/experience-fragments/menarinimaster/language-masters/en/site/header/master/jcr:content/root/container_1861674306/megamenu");
        ctx.currentResource(megamenu);
        ctx.addModelsForClasses(MegamenuI.class);
        megamenuModel =  ctx.request().adaptTo(MegamenuI.class);
        assertNotNull(megamenuModel.getImage());
    }

    @Test
    @Order(2)
    public void testLogoFilePath(){

        MockSlingSettingService settingService = (MockSlingSettingService)ctx.getService(SlingSettingsService.class);
        Set<String> runModes = new HashSet<>();
        Collections.addAll(runModes, "author", "qa");
        settingService.setRunModes(runModes);


        Resource megamenu = ctx.resourceResolver().getResource("/content/experience-fragments/menarinimaster/language-masters/en/site/header/master/jcr:content/root/container_1861674306/megamenu");
        ctx.currentResource(megamenu);
        ctx.addModelsForClasses(MegamenuI.class);
        megamenuModel =  ctx.request().adaptTo(MegamenuI.class);
        String mmLogoFilePath = megamenuModel.getImage().get("fileReference");

        Resource logo = ctx.resourceResolver().getResource("/content/experience-fragments/menarinimaster/language-masters/en/site/header/master/jcr:content/root/container_571272791/logo");
        assertEquals(mmLogoFilePath, logo.getValueMap().get("fileReference"));
    }

}
