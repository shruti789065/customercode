package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.beans.KeyValueItem;
import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Component(service = Servlet.class,immediate = true)
@SlingServletResourceTypes(
		resourceTypes = {"menarinimaster/components/form/connected-option-container"},
		methods = {HttpConstants.METHOD_GET},
		extensions = Constants.JSON,
		selectors = {ConnectedOptionsServlet.DEFAULT_SELECTOR})
@ServiceDescription("Menarini Master Template - Connected Options Servlet")
public class ConnectedOptionsServlet extends SlingAllMethodsServlet {
	private static final long serialVersionUID = 1447885216776273029L;
	public static final String DEFAULT_SELECTOR = "connectedOptions";
	private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		try {
			Resource currentResource = request.getResource();
			ResourceResolver resolver = currentResource.getResourceResolver();
			Node currentNode = currentResource.adaptTo(Node.class);
			if (currentNode != null) {
				JsonObject jsonObject = getResult(resolver, currentNode);
				response.setContentType(Constants.APPLICATION_JSON);
				response.getWriter().print(jsonObject);
			}
		} catch (Exception e) {
			logger.error("Error in Connected Options Servlet ", e);
		}
	}

	protected JsonObject getResult(ResourceResolver resolver, Node currentNode) throws Exception {
		List<JsonArray> departmentsList = new ArrayList<>();
		String departmentPagePath = currentNode.hasProperty("sourceFolder") ? currentNode.getProperty("sourceFolder").getString() : "";

		Session session = resolver.adaptTo(Session.class);
		Resource contentFragRes = resolver.getResource(departmentPagePath);

		if (contentFragRes != null) {
			String xpathQuery = buildXpathQuery(contentFragRes.getPath());
			Query query = buildQuery(session, xpathQuery);
			NodeIterator itemIterator = executeQuery(query);

			processNodes(resolver, itemIterator, departmentsList);
		}

		return mergeJsonArrays(departmentsList);
	}

	protected String buildXpathQuery(String path) {
		return "SELECT * FROM [dam:Asset] as p WHERE ISDESCENDANTNODE('" + path + "') ORDER BY p.[jcr:created] ASC";
	}

	protected Query buildQuery(Session session, String xpathQuery) throws RepositoryException {
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		return queryManager.createQuery(xpathQuery, Query.JCR_SQL2);
	}

	protected NodeIterator executeQuery(Query query) throws RepositoryException {
		QueryResult queryResult = query.execute();
		return queryResult.getNodes();
	}

	protected static void processNodes(ResourceResolver resolver, NodeIterator itemIterator, List<JsonArray> departmentsList) throws Exception {
		while (itemIterator.hasNext()) {
			Node node = itemIterator.nextNode();
			Resource itemRes = resolver.getResource(node.getPath());
			if (itemRes != null && itemRes.getResourceType().equals("dam:Asset")) {
				ContentFragment cf = itemRes.adaptTo(ContentFragment.class);
				if (cf != null) {
					JsonObject cfResultsPartial = processContentFragment(cf);
					if (cfResultsPartial != null) {
						JsonArray currentResults = new JsonArray();
						currentResults.add(cfResultsPartial);
						departmentsList.add(currentResults);
					}
				}
			}
		}
	}

	protected static JsonObject processContentFragment(ContentFragment cf) throws Exception {
		JsonObject cfResultsPartial = new JsonObject();
		cfResultsPartial.addProperty("title", cf.getTitle());
		cfResultsPartial.addProperty("name", cf.getName());
		JsonArray cfData = contentFragmentData(cf);
		if (cfData != null) {
			cfResultsPartial.add("department", cfData);
			return cfResultsPartial;
		}
		return null;
	}

	protected static JsonObject mergeJsonArrays(List<JsonArray> jsonArrays) {
		JsonObject mergedObject = new JsonObject();
		for (int i = 0; i < jsonArrays.size(); i++) {
			JsonArray jsonArray = jsonArrays.get(i);
			for (int j = 0; j < jsonArray.size(); j++) {
				JsonObject jsonObject = jsonArray.get(j).getAsJsonObject();
				mergedObject.add("fragment-" + (i), jsonObject);
			}
		}
		return mergedObject;
	}

	protected static JsonArray contentFragmentData(ContentFragment cf) throws Exception {
		Iterator<ContentElement> elementIterator = cf.getElements();
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		List<String> keyList = new ArrayList<>();
		List<String> valueList = new ArrayList<>();

		while (elementIterator.hasNext()) {
			ContentElement element = elementIterator.next();
			String itemElement = element.getName();
			String actualIndex = ModelUtils.extractIntAsString(itemElement);

			if (itemElement.contains(actualIndex)) {
				if (containsKey(itemElement)) {
					keyList.add(element.getContent());
				} else if (containsValue(itemElement)) {
					String encrypted = ModelUtils.encrypt("0123456789abcdef", "abcdefghijklmnop", element.getContent(), "AES/CBC/PKCS5PADDING");
					valueList.add(encrypted);
				}
			}
		}

		for (int i = 0; i < keyList.size(); i++) {
			String key = keyList.get(i);
			String value = valueList.get(i);
			if (key != null && !key.isEmpty() && value != null && !value.isEmpty()) {
				KeyValueItem keyValueItem = new KeyValueItem();
				keyValueItem.setKey(key);
				keyValueItem.setValue(value);
				jsonObject.addProperty(keyValueItem.getKey(), keyValueItem.getValue());
			}
		}
		jsonArray.add(jsonObject);
		return jsonArray;
	}

	protected static boolean containsKey(String s) {
		final String KEY = "key";
		return s.contains(KEY);
	}

	protected static boolean containsValue(String s) {
		final String VALUE = "value";
		return s.contains(VALUE);
	}
}
