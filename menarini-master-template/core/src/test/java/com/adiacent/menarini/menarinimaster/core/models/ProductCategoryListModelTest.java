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
import static junit.framework.Assert.assertEquals;


@ExtendWith({AemContextExtension.class})
class ProductCategoryListModelTest {

    private final AemContext ctx = new AemContextBuilder(ResourceResolverType.JCR_MOCK).plugin(CORE_COMPONENTS).build();
    ProductCategoryListI pcl;

    @BeforeEach
    void setUp() {

        Map p = new HashMap();
        p.put("sling:resourceSuperType","core/wcm/components/list/v3/list" );
        p.put("teaserDelegate","core/wcm/components/teaser/v2/teaser" );
        Resource r = ctx.create().resource("/apps/menarinimaster/components/productcategorylist",p );

        InputStream is = ProductCategoryListModelTest.class.getResourceAsStream("TerapeuticalPageChildrenCategory.json");
        ctx.load().json(is, "/content/menarinimaster/en/en");

    }


    @Test
    @Order(1)
    void testAllAreas() {
        Resource ih = ctx.resourceResolver().getResource("/content/menarinimaster/en/en/therapeutic-area/jcr:content/root/container/container/productcategorylist");
        ctx.currentResource(ih);
        pcl = ctx.request().adaptTo(ProductCategoryListI.class);
        assertEquals(3, pcl.getListItems().size());

    }

    @Test
    @Order(2)
    void testPharmaceuticalAreaOnly() {

        Resource ih = ctx.resourceResolver().getResource("/content/menarinimaster/en/en/pharmaceutical-area/jcr:content/root/container/container/productcategorylist");
        ctx.currentResource(ih);
        pcl = ctx.request().adaptTo(ProductCategoryListI.class);
        assertEquals(2, pcl.getListItems().size());

    }

    @Test
    @Order(3)
    void testHealthcareAreaOnly() {

        Resource ih = ctx.resourceResolver().getResource("/content/menarinimaster/en/en/healthcare-area/jcr:content/root/container/container/productcategorylist");
        ctx.currentResource(ih);
        pcl = ctx.request().adaptTo(ProductCategoryListI.class);
        assertEquals(2, pcl.getListItems().size());

    }

    @Test
    @Order(4)
    void getListItemsByStaticList() {

        InputStream is = ProductCategoryListModelTest.class.getResourceAsStream("TerapeuticalPageFixedCategory.json");
        ctx.load().json(is, "/content/menarinimaster/en/en/terapeutical-area");

        Resource ih = ctx.resourceResolver().getResource("/content/menarinimaster/en/en/terapeutical-area/jcr:content/root/container/container/productcategorylist");
        ctx.currentResource(ih);
        pcl = ctx.request().adaptTo(ProductCategoryListI.class);
        assertEquals(2,pcl.getListItems().size());
    }

}