package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;
import java.util.List;


@Component(service = { Servlet.class }, immediate = true)
@SlingServletResourceTypes(
        resourceTypes = NameConstants.NT_PAGE,
        methods= {HttpConstants.METHOD_GET},
        extensions= Constants.JSON,
        selectors={SearchResultServlet.DEFAULT_SELECTOR})
@ServiceDescription("Menarini Master Template - Search Result Servlet")

public class SearchResultServlet extends SlingSafeMethodsServlet {

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String DEFAULT_SELECTOR = "searchresult";


    private transient Page currentPage = null;
    private transient Page homepage = null;

    private List<String> jcrPropToInclude = List.of("jcr:title","jcr:description","text");

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response){
        try{
            Resource currentResource = request.getResource();
            ResourceResolver resourceResolver = currentResource.getResourceResolver();
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

            if (pageManager != null) {
                currentPage = pageManager.getContainingPage(currentResource.getPath());
            }
            if(currentPage != null){
                homepage = ModelUtils.getHomePage(resourceResolver, currentPage.getPath());
                JSONObject jsonObject = getResult(request, resourceResolver);
                response.setContentType(Constants.APPLICATION_JSON);
                response.getWriter().print(jsonObject);
            }
        }catch (Exception e){
            logger.error("Error in search results Get call: ", e);
        }
    }

    protected JSONObject getResult(SlingHttpServletRequest request, ResourceResolver resourceResolver) throws RepositoryException, JSONException {
        String keyword ;
        JSONArray results = new JSONArray();
        JSONObject response = new JSONObject();
        if(request.getParameter("fulltext") != null){
            keyword = request.getParameter("fulltext");
            StringBuilder myXpathQuery = new StringBuilder();
            myXpathQuery.append("SELECT * FROM [cq:Page] as p ");
            myXpathQuery.append("WHERE ISDESCENDANTNODE('" + homepage.getPath() + "') ");
            myXpathQuery.append(" AND contains(p.*, '" + keyword + "' ) ");
            myXpathQuery.append(" AND (contains(p.*,'/settings/wcm/templates/menarini---homepage') ");
            myXpathQuery.append(" OR contains(p.*,'/settings/wcm/templates/menarini---content-page') ");
            myXpathQuery.append(" OR contains(p.*,'/settings/wcm/templates/menarini---details-news') ");
            myXpathQuery.append(" OR contains(p.*,'/settings/wcm/templates/menarini---product-area') ");
            myXpathQuery.append(" OR contains(p.*,'/settings/wcm/templates/menarini---product-category') ");
            myXpathQuery.append(" OR contains(p.*,'/settings/wcm/templates/menarini---product-category') ");
            myXpathQuery.append(" OR contains(p.*,'/settings/wcm/templates/menarini---details-product') ");
            myXpathQuery.append(" OR contains(p.*,'/settings/wcm/templates/menarini---details-page')) ");
            myXpathQuery.append("ORDER BY p.[jcr:content/jcr:created] DESC");
            Session session = resourceResolver.adaptTo(Session.class);
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(myXpathQuery.toString(), Query.JCR_SQL2);
            QueryResult queryResult = query.execute();
            NodeIterator it = queryResult.getNodes();
            while (it.hasNext()){
                JSONObject result = new JSONObject();
                Node node = it.nextNode();
                Page resultPage = resourceResolver.adaptTo(PageManager.class).getContainingPage(node.getPath());
                StringBuilder queryInsidePage = new StringBuilder();
                queryInsidePage.append("SELECT * FROM [nt:unstructured] as page ");
                queryInsidePage.append("WHERE ISDESCENDANTNODE('" + node.getPath() + "') ");
                queryInsidePage.append(" AND contains(page.*, '" + keyword + "' ) ");

                Query queryPage = queryManager.createQuery(queryInsidePage.toString(), Query.JCR_SQL2);
                QueryResult queryPageResult = queryPage.execute();
                NodeIterator nodes = queryPageResult.getNodes();
                if(nodes.getSize() == 1){
                    Node pageNode = nodes.nextNode();
                    if(pageNode.getProperty("jcr:title").getString().toLowerCase().contains(keyword.toLowerCase())){
                        if(pageNode.hasProperty("jcr:description")){
                            result.put("description", pageNode.getProperty("jcr:description").getString());
                        } else {
                            result.put("description", pageNode.getProperty("jcr:title").getString());
                        }
                    }

                } else {
                    while (nodes.hasNext()){
                        Node pageNode = nodes.nextNode();
                        if(!pageNode.getName().equals("jcr:content")){
                            PropertyIterator jcrNodeProperties = pageNode.getProperties();
                            while (jcrNodeProperties.hasNext()){
                                Property property = jcrNodeProperties.nextProperty();
                                if(jcrPropToInclude.contains(property.getName())){
                                    if(!property.isMultiple() && property.getString().toLowerCase().contains(keyword.toLowerCase())){
                                        String withoutTags = property.getString().replaceAll("\\<.*?\\>","").trim();
                                        String clearText = withoutTags.replaceAll("&nbsp;"," ");
                                        result.put("description", clearText);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if(!result.isEmpty()){
                    result.put("url", resultPage.getPath() + ".html");
                    result.put("path", resultPage.getPath());
                    result.put("title", resultPage.getTitle());
                    results.put(result);
                }
            }
            response.put("results", results);
        }else{
            response.put("errorMsg", "Missing parameters!, required fulltext");
        }

        return response;
    }
}
