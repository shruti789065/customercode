package com.adiacent.menarini.relife.core.servlets;

import com.adiacent.menarini.relife.core.utils.Constants;
import com.day.cq.dam.api.Asset;
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
import java.time.Year;
import java.util.Iterator;

@Component(service = Servlet.class, name = "Menarini Master Template - EFPIA Servlet",  immediate = true)
@SlingServletResourceTypes(
        resourceTypes = "relife/components/efpia" ,
        methods= {HttpConstants.METHOD_GET},
        extensions= Constants.JSON,
        selectors={EfpiaServlet.DEFAULT_SELECTOR})
@ServiceDescription("Relife - EFPIA Servlet")
public class EfpiaServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -6078917648575443885L;
    private transient final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public static final String DEFAULT_SELECTOR = "efpia";

    private static final String MIME_TYPE_IMAGE_JPEG = "image/jpeg";

    private static final String MIME_TYPE_IMAGE_PNG = "image/png";


    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response){
        try{
            Resource currentResource = request.getResource();
            ResourceResolver resourceResolver = currentResource.getResourceResolver();

            if(currentResource.getValueMap().containsKey("folderPath")){
                String damFolderPath = currentResource.getValueMap().get("folderPath").toString();
                Resource damFolder = resourceResolver.getResource(damFolderPath);

                if(damFolder.getResourceType().equals("sling:Folder") || damFolder.getResourceType().equals("nt:folder")){

                    JsonObject finalResult = new JsonObject();

                    // Get the current year
                    Year currentYear = Year.now();

                    // Calculate the last three years
                    Year lastYear = currentYear.minusYears(1);
                    Year twoYearsAgo = currentYear.minusYears(2);
                    Year threeYearsAgo = currentYear.minusYears(3);

                    String lastyeardampath = damFolderPath+"/"+lastYear;
                    String twoyearagodampath = damFolderPath+"/"+twoYearsAgo;
                    String threeyearagodampath = damFolderPath+"/"+threeYearsAgo;
                    if(resourceResolver.getResource(lastyeardampath) != null){
                        JsonArray jsonArray = getResult(lastyeardampath, resourceResolver);
                        if(jsonArray.size() > 0){
                            finalResult.add(String.valueOf(lastYear),jsonArray);
                        }

                    }
                    if(resourceResolver.getResource(twoyearagodampath) != null){
                        JsonArray jsonArray = getResult(twoyearagodampath, resourceResolver);
                        if(jsonArray.size() > 0){
                            finalResult.add(String.valueOf(twoYearsAgo),jsonArray);
                        }

                    }
                    if(resourceResolver.getResource(threeyearagodampath) != null){
                        JsonArray jsonArray = getResult(threeyearagodampath, resourceResolver);
                        if(jsonArray.size() > 0){
                            finalResult.add(String.valueOf(threeYearsAgo),jsonArray);
                        }

                    }
                    response.setContentType(Constants.APPLICATION_JSON);
                    response.getWriter().print(finalResult);
                }
            }


        }catch (Exception e){
            LOG.error("Error in EFPIA Servlet Get call: ", e);
        }
    }

    protected JsonArray getResult(String yearFolderDamPath, ResourceResolver resourceResolver) throws RepositoryException, JsonException {
        Resource resourceDam = resourceResolver.getResource(yearFolderDamPath);
        JsonArray results = new JsonArray();
        Iterator<Resource> items =  resourceDam.listChildren();
        while (items.hasNext()){
            JsonObject result = new JsonObject();
            Resource item = items.next();
            Asset asset = item.adaptTo(Asset.class);
            if(asset != null){
                if(asset.getMimeType().equals(MIME_TYPE_IMAGE_PNG) || asset.getMimeType().equals(MIME_TYPE_IMAGE_JPEG)){
                    String fileNameWithExtension = asset.getName();
                    String fileName = fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf("."));
                    if(fileName.matches("[0-9]+")){
                        result.addProperty("url", asset.getPath());
                        results.add(result);
                    }

                }
            }
        }

        return results;
    }
}
