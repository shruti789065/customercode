package com.adiacent.menarini.guidotti.lab.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.servlethelpers.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SitemapServletTest {

    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    private SitemapServlet sitemapServlet;

    @BeforeEach
    public void setUp() throws IOException, ServletException {

        ctx.load().json("/com/adiacent/menarini/guidotti/lab/core/models/sitemap.json", "/content/laboratorio-guidotti/it");

        Resource currentResource = ctx.resourceResolver().getResource("/content/laboratorio-guidotti/it/jcr:content");
        ctx.currentResource(currentResource);
        ctx.addModelsForClasses(SitemapServlet.class);


        ((MockRequestPathInfo)ctx.request().getRequestPathInfo()).setExtension("xml");
        ((MockRequestPathInfo)ctx.request().getRequestPathInfo()).setSelectorString("sitemap");

        sitemapServlet = ctx.registerInjectActivateService(new SitemapServlet());
    }

    @Test
    @Order(1)
    public void testSitemapXml() throws ServletException, IOException {
        sitemapServlet.doGet(ctx.request(),ctx.response());
        Resource dataRes = ctx.resourceResolver().getResource("/content/laboratorio-guidotti/it");
        assertNotNull(dataRes);
    }
}
