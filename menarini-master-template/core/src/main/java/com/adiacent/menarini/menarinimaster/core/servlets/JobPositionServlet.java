package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import java.util.Iterator;


@Component(service = Servlet.class, name = "Menarini Master Template - Job Positions Servlet",  immediate = true)
@SlingServletResourceTypes(
        resourceTypes = NameConstants.NT_PAGE,
        methods= {HttpConstants.METHOD_GET},
        extensions= Constants.JSON,
        selectors={JobPositionServlet.DEFAULT_SELECTOR})
@ServiceDescription("Menarini Master Template - Job Positions Servlet")

public class JobPositionServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 1L;
    private transient final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public static final String DEFAULT_SELECTOR = "searchJobPosition";

    private transient Page currentPage = null;

    private String country ;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response){
        try{
            Resource currentResource = request.getResource();
            ResourceResolver resourceResolver = currentResource.getResourceResolver();
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

            if(pageManager != null){
                currentPage = pageManager.getContainingPage(currentResource.getPath());
                if(currentPage != null){
                    JSONObject jsonObject = getResult(request, resourceResolver, pageManager);
                    response.setContentType(Constants.APPLICATION_JSON);
                    response.getWriter().print(jsonObject);
                }

            }


        }catch (Exception e){
            LOG.error("Error in Job Positions Servlet Get call: ", e);
        }
    }

    protected JSONObject getResult(SlingHttpServletRequest request, ResourceResolver resourceResolver, PageManager pageManager) throws RepositoryException, JSONException {
        JSONArray results = new JSONArray();
        JSONObject response = new JSONObject();
        if(request.getParameter("country") != null){
            country = request.getParameter("country");
            Page countryPage = pageManager.getContainingPage(country);
            Iterator<Page> positionsPage = countryPage.listChildren();
            while (positionsPage.hasNext()) {
                JSONObject result = new JSONObject();
                Page child = positionsPage.next();
                String title = child.getContentResource().getValueMap().get(Constants.PAGE_TITLE).toString();
                String link = child.getPath().toString();
                result.put("title", title);
                result.put("url",link);
                results.put(result);
            }
            response.put("results", results);

        }
        return response;
    }
}
