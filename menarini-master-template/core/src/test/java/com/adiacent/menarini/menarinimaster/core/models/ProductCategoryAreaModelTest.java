package com.adiacent.menarini.menarinimaster.core.models;

import com.day.cq.wcm.scripting.WCMBindingsConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import java.io.InputStream;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;

@ExtendWith({AemContextExtension.class})
public class ProductCategoryAreaModelTest {
    private final AemContext ctx = new AemContextBuilder(ResourceResolverType.JCR_MOCK).build();

    @Mock
    ProductCategoryAreaModel model;


    @BeforeEach
    void setUp() {

        try {

            //settaggio pagina
            InputStream is = ProductCategoryAreaModelTest.class.getResourceAsStream("ProductCategoryPage.json");
            ctx.load().json(is, "/content/menarinimaster/en/en/categoryPage");

            //settaggio componente custom configurato nell'experience fragment
            InputStream is1 = ProductCategoryAreaModelTest.class.getResourceAsStream("ProductCategoryAreaComponent.json");
            ctx.load().json(is1, "/content/experience-fragments/menarinimaster/language-masters/en/site/product-category-area-xf/master/jcr:content/root/productcategoryarea");


            MockSlingHttpServletRequest request = spy(new MockSlingHttpServletRequest(ctx.resourceResolver(),ctx.bundleContext()));
            ResourceResolver resolver = spy(ctx.resourceResolver());

            lenient().when(request.getResourceResolver()).thenReturn(resolver);
            Resource currentRes = ctx.resourceResolver().getResource("/content/experience-fragments/menarinimaster/language-masters/en/site/product-category-area-xf/master/jcr:content/root/productcategoryarea");

            //impostazione pagina corrente
            ctx.currentPage("/content/menarinimaster/en/en/categoryPage");

            SlingBindings bindings = new SlingBindings();
            bindings.put(SlingBindings.REQUEST, request);
            bindings.put(WCMBindingsConstants.NAME_CURRENT_PAGE, ctx.currentPage());
            bindings.put(WCMBindingsConstants.NAME_COMPONENT_CONTEXT, ctx.componentContext());
            bindings.put(SlingBindings.RESOLVER, ctx.resourceResolver());
            request.setResource(currentRes);
            request.setAttribute(SlingBindings.class.getName(), bindings);
            ModelFactory modelFactory = ctx.getService(ModelFactory.class);
            model = modelFactory.createModel(request, ProductCategoryAreaModel.class);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    void countCategories() {
        assertEquals(2, model.getItems().size());
    }

}
