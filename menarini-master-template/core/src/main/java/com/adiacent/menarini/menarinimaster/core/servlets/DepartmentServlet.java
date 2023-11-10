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
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.json.JsonException;
import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
			JsonArray results = new JsonArray();
			List<Resource> departmentsList = new ArrayList<>();
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

					if (queryResult.getNodes().hasNext()) {
						Node node = queryResult.getNodes().nextNode();
						Resource itemRes = resolver.getResource(node.getPath());
						if (itemRes.getResourceType().equals("dam:Asset")) {
							ContentFragment cf = itemRes.adaptTo(ContentFragment.class);
							assert cf != null;
							JsonObject cfResultsPartial = new JsonObject();
							cfResultsPartial.addProperty("title", cf.getTitle());

							cfResultsPartial.addProperty("name", cf.getName());

							JsonObject cfData = contentFragmentData(cf);
							results.add(cfResultsPartial);
							//results.add(cfData);
						}
					}

					DataSource dataSource = new SimpleDataSource(departmentsList.iterator());
					request.setAttribute(DataSource.class.getName(), dataSource);
				}
			}

		} catch (Exception e) {
			LOG.error("Error in Dropdown Department Servlet ", e);
		}
	}

	protected JsonObject contentFragmentData(ContentFragment cf) throws JsonException {
		Iterator<ContentElement> elementIterator = cf.getElements();
		JsonObject jsonObject = new JsonObject();

		while (elementIterator.hasNext()) {
			ContentElement element = elementIterator.next();
			String itemElement = element.getName();
			KeyValueItem keyValueItem = new KeyValueItem();
			String actualIndex = ModelUtils.extractIntAsString(itemElement);

			if(itemElement.contains(actualIndex)){
				if (containsKey(itemElement)) {
					keyValueItem.setKey(element.getContent());

				}
			}
			int i = 0;
			//jsonArray.add(keyValueItem);
		}

		return jsonObject;
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
