package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.beans.KeyValueItem;
import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.json.JsonException;
import javax.servlet.Servlet;
import java.util.*;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES;


@Component(service = Servlet.class, name = "Menarini Master Template - Dropdown Departments Servlet", property = {
		SLING_SERVLET_RESOURCE_TYPES + "=bin/apac/department/dropdown"
}, immediate = true)

public class DepartmentServlet extends SlingAllMethodsServlet {
	private static final long serialVersionUID = 1447885216776273029L;

	private transient final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		try {
			// set fallback
			request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());

			ResourceResolver resolver = request.getResourceResolver();
			PageManager pageManager = resolver.adaptTo(PageManager.class);

			List<JsonArray> departmentsList = new ArrayList<>();
			if (pageManager != null) {
				String dropdownPath = request.getRequestPathInfo().getResourcePath();
				Resource dropdownResource = resolver.getResource(dropdownPath);
				assert dropdownResource != null;
				String departmentPagePath = dropdownResource.getValueMap().containsKey("parentPagePath") ? dropdownResource.getValueMap().get("parentPagePath").toString() : "";

				Session session = resolver.adaptTo(Session.class);
				StringBuilder myXpathQuery;
				Resource contentFragRes = null;
				contentFragRes = resolver.getResource(departmentPagePath);

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
						if (itemRes.getResourceType().equals("dam:Asset")) {
							ContentFragment cf = itemRes.adaptTo(ContentFragment.class);
							assert cf != null;
							JsonObject cfResultsPartial = new JsonObject();
							cfResultsPartial.addProperty("title", cf.getTitle());
							cfResultsPartial.addProperty("name", cf.getName());
							JsonArray cfData = contentFragmentData(cf);
							cfResultsPartial.add("department", cfData);

							JsonArray currentResults = new JsonArray();
							currentResults.add(cfResultsPartial);
							departmentsList.add(currentResults);
						}
					}
					int i = 0;
					//List<Resource> departmentResources = convertJsonArrayToResources(departmentsList, contentFragRes, resolver);
					//DataSource dataSource = new SimpleDataSource(departmentResources.iterator());
					//request.setAttribute(DataSource.class.getName(), dataSource);
				}
			}

		} catch (Exception e) {
			LOG.error("Error in Dropdown Department Servlet ", e);
		}
	}

	protected JsonArray contentFragmentData(ContentFragment cf) throws JsonException {
		Iterator<ContentElement> elementIterator = cf.getElements();
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		List<String> keyList = new ArrayList<String>();
		List<String> valueList = new ArrayList<String>();

		while (elementIterator.hasNext()) {
			ContentElement element = elementIterator.next();
			String itemElement = element.getName();
			String actualIndex = ModelUtils.extractIntAsString(itemElement);

			if(itemElement.contains(actualIndex)){
				if (containsKey(itemElement)) {
					keyList.add(element.getContent());
				} else if (containsValue(itemElement)) {
					valueList.add(element.getContent());
				}
			}
		}

		for(int i = 0; i < keyList.size(); i++) {
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

	public static List<Resource> convertJsonArrayToResources(JsonArray jsonArray, Resource parentResource, ResourceResolver resolver) throws PersistenceException, PersistenceException {
		List<Resource> resourceList = new ArrayList<>();

		for (JsonElement jsonElement : jsonArray) {
			if (jsonElement.isJsonObject()) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();


				String departmentName = jsonObject.get("name").getAsString();

				Map<String, Object> properties = new HashMap<>();
				for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
					properties.put(entry.getKey(), entry.getValue().getAsString());
				}

				Resource departmentResource = resolver.create(parentResource, departmentName, properties);

				JsonArray departmentArray = jsonObject.getAsJsonArray("department");
				List<Resource> departmentElementResources = convertJsonArrayToResources(departmentArray, departmentResource, resolver);
				resourceList.add(departmentResource);
				resourceList.addAll(departmentElementResources);
			}
		}

		return resourceList;
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
