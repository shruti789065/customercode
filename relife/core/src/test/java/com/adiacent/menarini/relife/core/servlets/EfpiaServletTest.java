package com.adiacent.menarini.relife.core.servlets;

import com.google.gson.JsonArray;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.RepositoryException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class EfpiaServletTest {
    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);
    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;
    private EfpiaServlet efpiaServlet;

    @BeforeEach
    void setUp() {
        request = aemContext.request();
        response = aemContext.response();
        aemContext.load().json("/com/adiacent/menarini/relife/core/models/Efpia.json","/content/relife/corporate/en/transparency");
        aemContext.load().json("/com/adiacent/menarini/relife/core/models/Efpiadam.json", "/content/dam/efpia/relife/relife-corporate");
        Resource currentResource = aemContext.resourceResolver().getResource("/content/relife/corporate/en/transparency/jcr:content/root/container/container/section/wrapper/container/efpia");
        aemContext.currentResource(currentResource);
        aemContext.addModelsForClasses(EfpiaServlet.class);
        efpiaServlet = aemContext.registerInjectActivateService(new EfpiaServlet());
    }

    @Test
    void doGet() {
        efpiaServlet.doGet(request, response);
        assertEquals(aemContext.response().getStatus(), 200);
    }

    @Test
    void getResult() throws RepositoryException {
        ResourceResolver resolver = spy(aemContext.resourceResolver());
        MockSlingHttpServletRequest request = spy(aemContext.request());
        lenient().when(request.getResourceResolver()).thenReturn(resolver);
        JsonArray results = efpiaServlet.getResult("/content/dam/efpia/relife/relife-corporate/2022", resolver);
        assertTrue(results.size() > 0);
        Assert.assertEquals(3, results.size());
        Assert.assertEquals("/content/dam/efpia/relife/relife-corporate/2022/002.jpg", results.get(0).getAsJsonObject().get("url").getAsString());
    }

}
