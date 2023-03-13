package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import java.util.*;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;


@Component(service = Servlet.class, property = {
        SLING_SERVLET_RESOURCE_TYPES + "=menarinimaster/components/pipeline-container",
        SLING_SERVLET_METHODS + "=GET",
        SLING_SERVLET_SELECTORS + "=pipeline",
        SLING_SERVLET_EXTENSIONS + "=json"}, immediate = true)

public class PipelineServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private transient final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response){
        try{
            Resource currentResource = request.getResource();
            Node currentNode = currentResource.adaptTo(Node.class);
            if(currentNode != null){
                JSONArray jsonArray = getResult(request, currentNode);
                response.setContentType(Constants.APPLICATION_JSON);
                response.getWriter().print(jsonArray);
            }
        }catch (Exception e){
            LOG.error("Error in pipeline servlet Get call: ", e);
        }
    }

    protected JSONArray getResult(SlingHttpServletRequest request, Node currentNode) throws RepositoryException, JSONException {
        List <JSONObject> resultsList = new ArrayList<>();
        JSONArray results = new JSONArray();
        if(request.getParameter("type") != null){
            NodeIterator containerNode = currentNode != null && currentNode.hasNodes() ? currentNode.getNodes() : null;
            while(containerNode.hasNext()) {
                JSONObject result = new JSONObject();
                Node itemNode = containerNode.nextNode();
                result.put("compound", itemNode.getProperty("compoundValue").getString());
                result.put("mechanismofaction", itemNode.hasProperty("mechanismOfAction") ? itemNode.getProperty("mechanismOfAction").getString() : "");
                result.put("indication", itemNode.getProperty("indicationValue").getString());
                result.put("enablestage1", itemNode.hasProperty("enableStage1") ? itemNode.getProperty("enableStage1").getBoolean() : "");
                result.put("enablestage2", itemNode.hasProperty("enableStage2") ? itemNode.getProperty("enableStage2").getBoolean() : "");
                result.put("enablestage3", itemNode.hasProperty("enableStage3") ? itemNode.getProperty("enableStage3").getBoolean(): "");
                result.put("enablestage4", itemNode.hasProperty("enableStage4") ? itemNode.getProperty("enableStage4").getBoolean() : "");
                result.put("enablestage5", itemNode.hasProperty("enableStage5") ? itemNode.getProperty("enableStage5").getBoolean() : "");
                result.put("enablestage6", itemNode.hasProperty("enableStage6") ? itemNode.getProperty("enableStage6").getBoolean() : "");
                result.put("enablestage7", itemNode.hasProperty("enableStage7") ? itemNode.getProperty("enableStage7").getBoolean() : "");
                result.put("labelclinicaltrials", itemNode.hasProperty("labeClinicalTrials") ? itemNode.getProperty("labeClinicalTrials").getString() : "");
                result.put("targetclinicaltrials", itemNode.hasProperty("target") ? itemNode.getProperty("target").getString() : "");
                result.put("clinicaltrails", itemNode.hasProperty("clinicalTrials") ? itemNode.getProperty("clinicalTrials").getString() : "#");
                result.put("readmore", itemNode.hasProperty("readMore") ? itemNode.getProperty("readMore").getString() : "");
                resultsList.add(result);
            }
            HashMap<String, JSONArray> resultMap = new LinkedHashMap<>();
            for (JSONObject currentObj : resultsList){
                String compareValue = null;
                if(request.getParameter("type").equals("compound")){
                    compareValue = currentObj.getString("compound");
                }else if(request.getParameter("type").equals("indication")){
                    compareValue = currentObj.getString("indication");
                }
                if(resultMap.containsKey(compareValue)){
                    JSONArray compareValArr = resultMap.get(compareValue);
                    compareValArr.put(currentObj);
                }else {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(currentObj);
                    resultMap.put(compareValue,jsonArray);
                }
            }
            Iterator it = resultMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                JSONArray array = (JSONArray)pair.getValue();
                for (int c=0;c<array.length();c++){
                    results.put(array.get(c));
                }
            }
        }else{
            results.put("Missing parameters!, required type");
        }
        return results;
    }

}
