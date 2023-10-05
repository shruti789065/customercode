package com.adiacent.menarini.menarinimaster.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;

import java.util.Iterator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class DropdownServletTest {
    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);
    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;
    private BundleContext bundleContext;
    private DropdownServlet dropdownServlet;

    @BeforeEach
    void setUp() {
        request = aemContext.request();
        response = aemContext.response();
        bundleContext= aemContext.bundleContext();
        dropdownServlet = aemContext.registerInjectActivateService(new DropdownServlet());
    }

    @Test
    void doGet() {
        try{
            SlingHttpServletRequest request  = mock(SlingHttpServletRequest.class);

            RequestPathInfo rpi = mock(RequestPathInfo.class);
            Mockito.when(request.getRequestPathInfo()).thenReturn(rpi);
            Mockito.when(rpi.getSuffix()).thenReturn("/content/menarini-stemline/en/en/research/pipeline/jcr:content/root/container/container/container_835588614/pipeline_container.pipeline.json?type=compound");

            Resource currentResource = mock(Resource.class);
            when(request.getResource()).thenReturn(currentResource);
            when(currentResource.getResourceType()).thenReturn("/bin/menariniStemline/compoundOption");

            ResourceResolver resolver = mock(ResourceResolver.class);
            Resource resource = mock(Resource.class);
            when(request.getResourceResolver()).thenReturn(resolver);
            when(resolver.getResource("/content/dam/menarini-stemline/area-content-fragments/compound-dropdown-options")).thenReturn(resource);

            Iterator<Resource> iterator = Mockito.mock(Iterator.class);
            Mockito.when(resource.listChildren()).thenReturn(iterator);
            Mockito.when(iterator.hasNext()).thenReturn(false);
            dropdownServlet.doGet(request, response);

        }catch (Exception e){

        }
    }

}
