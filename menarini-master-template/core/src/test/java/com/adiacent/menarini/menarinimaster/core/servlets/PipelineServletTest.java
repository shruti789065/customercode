package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adobe.cq.dam.cfm.ContentFragment;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PipelineServletTest {

	@Mock
	private SlingHttpServletRequest request;

	@Mock
	private SlingHttpServletResponse response;

	@Mock
	private Resource currentResource;

	@Mock
	private ResourceResolver resourceResolver;

	@Mock
	private Node currentNode;

	@Mock
	private NodeIterator nodeIterator;

	@Mock
	private Node itemNode;

	@Mock
	private Resource compoundCfRes;

	@Mock
	private ContentFragment cf;

	@InjectMocks
	private PipelineServlet pipelineServlet;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(request.getResource()).thenReturn(currentResource);
		when(currentResource.adaptTo(Node.class)).thenReturn(currentNode);
		when(request.getResourceResolver()).thenReturn(resourceResolver);
	}

	@Test
	public void testGetResult() throws Exception {
		// Mocking the behavior of currentNode and nodeIterator
		when(currentNode.hasNodes()).thenReturn(true);
		when(currentNode.getNodes()).thenReturn(nodeIterator);
		when(nodeIterator.hasNext()).thenReturn(true, false);
		when(nodeIterator.nextNode()).thenReturn(itemNode);

		// Mocking the behavior of itemNode.getProperty
		when(itemNode.getProperty(anyString())).thenReturn(mock(Property.class));
		when(itemNode.getProperty("compoundValue").getString()).thenReturn("mockedCompoundValue");
		// ... mock other property behaviors ...

		// Mocking the behavior of resourceResolver
		when(resourceResolver.getResource(anyString())).thenReturn(compoundCfRes);
		when(compoundCfRes.adaptTo(ContentFragment.class)).thenReturn(cf);
		//when(cf.getElement("value").getContent()).thenReturn("mockedCompoundElementValue");
		// ... mock other cf.getElement behaviors ...

		// Call the method to be tested
		when(cf.getElement("value")).thenReturn(null);

		JSONArray resultArray = pipelineServlet.getResult(request, currentNode, resourceResolver);


		assertEquals(1, resultArray.length());

	}

	// Add more test methods as needed
}
