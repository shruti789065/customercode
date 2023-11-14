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

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;




@Component(service = { Servlet.class }, immediate = true)
@SlingServletResourceTypes(
        resourceTypes = NameConstants.NT_PAGE,
        methods= {HttpConstants.METHOD_GET},
        extensions= Constants.JSON,
        selectors={SearchResultServlet.DEFAULT_SELECTOR})
@ServiceDescription("Menarini Master Template - Search Result Servlet")

public class SearchResultServlet extends SlingSafeMethodsServlet {

    private transient final Logger LOG = LoggerFactory.getLogger(this.getClass());
    public static final String DEFAULT_SELECTOR = "searchresult";

    private String keyword ;
    private transient Page currentPage = null;
    private transient Page homepage = null;

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
            LOG.error("Error in search results Get call: ", e);
        }
    }

    protected JSONObject getResult(SlingHttpServletRequest request, ResourceResolver resourceResolver) throws RepositoryException, JSONException {
        JSONArray results = new JSONArray();
        JSONObject response = new JSONObject();
        if(request.getParameter("fulltext") != null){
            keyword = request.getParameter("fulltext");
            StringBuilder myXpathQuery = new StringBuilder();
            myXpathQuery.append("SELECT * FROM [cq:Page] as p ");
            myXpathQuery.append("WHERE ISDESCENDANTNODE('" + homepage.getPath() + "') ");
            myXpathQuery.append(" AND contains(p.*, '*" + keyword + "*' ) ");
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
                result.put("url", resultPage.getPath() + ".html");
                result.put("path", resultPage.getPath());
                result.put("title", resultPage.getTitle());
                result.put("description", resultPage.getDescription());
                results.put(result);
            }
            response.put("results", results);
        }else{
            response.put("errorMsg", "Missing parameters!, required fulltext");
        }

        return response;
    }
}
