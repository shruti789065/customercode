package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.xfa.Element;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class MarketListServletTest {
    private final AemContext aemContext = new AemContext();

    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;

    private MarketListServlet marketListServlet;

    @BeforeEach
    void setUp() {
        request = aemContext.request();
        response = aemContext.response();
        aemContext.load().json("/com/adiacent/menarini/menarinimaster/core/models/marketListDropdown.json", "/content/menarini-apac/en/test");
        aemContext.load().json("/com/adiacent/menarini/menarinimaster/core/models/marketListDropdownDam.json", "/content/dam/menarini-apac/area-content-fragments/countries");
        Resource currentResource = aemContext.resourceResolver().getResource("/content/menarini-apac/en/test");
        aemContext.currentResource(currentResource);
        aemContext.addModelsForClasses(MarketListServlet.class);
        marketListServlet = aemContext.registerInjectActivateService(new MarketListServlet());
    }

    @Test
    @Order(1)
    public void testDoGet() {
        aemContext.request().setParameterMap(ImmutableMap.of("country", "/content/dam/menarini-apac/area-content-fragments/countries/india"));
        marketListServlet.doGet(request,response);
        assertEquals(aemContext.response().getStatus(), 200);
    }


}
