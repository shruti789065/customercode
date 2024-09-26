package com.adiacent.menarini.relife.core.servlets;

import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class FullSearchResultServletTest {
    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    private MockSlingHttpServletRequest request;

    private MockSlingHttpServletResponse response;

    @Mock
    private FullSearchResultServlet servlet;


    @BeforeEach
    public void setUp() {
        ctx.load().json("/com/adiacent/menarini/relife/core/models/pages.json", "/content/relife/italy/it");
        Resource currentRes = ctx.resourceResolver().getResource("/content/relife/italy/it/search");
        ctx.currentResource(currentRes);
        ctx.addModelsForClasses(FullSearchResultServlet.class);
        response = ctx.response();
        request = spy(ctx.request());
        servlet = ctx.registerInjectActivateService(new FullSearchResultServlet());
    }

    @Test
    @Order(1)
    public void testDoGet(){
        ctx.request().setParameterMap(ImmutableMap.of("fulltext", "prova"));
        servlet.doGet(request,response);
        assertTrue(response.getBufferSize() > 0);
    }
}
