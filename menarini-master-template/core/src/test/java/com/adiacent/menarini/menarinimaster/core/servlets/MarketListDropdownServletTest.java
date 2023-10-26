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
public class MarketListDropdownServletTest {

    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    private MarketListDropdownServlet marketListDropdownServlet = null;

    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;

    @BeforeEach
    public void setUp() throws RepositoryException {
        request = ctx.request();
        response = ctx.response();
        ctx.load().json("/com/adiacent/menarini/menarinimaster/core/models/marketListDropdown.json", "/content/menarini-apac/en/test");
        ctx.load().json("/com/adiacent/menarini/menarinimaster/core/models/marketListDropdownDam.json", "/content/dam/menarini-apac/area-content-fragments/countries");
        Resource currentResource = ctx.resourceResolver().getResource("/content/menarini-apac/en/test/jcr:content");
        ctx.currentResource(currentResource);
        ctx.addModelsForClasses(MarketListDropdownServlet.class);
        marketListDropdownServlet = ctx.registerInjectActivateService(new MarketListDropdownServlet());

    }

    @Test
    @Order(1)
    public void testDataSource() {
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setResourcePath("/content/menarini-apac/en/test/jcr:content/root/container/section_59866877/wrapper/countryoptions");
        marketListDropdownServlet.doGet(request,response);
        assertNotNull(ctx.request().getAttribute(DataSource.class.getName()));
    }

}
