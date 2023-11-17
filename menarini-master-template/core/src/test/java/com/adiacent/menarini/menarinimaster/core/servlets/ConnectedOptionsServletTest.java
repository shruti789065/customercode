package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.adiacent.menarini.menarinimaster.core.servlets.ConnectedOptionsServlet.contentFragmentData;
import static com.adiacent.menarini.menarinimaster.core.servlets.ConnectedOptionsServlet.processContentFragment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class ConnectedOptionsServletTest {

	@Mock
	private SlingHttpServletRequest request;

	@Mock
	private SlingHttpServletResponse response;

	@Mock
	private Resource currentResource;

	@Mock
	private ResourceResolver resolver;

	@Mock
	private Node currentNode;

	@Mock
	private Session session;

	@Mock
	private QueryManager queryManager;

	@Mock
	private Query query;

	@Mock
	private QueryResult queryResult;

	@Mock
	private NodeIterator nodeIterator;

	@Mock
	private Node node;

	@Mock
	private Resource itemRes;

	@Mock
	private ContentFragment cf;
	@Mock
	private Workspace workspace;

	@Mock
	private ContentElement element;

	@InjectMocks
	private ConnectedOptionsServlet connectedOptionsServlet;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		when(request.getResource()).thenReturn(currentResource);
		when(currentResource.getResourceResolver()).thenReturn(resolver);
		when(currentResource.adaptTo(Node.class)).thenReturn(currentNode);

		when(resolver.adaptTo(Session.class)).thenReturn(session);
		when(session.getWorkspace()).thenReturn(workspace);

		when(workspace.getQueryManager()).thenReturn(queryManager);
		when(queryManager.createQuery(anyString(), eq(Query.JCR_SQL2))).thenReturn(query);
		when(query.execute()).thenReturn(queryResult);
		when(queryResult.getNodes()).thenReturn(nodeIterator);
		when(nodeIterator.hasNext()).thenReturn(true, false);
		when(nodeIterator.nextNode()).thenReturn(node);

		when(resolver.getResource(anyString())).thenReturn(itemRes);
		when(itemRes.adaptTo(ContentFragment.class)).thenReturn(cf);

		when(cf.getTitle()).thenReturn("Title");
		when(cf.getName()).thenReturn("Name");

		when(cf.getElements()).thenReturn(Collections.singletonList(element).iterator());
		when(element.getName()).thenReturn("key1");
		when(element.getContent()).thenReturn("value1");
	}

	@Test
	public void testDoGet() throws Exception {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		when(response.getWriter()).thenReturn(writer);
		connectedOptionsServlet.doGet(request, response);

		writer.flush();
		String responseString = stringWriter.toString();
		assertNotNull(responseString);
	}

	@Test
	public void testExecuteQuery() throws RepositoryException {
		// Configurare il comportamento del mock di Query
		when(query.execute()).thenReturn(queryResult);

		// Configurare il comportamento del mock di QueryResult
		when(queryResult.getNodes()).thenReturn(nodeIterator);

		// Eseguire il test
		NodeIterator result = connectedOptionsServlet.executeQuery(query);

		// Verificare che il risultato non sia nullo
		assertNotNull(result);
	}


	/*@Test
	public void testProcessNodes() throws Exception {
		// Configurare il comportamento dei mock
		when(nodeIterator.hasNext()).thenReturn(true, false);
		when(nodeIterator.nextNode()).thenReturn(node);
		when(resolver.getResource(node.getPath())).thenReturn(itemRes);
		when(itemRes.getResourceType()).thenReturn("dam:Asset");
		when(itemRes.adaptTo(ContentFragment.class)).thenReturn(cf);

		JsonObject cfResultsPartial = new JsonObject();
		cfResultsPartial.addProperty("title", "Test Title");
		cfResultsPartial.addProperty("name", "Test Name");

		//when(processContentFragment(cf)).thenReturn(cfResultsPartial);

		// Chiamare il metodo da testare
		List<JsonArray> departmentsList = new ArrayList<>();
		ConnectedOptionsServlet.processNodes(resolver, nodeIterator, departmentsList);

		// Verificare il risultato atteso
		assertEquals(1, departmentsList.size());

		JsonArray currentResults = departmentsList.get(0);
		assertNotNull(currentResults);
		assertEquals(1, currentResults.size());

		JsonObject result = currentResults.get(0).getAsJsonObject();
		assertNotNull(result);
		assertEquals("Test Title", result.get("title").getAsString());
		assertEquals("Test Name", result.get("name").getAsString());
	}

	@Test
	public void testProcessContentFragment() throws Exception {
		// Configurare il comportamento del mock di ContentFragment
		when(cf.getTitle()).thenReturn("Test Title");
		when(cf.getName()).thenReturn("Test Name");

		// Chiamare il metodo da testare
		JsonObject cfResultsPartial = new JsonObject();
		JsonArray cfData = contentFragmentData(cf);
		if (cfData != null) {
			cfResultsPartial.add("department", cfData);
		}
		JsonObject result = processContentFragment(cf);

		// Verificare il risultato atteso
		assertNotNull(result);
		assertEquals("Test Title", result.get("title").getAsString());
		assertEquals("Test Name", result.get("name").getAsString());

	}*/

	@Test
	public void testMergeJsonArrays() {
		List<JsonArray> jsonArrays = new ArrayList<>();
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonObject);
		jsonArrays.add(jsonArray);
		JsonObject result = ConnectedOptionsServlet.mergeJsonArrays(jsonArrays);
	}

	/*@Test
	public void testContentFragmentData() throws Exception {
		// Configurare il comportamento del mock di ContentFragment
		Iterator<ContentElement> elementIterator = mock(Iterator.class);
		when(cf.getElements()).thenReturn(elementIterator);

		ContentElement contentElement1 = mock(ContentElement.class);
		when(contentElement1.getName()).thenReturn("key_1");
		when(contentElement1.getContent()).thenReturn("value_1");

		ContentElement contentElement2 = mock(ContentElement.class);
		when(contentElement2.getName()).thenReturn("key_2");
		when(contentElement2.getContent()).thenReturn("value_2");

		when(elementIterator.hasNext()).thenReturn(true, true, false);
		when(elementIterator.next()).thenReturn(contentElement1, contentElement2);

		// Chiamare il metodo da testare
		JsonArray result = contentFragmentData(cf);

		// Verificare il risultato atteso
		assertNotNull(result);
		assertEquals(1, result.size());

		JsonObject jsonObject = result.get(0).getAsJsonObject();
		assertNotNull(jsonObject);
		assertEquals("value_1", jsonObject.get("key_1").getAsString());
		assertEquals("value_2", jsonObject.get("key_2").getAsString());
	}*/

	@Test
	public void testBuildXpathQuery() {
		String path = "/content/dam/assets";
		String xpathQuery = connectedOptionsServlet.buildXpathQuery(path);
		assertEquals("SELECT * FROM [dam:Asset] as p WHERE ISDESCENDANTNODE('/content/dam/assets') ORDER BY p.[jcr:created] ASC", xpathQuery);
	}

	@Test
	public void testBuildQuery() throws Exception {
		Query query = connectedOptionsServlet.buildQuery(session, "SELECT * FROM [dam:Asset]");
		assertNotNull(query);
	}
}
