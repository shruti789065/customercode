package com.adiacent.menarini.menarinimaster.core.models;


import com.adobe.cq.wcm.core.components.models.Teaser;
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
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


@ExtendWith({AemContextExtension.class})
public class TeaserModelTest {

    private final AemContext ctx = new AemContextBuilder(ResourceResolverType.JCR_MOCK).plugin(CORE_COMPONENTS).build();

    @BeforeEach
    void setUp() {

        InputStream is = TeaserModelTest.class.getResourceAsStream("dataPages.json");

        ctx.create().resource("/apps/menarinimaster/components/internalheader","sling:resourceSuperType", "core/wcm/components/teaser/v2/teaser");

        ctx.load().json(is, "/content/menarinimaster/en/en");
    }

    @Test
    @Order(1)
    public void testFirstLevelPageTitle(){

        Resource ih = ctx.resourceResolver().getResource("/content/menarinimaster/en/en/test-content-page/jcr:content/root/container/internalheader");

        ctx.currentResource(ih);

        Teaser model = ctx.request().adaptTo(TeaserI.class);

        assertNotNull(model.getTitle());
    }

    @Test
    @Order(2)
    public void testSecondLevelPageTitle(){

        Resource ih = ctx.resourceResolver().getResource("/content/menarinimaster/en/en/test-content-page/test-detail-page/jcr:content/root/container/internalheader");

        ctx.currentResource(ih);

        Teaser model = ctx.request().adaptTo(TeaserI.class);

        assertEquals(model.getTitle(),"content title IH");
    }


}
