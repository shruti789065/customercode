package com.adiacent.menarini.menarinimaster.core.servlets;

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
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class PipelineServletTest {
	private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);
	private MockSlingHttpServletRequest request;
	private MockSlingHttpServletResponse response;

	private PipelineServlet pipelineServlet;

	@BeforeEach
	void setUp() {
		request = aemContext.request();
		response = aemContext.response();
		aemContext.load().json("/com/adiacent/menarini/menarinimaster/core/models/pipeline.json", "/content/menarini-stemline/en/en/science/research/clinical-trials");
		aemContext.load().json("/com/adiacent/menarini/menarinimaster/core/models/pipelineCF.json", "/content/dam/menarini-stemline/area-content-fragments");
		Resource currentResource = aemContext.resourceResolver().getResource("/content/menarini-stemline/en/en/science/research/clinical-trials/jcr:content/root/container/container_1242765028/container_1321963744/pipeline_container");
		aemContext.currentResource(currentResource);
		aemContext.addModelsForClasses(PipelineServlet.class);
		pipelineServlet = aemContext.registerInjectActivateService(new PipelineServlet());
	}

	@Test
	@Order(1)
	public void testDoGet() {
		aemContext.request().setParameterMap(ImmutableMap.of("type", "compound"));
		pipelineServlet.doGet(request,response);
		assertEquals(aemContext.response().getStatus(), 200);
	}
}
