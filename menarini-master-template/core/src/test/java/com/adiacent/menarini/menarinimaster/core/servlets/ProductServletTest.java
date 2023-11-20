package com.adiacent.menarini.menarinimaster.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ProductServletTest {
    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    private ProductsServlet productsServlet;

    private MockSlingHttpServletRequest request;

    private MockSlingHttpServletResponse response;


    @BeforeEach
    void setUp() {

        aemContext.load().json("/com/adiacent/menarini/menarinimaster/core/models/products.json", "/content/menarini-stemline/en/en/product");
        Resource currentResource = aemContext.resourceResolver().getResource("/content/menarini-stemline/en/en/product/jcr:content/root/container/container/container_1517294438/product_container");
        aemContext.currentResource(currentResource);
        aemContext.addModelsForClasses(ProductsServlet.class);

        response = aemContext.response();
        request = spy(aemContext.request());

        when(request.getResource()).thenReturn(currentResource);

        productsServlet = aemContext.registerInjectActivateService(new ProductsServlet());

    }

    @Test
    void doGet()  throws ServletException, IOException {
            request.addRequestParameter("resourceType","menarinimaster/components/product-container");
            aemContext.requestPathInfo().setSuffix("/content/menarini-stemline/en/en/product/jcr:content/root/container/container/container_1517294438/product_container");
            productsServlet.doGet(request,response);
            assertTrue(response.getBufferSize() > 0);
    }

}
