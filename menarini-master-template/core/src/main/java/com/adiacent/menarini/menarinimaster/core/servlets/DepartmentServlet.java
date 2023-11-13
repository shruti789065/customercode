package com.adiacent.menarini.menarinimaster.core.servlets;

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
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;
import java.util.Iterator;

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
			JsonArray departmentsList = new JsonArray();
			if (pageManager != null) {
				String dropdownPath = request.getRequestPathInfo().getResourcePath();
				Resource dropdownResource = resolver.getResource(dropdownPath);
				assert dropdownResource != null;
				String departmentPagePath = dropdownResource.getValueMap().containsKey("parentPagePath") ?
						dropdownResource.getValueMap().get("parentPagePath").toString() : "";

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
							cfResultsPartial.add("department", cfData);
							departmentsList.add(cfResultsPartial);
						}
					}

					// Creare un DataSource direttamente dal JsonArray
					DataSource dataSource = new SimpleDataSource(departmentsList.iterator());
					request.setAttribute(DataSource.class.getName(), dataSource);
				}
			}

		} catch (Exception e) {
			LOG.error("Error in Dropdown Department Servlet ", e);
		}
	}

	protected JsonArray contentFragmentData(ContentFragment cf) {
		JsonArray jsonArray = new JsonArray();
		Iterator<ContentElement> elementIterator = cf.getElements();

		while (elementIterator.hasNext()) {
			ContentElement element = elementIterator.next();
			String itemElement = element.getName();
			String actualIndex = ModelUtils.extractIntAsString(itemElement);

			if (itemElement.contains(actualIndex)) {
				if (containsKey(itemElement) && !element.getContent().isEmpty()) {
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("key", element.getContent());
					jsonArray.add(jsonObject);
				} else if (containsValue(itemElement) && !element.getContent().isEmpty()) {
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("value", element.getContent());
					jsonArray.add(jsonObject);
				}
			}
		}

		return jsonArray;
	}

	protected boolean containsKey(String s) {
		final String KEY = "key";
		return s.contains(KEY);
	}

	protected boolean containsValue(String s) {
		final String VALUE = "value";
		return s.contains(VALUE);
	}
}
