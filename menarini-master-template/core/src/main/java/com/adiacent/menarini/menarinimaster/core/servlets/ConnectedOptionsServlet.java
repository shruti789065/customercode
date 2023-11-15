package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.beans.KeyValueItem;
import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.constants.NameConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
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
import javax.json.JsonException;
import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES;


@Component(service = Servlet.class,immediate = true)
@SlingServletResourceTypes(
		resourceTypes = SLING_SERVLET_RESOURCE_TYPES + "=menarinimaster/components/form/connected-option-container",
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
			PageManager pageManager = resolver.adaptTo(PageManager.class);

			if (pageManager != null) {
				Page currentPage = pageManager.getContainingPage(currentResource.getPath());
				if (currentPage != null) {
					JsonObject jsonObject = getResult(request, resolver);
					response.setContentType(Constants.APPLICATION_JSON);
					response.getWriter().print(jsonObject);
				}
			}

		} catch (Exception e) {
			logger.error("Error in Connected Options Servlet ", e);
		}
	}

	protected JsonObject getResult(SlingHttpServletRequest request, ResourceResolver resolver) throws RepositoryException, JsonException {
		List<JsonArray> departmentsList = new ArrayList<>();
		String dropdownPath = request.getRequestPathInfo().getResourcePath();

		Resource dropdownResource = resolver.resolve(dropdownPath);
		String resourceType= "menarinimaster/components/form/connected-option-container";
		String departmentPagePath = getComponentProperty(dropdownResource.getPath(),resourceType,"sourceFolder", resolver);

		Session session = resolver.adaptTo(Session.class);
		StringBuilder myXpathQuery;
		Resource contentFragRes = resolver.getResource(departmentPagePath);

		if (contentFragRes != null) {
			myXpathQuery = new StringBuilder();
			myXpathQuery.append("SELECT * FROM [dam:Asset] as p ");
			myXpathQuery.append("WHERE ISDESCENDANTNODE('").append(contentFragRes.getPath()).append("') ");
			myXpathQuery.append(" ORDER BY p.[jcr:created] ASC ");
			assert session != null;

			QueryManager queryManager = session.getWorkspace().getQueryManager();
			Query query = queryManager.createQuery(myXpathQuery.toString(), Query.JCR_SQL2);
			QueryResult queryResult = query.execute();
			NodeIterator item = queryResult.getNodes();
			while (item.hasNext()) {
				Node node = item.nextNode();
				Resource itemRes = resolver.getResource(node.getPath());
				assert itemRes != null;
				if (itemRes.getResourceType().equals("dam:Asset")) {
					ContentFragment cf = itemRes.adaptTo(ContentFragment.class);
					assert cf != null;
					JsonObject cfResultsPartial = new JsonObject();
					cfResultsPartial.addProperty("title", cf.getTitle());
					cfResultsPartial.addProperty("name", cf.getName());
					JsonArray cfData = contentFragmentData(cf);
					if (cfData != null) {
						cfResultsPartial.add("department", cfData);
					}

					JsonArray currentResults = new JsonArray();
					currentResults.add(cfResultsPartial);
					departmentsList.add(currentResults);
				}
			}
		}
		return mergeJsonArrays(departmentsList);
	}

	private static JsonObject mergeJsonArrays(List<JsonArray> jsonArrays) {
		JsonObject mergedObject = new JsonObject();
		// Itera attraverso ogni JsonArray nella lista
		for (int i = 0; i < jsonArrays.size(); i++) {
			JsonArray jsonArray = jsonArrays.get(i);
			// Aggiungi ogni elemento del JsonArray all'oggetto principale
			for (int j = 0; j < jsonArray.size(); j++) {
				JsonObject jsonObject = jsonArray.get(j).getAsJsonObject();
				mergedObject.add("fragment-"+ (i), jsonObject);
			}
		}
		return mergedObject;
	}

	public static String getComponentProperty(String pagePath, String resourceType, String propertyName,ResourceResolver resolver) {
		String propertyValue = "";

		// Ottieni il Resource della pagina
		Resource pageResource = resolver.getResource(pagePath);
		if (pageResource != null) {
			// Cerca il componente nella pagina con il resource type specificato
			Resource componentResource = findComponentByResourceType(pageResource, resourceType);
			if (componentResource != null) {
				// Ottieni il ValueMap del componente e recupera il valore della proprietà
				ValueMap valueMap = componentResource.getValueMap();
				propertyValue = valueMap.get(propertyName, String.class);
			}
		}

		return propertyValue;
	}


	private static Resource findComponentByResourceType(Resource parentResource, String resourceType) {
		if (parentResource.isResourceType(resourceType)) {
			return parentResource;
		}

		for (Resource childResource : parentResource.getChildren()) {
			Resource foundResource = findComponentByResourceType(childResource, resourceType);
			if (foundResource != null) {
				return foundResource;
			}
		}

		return null;
	}

	protected JsonArray contentFragmentData(ContentFragment cf) throws JsonException {
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
				if (Boolean.TRUE.equals(containsKey(itemElement))) {
					keyList.add(element.getContent());
				} else if (Boolean.TRUE.equals(containsValue(itemElement))) {
					valueList.add(element.getContent());
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

	protected Boolean containsKey(String s) {
		final String KEY = "key";
		return s.contains(KEY);
	}

	protected Boolean containsValue(String s) {
		final String VALUE = "value";
		return s.contains(VALUE);
	}
}
