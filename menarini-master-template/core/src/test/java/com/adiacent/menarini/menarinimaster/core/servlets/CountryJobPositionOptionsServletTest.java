package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.schedulers.EncodeDecodeSecretKey;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class CountryJobPositionOptionsServletTest {

    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;
    @Mock
    private CountryJobPositionOptionsServlet countryJobPositionOptionsServlet;

    private EncodeDecodeSecretKey encodeDecodeSecretKey;


    @BeforeEach
    void setUp() {
        request = aemContext.request();
        response = aemContext.response();

        aemContext.load().json("/com/adiacent/menarini/menarinimaster/core/models/CountryJobPositionOptions.json", "/content/menarini-apac/en/careers");
        Resource currentResource = aemContext.resourceResolver().getResource("/content/menarini-apac/en/careers/send-your-cv/jcr:content/root/container/container/container/connected_option_con");
        aemContext.currentResource(currentResource);
        aemContext.addModelsForClasses(JobPositionServlet.class);


        countryJobPositionOptionsServlet = aemContext.registerInjectActivateService(new CountryJobPositionOptionsServlet());
    }

    @Test
    @Order(1)
    public void testDoGet() {
        encodeDecodeSecretKey = aemContext.registerInjectActivateService(new EncodeDecodeSecretKey(),
                "getSecretKey","LYA6f1TM09aL1xMD",
                "getIvParameter", "eXRa60ZQHI0XbwJb",
                "getAlgorithm", "AES/CBC/PKCS5PADDING");
        countryJobPositionOptionsServlet.doGet(request,response);
        assertEquals(aemContext.response().getStatus(), 200);
    }

}

