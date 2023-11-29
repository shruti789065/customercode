package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.RepositoryException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class CountryServletTest {

    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    private CountryServlet countryServlet = null;

    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;

    @BeforeEach
    public void setUp() throws RepositoryException {
        request = ctx.request();
        response = ctx.response();
        ctx.load().json("/com/adiacent/menarini/menarinimaster/core/models/CountryServlet.json", "/content/menarini-apac/en/careers");
        Resource currentResource = ctx.resourceResolver().getResource("/content/menarini-apac/en/careers/jcr:content");
        ctx.currentResource(currentResource);
        ctx.addModelsForClasses(CountryServlet.class);
        countryServlet = ctx.registerInjectActivateService(new CountryServlet());

    }

    @Test
    @Order(1)
    public void testDataSource() {
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setResourcePath("/content/menarini-apac/en/careers/jcr:content/root/container/section_452009300/wrapper/container/countryoptions");
        countryServlet.doGet(request,response);
        assertNotNull(ctx.request().getAttribute(DataSource.class.getName()));
    }

}
