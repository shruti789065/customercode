package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.schedulers.EncodeDecodeSecretKey;
import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
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

import javax.jcr.Node;
import javax.servlet.Servlet;
import java.util.Iterator;


@Component(service = Servlet.class, immediate = true)
@SlingServletResourceTypes(
        resourceTypes = {"menarinimaster/components/form/connected-option-container"},
        methods = {HttpConstants.METHOD_GET},
        extensions = Constants.JSON,
        selectors = {CountryJobPositionOptionsServlet.DEFAULT_SELECTOR})
@ServiceDescription("Menarini Master Template - Connected Country And Job Position Dropdown Options Servlet")

public class CountryJobPositionOptionsServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 8861971348268777529L;
    private transient final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public static final String DEFAULT_SELECTOR = "countryJobOptions";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response){
        try{
            Resource currentResource = request.getResource();
            ResourceResolver resolver = currentResource.getResourceResolver();
            Node currentNode = currentResource.adaptTo(Node.class);
            if (currentNode != null) {
                JsonObject jsonObject = getResult(resolver,currentNode);
                response.setContentType(Constants.APPLICATION_JSON);
                response.getWriter().print(jsonObject);
            }

        }catch (Exception e){
            LOG.error("Error in Connected Country And Job Position Dropdown Options Servlet Get call: ", e);
        }
    }

    protected JsonObject getResult(ResourceResolver resolver ,Node currentNode) throws Exception {
        JsonObject result = new JsonObject();
        String countryPagePath = currentNode.getProperty("sourceFolder").getString();
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        if (pageManager != null) {
            Page countryParentPage = pageManager.getPage(countryPagePath);
            if (countryParentPage != null) {
                Iterator<Page> children = countryParentPage.listChildren();
                int counter = 0;
                while (children.hasNext()) {
                    Page countryPage = children.next();
                    result.add("fragment-"+counter, getCountry(countryPage));
                    counter++;
                }
            }
        }
        return result;
    }

    protected JsonObject getCountry(Page countryPage) throws Exception {
        JsonObject countryResult = new JsonObject();
        countryResult.addProperty("title", countryPage.getTitle());
        countryResult.addProperty("name", countryPage.getName());
        JsonArray positionsJsonArray = getJobPositions(countryPage);
        countryResult.add("department",positionsJsonArray);
        return countryResult;
    }

    protected JsonArray getJobPositions(Page countryPage) throws Exception {
        JsonArray jobPositionsResult = new JsonArray();
        JsonObject result = new JsonObject();
        String mail="";
        if(countryPage.getContentResource().getValueMap().containsKey("sendCVMail")){
            mail = ModelUtils.encrypt(EncodeDecodeSecretKey.get_instance().getConfig().getSecretKey(), EncodeDecodeSecretKey.get_instance().getConfig().getIvParameter(),countryPage.getContentResource().getValueMap().get("sendCVMail").toString(), EncodeDecodeSecretKey.get_instance().getConfig().getAlgorithm());
        }
        if(countryPage != null){
            Iterator<Page> positionPages = countryPage.listChildren();
            while (positionPages.hasNext()) {
                Page positionPage = positionPages.next();
                result.addProperty(positionPage.getTitle(),mail);
            }
            if(result.size() > 0){
                jobPositionsResult.add(result);
            }
        }
        return jobPositionsResult;
    }

}
