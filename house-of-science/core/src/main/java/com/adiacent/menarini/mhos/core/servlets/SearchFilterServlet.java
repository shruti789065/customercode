package com.adiacent.menarini.mhos.core.servlets;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
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
import java.text.SimpleDateFormat;
import java.util.*;


@Component(service = { Servlet.class }, name = "Menarini House of Science - Search Filter Servlet", immediate = true)
@SlingServletResourceTypes(
        resourceTypes = NameConstants.NT_PAGE,
        methods= {HttpConstants.METHOD_GET},
        extensions= "json",
        selectors={SearchFilterServlet.DEFAULT_SELECTOR})
@ServiceDescription("Menarini House of Science - Search Filter Servlet")

public class SearchFilterServlet extends SlingSafeMethodsServlet {

    private transient final Logger LOG = LoggerFactory.getLogger(this.getClass());
    public static final String DEFAULT_SELECTOR = "searchFilter";

    private String keyword ;
    private String cfCategory;
    Page currentPage = null;
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
                JSONArray jsonArray = getResult(request, resourceResolver);
                response.setContentType("application/json");
                response.getWriter().print(jsonArray);
            }
        }catch (Exception e){
            LOG.error("Error in search results Get call: ", e);
        }
    }

    protected JSONArray getResult(SlingHttpServletRequest request, ResourceResolver resourceResolver) throws RepositoryException, JSONException {
        JSONArray results = new JSONArray();

            keyword = request.getParameter("fulltext");
            cfCategory = request.getParameter("category");
            String siteName = request.getResource().getPath().split("/")[2];
            String language = request.getResource().getPath().split("/")[4];
            Resource resourceDam = null;
            StringBuilder myXpathQuery;

            if(cfCategory != null && keyword == null){
                resourceDam = resourceResolver.getResource("/content/dam/" + siteName + "/content-fragments/" + language+  "/" + cfCategory);
                if(resourceDam != null){
                    Iterator<Resource> resouceCategory =  resourceDam.listChildren();
                    while (resouceCategory.hasNext()){
                        JSONObject cfResults = new JSONObject();
                        Resource resourceCf = resouceCategory.next();

                        JSONObject res = contentFragmentData(resourceCf, resourceResolver);
                        if(res.length() >0){
                            results.put(res);
                        }
                    }
                }
            }
            if((keyword!=null && cfCategory!=null) || (cfCategory == null && keyword != null)){
                if(keyword!=null && cfCategory!=null){
                    resourceDam = resourceResolver.getResource("/content/dam/" + siteName + "/content-fragments/" + language+  "/" + cfCategory);
                }else {
                    resourceDam = resourceResolver.getResource("/content/dam/" + siteName + "/content-fragments/" + language);
                }

                myXpathQuery = new StringBuilder();
                myXpathQuery.append("SELECT * FROM [dam:Asset] as p ");
                myXpathQuery.append("WHERE ISDESCENDANTNODE('" + resourceDam.getPath() + "') ");
                myXpathQuery.append(" AND contains(p.*, '*" + keyword + "*' ) ");
                Session session = resourceResolver.adaptTo(Session.class);
                QueryManager queryManager = session.getWorkspace().getQueryManager();
                Query query = queryManager.createQuery(myXpathQuery.toString(), Query.JCR_SQL2);
                QueryResult queryResult = query.execute();
                NodeIterator item = queryResult.getNodes();
                while (item.hasNext()){
                    Node node = item.nextNode();
                    Resource itemRes = resourceResolver.getResource(node.getPath());
                    JSONObject res = contentFragmentData(itemRes, resourceResolver);
                    if(res.length() >0){
                        results.put(res);
                    }
                }
            }
        return results;
    }


    protected JSONObject contentFragmentData(Resource resourceCf, ResourceResolver resourceResolver) throws JSONException {
        JSONObject cfResults = new JSONObject();
        if(resourceCf.getResourceType().equals("dam:Asset")) {
            ContentFragment cf = resourceCf.adaptTo(ContentFragment.class);
            // format date
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            // format year
            SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");

            // get convert article date format
            ContentElement articleDateField = cf.getElement("articleDate");
            if(!articleDateField.getContent().equals("")){
                FragmentData articleDate = articleDateField.getValue();
                Calendar c = articleDate.getValue(Calendar.class);
                String formatedArticleDate = simpleDateFormat.format(c.getTime());
                cfResults.put("date", formatedArticleDate);

                // get year
                String year = formatYear.format(c.getTime());
                cfResults.put("year", year);
            }else {
                cfResults.put("date", "");
                cfResults.put("year", "");
            }
            cfResults.put("title", cf.getTitle());
            cfResults.put("description", cf.getElement("description").getContent());
            cfResults.put("urlLink", cf.getElement("link").getContent());
            cfResults.put("targetLink", cf.getElement("linkTarget").getContent());
            cfResults.put("labelLink", cf.getElement("linkLabel").getContent());
            TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            String[] authors =  cf.getElement("author").getValue().getValue(String[].class);
            String[] topics = cf.getElement("topic").getValue().getValue(String[].class);
            String[] sources = cf.getElement("source").getValue().getValue(String[].class);
            String[] tags = cf.getElement("tags").getValue().getValue(String[].class);
            Tag typology = tagManager.resolve(cf.getElement("typology").getValue().getValue(String.class));
            cfResults.put("author", getTags(authors,tagManager));
            cfResults.put("topic",getTags(topics,tagManager));
            cfResults.put("source", getTags(sources,tagManager));
            cfResults.put("typology", typology != null ? typology.getTitle() :"");
            cfResults.put("tag",getTags(tags,tagManager));

        }
        return cfResults;
    }

    protected JSONArray getTags(String[] arrayTags , TagManager tagManager){
        JSONArray authorTag =  new JSONArray();
        if(arrayTags != null){
            for (String val : arrayTags) {
                Tag tag = tagManager.resolve(val.toString());
                if (tag != null) {
                    authorTag.put(tag.getTitle());
                }
            }
        }
        return authorTag ;
    }

}
