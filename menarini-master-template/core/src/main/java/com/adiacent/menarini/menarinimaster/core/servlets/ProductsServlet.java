package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.servlet.Servlet;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;


@Component(service = Servlet.class, property = {
        SLING_SERVLET_RESOURCE_TYPES + "=menarinimaster/components/product-container",
        SLING_SERVLET_METHODS + "=GET",
        SLING_SERVLET_SELECTORS + "=products",
        SLING_SERVLET_EXTENSIONS + "=json"}, immediate = true)

public class ProductsServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response){
        try{
            Resource currentResource = request.getResource();
            Node currentNode = currentResource.adaptTo(Node.class);
            if(currentNode != null){
                JSONArray results = new JSONArray();
                NodeIterator containerNode = currentNode != null && currentNode.hasNodes() ? currentNode.getNodes() : null;
                if(containerNode != null) {
                    while (containerNode.hasNext()) {
                        JSONObject result = new JSONObject();
                        Node itemNode = containerNode.nextNode();
                        if (itemNode.getProperty("sling:resourceType").getString().equals("menarinimaster/components/product-item")) {
                            result.put("country", itemNode.getProperty("country").getString());
                            result.put("image", itemNode.hasProperty("fileReference") ? itemNode.getProperty("fileReference").getString() : "");
                            result.put("name", itemNode.hasProperty("productName") ? itemNode.getProperty("productName").getString() : "");
                            result.put("website", itemNode.hasProperty("link") ? itemNode.getProperty("link").getString() : "");
                            result.put("targetwebsite", itemNode.getProperty("target").getString());
                            result.put("labelwebsite", itemNode.hasProperty("label") ? itemNode.getProperty("label").getString() : "");
                            results.put(result);
                        }
                    }
                }
                response.setContentType(Constants.APPLICATION_JSON);
                response.getWriter().print(results);
            }
        }catch (Exception e){
            logger.error("Error in Products servlet Get call: ", e);
        }
    }

}
