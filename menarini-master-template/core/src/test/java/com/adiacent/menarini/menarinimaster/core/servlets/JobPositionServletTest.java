package com.adiacent.menarini.menarinimaster.core.servlets;

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
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class JobPositionServletTest {
    private final AemContext aemContext = new AemContext();

    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;

    private JobPositionServlet jobPositionServlet;

    @BeforeEach
    void setUp() {
        request = aemContext.request();
        response = aemContext.response();
        aemContext.load().json("/com/adiacent/menarini/menarinimaster/core/models/CountryServlet.json", "/content/menarini-apac/en/careers");
        Resource currentResource = aemContext.resourceResolver().getResource("/content/menarini-apac/en/careers");
        aemContext.currentResource(currentResource);
        aemContext.addModelsForClasses(JobPositionServlet.class);
        jobPositionServlet = aemContext.registerInjectActivateService(new JobPositionServlet());
    }

    @Test
    @Order(1)
    public void testDoGet() {
        aemContext.request().setParameterMap(ImmutableMap.of("country", "/content/menarini-apac/en/careers/countries/singapore"));
        jobPositionServlet.doGet(request,response);
        assertEquals(aemContext.response().getStatus(), 200);
    }


}
