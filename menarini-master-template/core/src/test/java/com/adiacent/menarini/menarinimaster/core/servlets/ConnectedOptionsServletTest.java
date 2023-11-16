package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

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
		MockitoAnnotations.initMocks(this);

		when(request.getResource()).thenReturn(currentResource);
		when(currentResource.getResourceResolver()).thenReturn(resolver);
		when(currentResource.adaptTo(Node.class)).thenReturn(currentNode);

		when(resolver.adaptTo(Session.class)).thenReturn(session);
		when(session.getWorkspace()).thenReturn(workspace); // Aggiunto questo

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
		// Add additional assertions based on the expected response content
	}

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
		// Add additional assertions based on the expected behavior of the query
	}

	// Add more tests to cover other methods and scenarios

}
