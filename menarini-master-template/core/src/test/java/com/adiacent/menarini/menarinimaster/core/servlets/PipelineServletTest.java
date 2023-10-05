package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adobe.cq.dam.cfm.ContentFragment;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.io.PrintWriter;
import java.io.StringWriter;


@RunWith(MockitoJUnitRunner.class)
public class PipelineServletTest {

	@Mock
	private SlingHttpServletRequest slingHttpServletRequest;

	@Mock
	private SlingHttpServletResponse slingHttpServletResponse;

	@Mock
	private Resource currentResource;

	@Mock
	private Node currentNode;

	@Mock
	private ResourceResolver resourceResolver;

	@Mock
	private ContentFragment contentFragment;

	@InjectMocks
	private PipelineServlet pipelineServlet;

	@Before
	public void setup() {
		Mockito.when(slingHttpServletRequest.getResource()).thenReturn(currentResource);
		Mockito.when(currentResource.adaptTo(Node.class)).thenReturn(currentNode);
		Mockito.when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
	}

	@Test
	public void testDoGetHandlesException() throws Exception {
		// Arrange
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		Mockito.when(slingHttpServletResponse.getWriter()).thenReturn(printWriter);

		// Act
		pipelineServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);

		// Assert
		// No exception is thrown
	}

	@Test
	public void testGetResultReturnsMissingParametersWhenTypeIsNull() throws Exception {
		// Arrange
		Mockito.when(slingHttpServletRequest.getParameter("type")).thenReturn(null);

		// Act
		JSONArray results = pipelineServlet.getResult(slingHttpServletRequest, currentNode, resourceResolver);

		// Assert
		Assert.assertEquals(1, results.length());
		Assert.assertEquals("Missing parameters!, required type", results.get(0));
	}

	@Test
	public void testGetResultReturnsCorrectResultsWhenTypeIsCompound() throws Exception {
		// Arrange
		NodeIterator containerNode = Mockito.mock(NodeIterator.class);
		Mockito.when(currentNode.hasNodes()).thenReturn(true);
		Mockito.when(currentNode.getNodes()).thenReturn(containerNode);

		Node itemNode1 = Mockito.mock(Node.class);
		Node itemNode2 = Mockito.mock(Node.class);
		Mockito.when(containerNode.hasNext()).thenReturn(true, true, false);
		Mockito.when(containerNode.nextNode()).thenReturn(itemNode1, itemNode2);

		Resource compoundCfRes1 = Mockito.mock(Resource.class);
		Resource compoundCfRes2 = Mockito.mock(Resource.class);
		Mockito.when(resourceResolver.getResource(Mockito.anyString())).thenReturn(compoundCfRes1, compoundCfRes2);

		Mockito.when(compoundCfRes1.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
		Mockito.when(compoundCfRes2.adaptTo(ContentFragment.class)).thenReturn(contentFragment);

		Mockito.when(itemNode1.getProperty("compoundValue").getString()).thenReturn("path1");
		Mockito.when(itemNode2.getProperty("compoundValue").getString()).thenReturn("path2");

		Mockito.when(contentFragment.getElement("value").getContent()).thenReturn("compoundValue1", "compoundValue2");
		Mockito.when(itemNode1.hasProperty("mechanismOfAction")).thenReturn(true);
		Mockito.when(itemNode2.hasProperty("mechanismOfAction")).thenReturn(false);
		Mockito.when(itemNode1.getProperty("mechanismOfAction").getString()).thenReturn("mechanismOfActionValue1");
		Mockito.when(itemNode1.getProperty("indicationValue").getString()).thenReturn("indicationValue1");
		Mockito.when(itemNode2.getProperty("indicationValue").getString()).thenReturn("indicationValue2");
		Mockito.when(itemNode1.hasProperty("enableStage1")).thenReturn(true);
		Mockito.when(itemNode2.hasProperty("enableStage1")).thenReturn(false);
		Mockito.when(itemNode1.getProperty("enableStage1").getBoolean()).thenReturn(true);
		Mockito.when(itemNode1.hasProperty("enableStage2")).thenReturn(true);
		Mockito.when(itemNode2.hasProperty("enableStage2")).thenReturn(false);
		Mockito.when(itemNode1.getProperty("enableStage2").getBoolean()).thenReturn(true);
		Mockito.when(itemNode1.hasProperty("labeClinicalTrials")).thenReturn(true);
		Mockito.when(itemNode2.hasProperty("labeClinicalTrials")).thenReturn(false);
		Mockito.when(itemNode1.getProperty("labeClinicalTrials").getString()).thenReturn("labelClinicalTrialsValue1");
		Mockito.when(itemNode1.getProperty("target").getString()).thenReturn("targetValue1");
		Mockito.when(itemNode1.getProperty("clinicalTrials").getString()).thenReturn("clinicalTrialsValue1");
		Mockito.when(itemNode1.hasProperty("readMore")).thenReturn(true);
		Mockito.when(itemNode2.hasProperty("readMore")).thenReturn(false);
		Mockito.when(itemNode1.getProperty("readMore").getString()).thenReturn("readMoreValue1");
		Mockito.when(itemNode1.getProperty("statusType").getString()).thenReturn("statusTypeValue1");
		Mockito.when(contentFragment.getElement("description").getContent()).thenReturn("compoundDescription1", "compoundDescription2");

		Mockito.when(contentFragment.getElement("value").getContent()).thenReturn("compoundValue1", "compoundValue2");

		Mockito.when(contentFragment.getElement("description").getContent()).thenReturn("compoundDescription1", "compoundDescription2");

		// Act
		JSONArray results = pipelineServlet.getResult(slingHttpServletRequest, currentNode, resourceResolver);

		// Assert
		Assert.assertEquals(2, results.length());
		JSONObject result1 = results.getJSONObject(0);
		JSONObject result2 = results.getJSONObject(1);
		Assert.assertEquals("compoundValue1", result1.get("compound"));
		Assert.assertEquals("mechanismOfActionValue1", result1.get("mechanismofaction"));
		Assert.assertEquals("indicationValue1", result1.get("indication"));
		Assert.assertEquals(true, result1.get("enablestage1"));
		Assert.assertEquals(true, result1.get("enablestage2"));
		Assert.assertEquals(true, result1.get("enablestage3"));
		Assert.assertEquals(true, result1.get("enablestage4"));
		Assert.assertEquals(true, result1.get("enablestage5"));
		Assert.assertEquals(true, result1.get("enablestage6"));
		Assert.assertEquals(true, result1.get("enablestage7"));
		Assert.assertEquals("labelClinicalTrialsValue1", result1.get("labelclinicaltrials"));
		Assert.assertEquals("targetValue1", result1.get("targetclinicaltrials"));
		Assert.assertEquals("clinicalTrialsValue1", result1.get("clinicaltrials"));
		Assert.assertEquals("readMoreValue1", result1.get("readmore"));
		Assert.assertEquals("statusTypeValue1", result1.get("status"));
		Assert.assertEquals("compoundDescription1", result1.get("description"));

		Assert.assertEquals("compoundValue2", result2.get("compound"));
		Assert.assertEquals("", result2.get("mechanismofaction"));
		Assert.assertEquals("indicationValue2", result2.get("indication"));
		Assert.assertEquals(false, result2.get("enablestage1"));
		Assert.assertEquals(false, result2.get("enablestage2"));
		Assert.assertEquals(false, result2.get("enablestage3"));
		Assert.assertEquals(false, result2.get("enablestage4"));
		Assert.assertEquals(false, result2.get("enablestage5"));
		Assert.assertEquals(false, result2.get("enablestage6"));
		Assert.assertEquals(false, result2.get("enablestage7"));
		Assert.assertEquals("", result2.get("labelclinicaltrials"));
		Assert.assertEquals("", result2.get("targetclinicaltrials"));
		Assert.assertEquals("", result2.get("clinicaltrials"));
		Assert.assertEquals("", result2.get("readmore"));
		Assert.assertEquals("", result2.get("status"));
		Assert.assertEquals("compoundDescription2", result2.get("description"));
	}

	@Test
	public void testGetResultReturnsCorrectResultsWhenTypeIsIndication() throws Exception {
		// Arrange
		Mockito.when(slingHttpServletRequest.getParameter("type")).thenReturn("indication");

		NodeIterator containerNode = Mockito.mock(NodeIterator.class);
		Mockito.when(currentNode.hasNodes()).thenReturn(true);
		Mockito.when(currentNode.getNodes()).thenReturn(containerNode);

		Node itemNode1 = Mockito.mock(Node.class);
		Node itemNode2 = Mockito.mock(Node.class);
		Mockito.when(containerNode.hasNext()).thenReturn(true, true, false);
		Mockito.when(containerNode.nextNode()).thenReturn(itemNode1, itemNode2);

		Resource compoundCfRes1 = Mockito.mock(Resource.class);
		Resource compoundCfRes2 = Mockito.mock(Resource.class);
		Mockito.when(resourceResolver.getResource(Mockito.anyString())).thenReturn(compoundCfRes1, compoundCfRes2);

		Mockito.when(compoundCfRes1.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
		Mockito.when(compoundCfRes2.adaptTo(ContentFragment.class)).thenReturn(contentFragment);

		Mockito.when(itemNode1.getProperty("compoundValue").getString()).thenReturn("path1");
		Mockito.when(itemNode2.getProperty("compoundValue").getString()).thenReturn("path2");

		Mockito.when(contentFragment.getElement("value").getContent()).thenReturn("compoundValue1", "compoundValue2");
		Mockito.when(itemNode1.hasProperty("mechanismOfAction")).thenReturn(true);
		Mockito.when(itemNode2.hasProperty("mechanismOfAction")).thenReturn(false);
		Mockito.when(itemNode1.getProperty("mechanismOfAction").getString()).thenReturn("mechanismOfActionValue1");
		Mockito.when(itemNode1.getProperty("indicationValue").getString()).thenReturn("indicationValue1");
		Mockito.when(itemNode2.getProperty("indicationValue").getString()).thenReturn("indicationValue2");
		Mockito.when(itemNode1.hasProperty("enableStage1")).thenReturn(true);
		Mockito.when(itemNode2.hasProperty("enableStage1")).thenReturn(false);
		Mockito.when(itemNode1.getProperty("enableStage1").getBoolean()).thenReturn(true);
		Mockito.when(itemNode1.hasProperty("enableStage2")).thenReturn(true);
		Mockito.when(itemNode2.hasProperty("enableStage2")).thenReturn(false);
		Mockito.when(itemNode1.getProperty("enableStage2").getBoolean()).thenReturn(true);
		Mockito.when(itemNode1.hasProperty("labeClinicalTrials")).thenReturn(true);
		Mockito.when(itemNode2.hasProperty("labeClinicalTrials")).thenReturn(false);
		Mockito.when(itemNode1.getProperty("labeClinicalTrials").getString()).thenReturn("labelClinicalTrialsValue1");
		Mockito.when(itemNode1.getProperty("target").getString()).thenReturn("targetValue1");
		Mockito.when(itemNode1.getProperty("clinicalTrials").getString()).thenReturn("clinicalTrialsValue1");
		Mockito.when(itemNode1.hasProperty("readMore")).thenReturn(true);
		Mockito.when(itemNode2.hasProperty("readMore")).thenReturn(false);
		Mockito.when(itemNode1.getProperty("readMore").getString()).thenReturn("readMoreValue1");
		Mockito.when(itemNode1.getProperty("statusType").getString()).thenReturn("statusTypeValue1");
		Mockito.when(contentFragment.getElement("description").getContent()).thenReturn("compoundDescription1", "compoundDescription2");

		Mockito.when(contentFragment.getElement("value").getContent()).thenReturn("compoundValue1", "compoundValue2");

		Mockito.when(contentFragment.getElement("description").getContent()).thenReturn("compoundDescription1", "compoundDescription2");

		// Act
		JSONArray results = pipelineServlet.getResult(slingHttpServletRequest, currentNode, resourceResolver);

		// Assert
		Assert.assertEquals(2, results.length());
		JSONObject result1 = results.getJSONObject(0);
		JSONObject result2 = results.getJSONObject(1);
		Assert.assertEquals("", result1.get("compound"));
		Assert.assertEquals("mechanismOfActionValue1", result1.get("mechanismofaction"));
		Assert.assertEquals("indicationValue1", result1.get("indication"));
		Assert.assertEquals(true, result1.get("enablestage1"));
		Assert.assertEquals(true, result1.get("enablestage2"));
		Assert.assertEquals(true, result1.get("enablestage3"));
		Assert.assertEquals(true, result1.get("enablestage4"));
		Assert.assertEquals(true, result1.get("enablestage5"));
		Assert.assertEquals(true, result1.get("enablestage6"));
		Assert.assertEquals(true, result1.get("enablestage7"));
		Assert.assertEquals("labelClinicalTrialsValue1", result1.get("labelclinicaltrials"));
		Assert.assertEquals("targetValue1", result1.get("targetclinicaltrials"));
		Assert.assertEquals("clinicalTrialsValue1", result1.get("clinicaltrials"));
		Assert.assertEquals("readMoreValue1", result1.get("readmore"));
		Assert.assertEquals("statusTypeValue1", result1.get("status"));
		Assert.assertEquals("compoundDescription1", result1.get("description"));

		Assert.assertEquals("", result2.get("compound"));
		Assert.assertEquals("", result2.get("mechanismofaction"));
		Assert.assertEquals("indicationValue2", result2.get("indication"));
		Assert.assertEquals(false, result2.get("enablestage1"));
		Assert.assertEquals(false, result2.get("enablestage2"));
		Assert.assertEquals(false, result2.get("enablestage3"));
		Assert.assertEquals(false, result2.get("enablestage4"));
		Assert.assertEquals(false, result2.get("enablestage5"));
		Assert.assertEquals(false, result2.get("enablestage6"));
		Assert.assertEquals(false, result2.get("enablestage7"));
		Assert.assertEquals("", result2.get("labelclinicaltrials"));
		Assert.assertEquals("", result2.get("targetclinicaltrials"));
		Assert.assertEquals("", result2.get("clinicaltrials"));
		Assert.assertEquals("", result2.get("readmore"));
		Assert.assertEquals("", result2.get("status"));
		Assert.assertEquals("compoundDescription2", result2.get("description"));
	}

	// Add more tests to cover other scenarios and edge cases in the PipelineServlet class

}
