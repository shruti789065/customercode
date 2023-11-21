package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES;



@Component( service = {Servlet.class},
        property = { SLING_SERVLET_RESOURCE_TYPES + "=productCategoryArea/categoryServlet"
        })
public class ProductCategoryServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -5290835246731111892L;




    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)  {
        ResourceResolver resolver = request.getResourceResolver();
        try {
            List<Resource> categoriesList = new ArrayList<>();
            Session session = resolver.adaptTo(Session.class);
            QueryManager queryManager = session.getWorkspace().getQueryManager();

            // get SiteName from the URL
            // under XF get Name after /content/experience-fragments/
            // in product category list component retrive name from URL after /content/menarinimaster/
            String siteNameFromPath = request.getRequestParameterMap().containsKey("resourceType") ? request.getRequestParameterMap().getValue("resourceType").getString().contains("productcategorylist") ? request.getRequestPathInfo().getSuffix().split("/")[2] : request.getRequestPathInfo().getSuffix().split("/")[3] : "";

            // Query to get all template under /conf/SiteName_retrived_from_URL/settings/wcm/templates, which contains all the templates
            String expression = "SELECT * FROM [nt:base] AS template WHERE  ISDESCENDANTNODE(template , '/conf/"+siteNameFromPath+"/settings/wcm/templates' ) " +
                    " AND [jcr:primaryType] LIKE 'cq:Template'";

            Query queryContent = queryManager.createQuery(expression, Query.JCR_SQL2);
            NodeIterator nodeIterator = queryContent.execute().getNodes();

            JsonArray productCategory = new JsonArray();


            while (nodeIterator.hasNext()) {
                Node templateNode = nodeIterator.nextNode();
                //Creating JSON object
                JsonObject newsJson = new JsonObject();

                // check if template node name start with "menarini---product-list" , Remember templates that we want to show in select should start with "menarini---product-list"
               if(templateNode.getPath().contains("menarini---product-list")){
                   String templateName = templateNode.hasNode(JcrConstants.JCR_CONTENT) ? templateNode.getNode(JcrConstants.JCR_CONTENT).getProperty(JcrConstants.JCR_TITLE).getString() :"";
                   newsJson.addProperty("text" , templateName);
                   newsJson.addProperty("value", templateNode.getPath());
                   productCategory.add(newsJson);
               }
            }

            if(productCategory.isEmpty()){
                ValueMap vm;
                vm = new ValueMapDecorator(new HashMap<String, Object>());
                vm.put("value", "");
                vm.put("text", "No options found");
                categoriesList.add(new ValueMapResource(resolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, vm));
            } else {
                ValueMap vmall;
                vmall = new ValueMapDecorator(new HashMap<String, Object>());
                vmall.put("value", "all");
                vmall.put("text", "All Products");
                categoriesList.add(new ValueMapResource(resolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, vmall));
                for(int i = 0 ; i< productCategory.size(); i++ ) {
                    ValueMap vm;
                    vm = new ValueMapDecorator(new HashMap<String, Object>());
                    vm.put("value", productCategory.get(i).getAsJsonObject().get("value").getAsString());
                    vm.put("text", productCategory.get(i).getAsJsonObject().get("text").getAsString());
                    categoriesList.add(new ValueMapResource(resolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, vm));
                }
            }

            DataSource dataSource = new SimpleDataSource(categoriesList.iterator());
            request.setAttribute(DataSource.class.getName(), dataSource);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
