package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES;


@Component(service = Servlet.class, name = "Menarini Master Template - Dropdown Countries Servlet", property = {
		SLING_SERVLET_RESOURCE_TYPES + "=bin/apac/country/dropdown"
}, immediate = true)

public class CountryServlet extends SlingAllMethodsServlet {
	private static final long serialVersionUID = 1L;

	private transient final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		try {
			// set fallback
			request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());

			ResourceResolver resolver = request.getResourceResolver();
			PageManager pageManager = resolver.adaptTo(PageManager.class);

			List<Resource> countriesList = new ArrayList<>();
			if (pageManager != null) {
				//String path = request.getPathInfo().contains(".html") ? request.getPathInfo().replace(".html","") : request.getPathInfo().toString();
				String dropdownPath = request.getRequestPathInfo().getResourcePath();
				Resource dropdownResource = resolver.getResource(dropdownPath);
				String countryPagePath = dropdownResource.getValueMap().containsKey("parentPagePath") ? dropdownResource.getValueMap().get("parentPagePath").toString() : "";
				Page countryPage = pageManager.getPage(countryPagePath);
				if (countryPage != null) {
					Iterator<Page> children = countryPage.listChildren();
					ValueMap vm;
					int count = 0;
					while (children.hasNext()) {
						vm = new ValueMapDecorator(new HashMap<>());
						if (count == 0) {
							vm.put("value", "-");
							vm.put("text", "Select");
						} else {
							Page child = children.next();
							String title = child.getContentResource().getValueMap().get(Constants.PAGE_TITLE).toString();
							String value = child.getPath().toString();
							vm.put("value", value);
							vm.put("text", title);
						}
						countriesList.add(new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm));
						count++;
					}
					if (countriesList.isEmpty()) {
						vm = new ValueMapDecorator(new HashMap<>());
						vm.put("value", "-");
						vm.put("text", "No options found");
						countriesList.add(new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm));
					}
					DataSource dataSource = new SimpleDataSource(countriesList.iterator());
					request.setAttribute(DataSource.class.getName(), dataSource);
				}
			}

		} catch (Exception e) {
			LOG.error("Error in Dropdown Country Servlet ", e);
		}
	}
}
