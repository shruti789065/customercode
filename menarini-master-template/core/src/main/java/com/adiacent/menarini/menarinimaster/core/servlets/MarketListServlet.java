package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
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

import javax.jcr.RepositoryException;
import javax.json.JsonException;
import javax.servlet.Servlet;
import java.util.Iterator;


@Component(service = Servlet.class, name = "Menarini Master Template - Market List Servlet",  immediate = true)
@SlingServletResourceTypes(
        resourceTypes = NameConstants.NT_PAGE,
        methods= {HttpConstants.METHOD_GET},
        extensions= Constants.JSON,
        selectors={MarketListServlet.DEFAULT_SELECTOR})
@ServiceDescription("Menarini Master Template - Market List Servlet")

public class MarketListServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = -6059622002353584286L;
    private transient final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public static final String DEFAULT_SELECTOR = "marketList";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response){
        try{
            Resource currentResource = request.getResource();
            ResourceResolver resourceResolver = currentResource.getResourceResolver();
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

            if(pageManager != null) {
                Page currentPage = pageManager.getContainingPage(currentResource.getPath());
                if (currentPage != null) {
                    JsonObject jsonObject = getResult(request, resourceResolver);
                    response.setContentType(Constants.APPLICATION_JSON);
                    response.getWriter().print(jsonObject);
                }
            }

        }catch (Exception e){
            LOG.error("Error in Job Positions Servlet Get call: ", e);
        }
    }

    protected JsonObject getResult(SlingHttpServletRequest request, ResourceResolver resourceResolver) throws RepositoryException, JsonException {
        JsonArray results = new JsonArray();
        JsonObject response = new JsonObject();
        if(request.getParameter("country") != null){
            String country = request.getParameter("country");
            Resource countryFolder = resourceResolver.getResource(country);
            if(countryFolder != null && countryFolder.isResourceType("sling:Folder")){
                Iterator<Resource> items = countryFolder.listChildren();
                while (items.hasNext()){
                    JsonObject result = new JsonObject();
                    Resource item = items.next();
                    ContentFragment cf = item.adaptTo(ContentFragment.class);
                    if(cf != null){
                        String iconPath = cf.getElement("iconPath").getContent();
                        String url = cf.getElement("linkUrl").getContent();
                        result.addProperty("iconPath", iconPath);
                        result.addProperty("url", url);
                        results.add(result);
                    }
                }
            }
            response.add("results", results);

        }
        return response;
    }
}
