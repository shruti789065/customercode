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
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class DropdownServletTest {
    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);
    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;
    private DropdownServlet dropdownServlet;

    @BeforeEach
    void setUp() {
        aemContext.load().json("/com/adiacent/menarini/menarinimaster/core/models/pipeline.json", "/content/menarini-stemline/en/en/science/research/clinical-trials");
        aemContext.load().json("/com/adiacent/menarini/menarinimaster/core/models/pipelineCF.json", "/content/dam/menarini-stemline/area-content-fragments");
        Resource currentResource = aemContext.resourceResolver().getResource("/content/menarini-stemline/en/en/science/research/clinical-trials/jcr:content/root/container/container_1242765028/container_1321963744/pipeline_container/pipeline_item");
        aemContext.currentResource(currentResource);
        aemContext.addModelsForClasses(PipelineServlet.class);

        response = aemContext.response();
        request = spy(aemContext.request());

        when(request.getResource()).thenReturn(currentResource);

        dropdownServlet = aemContext.registerInjectActivateService(new DropdownServlet());
    }

    @Test
    void doGet() {
        try{

            aemContext.requestPathInfo().setSuffix("/content/menarini-stemline/en/en/science/research/clinical-trials/jcr:content/root/container/container_1242765028/container_1321963744/pipeline_container/pipeline_item");

            Resource currentResource = mock(Resource.class);
            when(request.getResource()).thenReturn(currentResource);
            when(currentResource.getResourceType()).thenReturn("/bin/menariniStemline/compoundOption");

            dropdownServlet.doGet(request,response);
            assertTrue(response.getBufferSize() > 0);

        }catch (Exception e){

        }
    }

}
