package com.adiacent.menarini.mhos.core.models;

import com.adiacent.menarini.mhos.core.util.Util;
import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.adiacent.menarini.mhos.core.util.Util.getFormat;

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class })
public class LatestArticleCardModel {

    @SlingObject
    private static ResourceResolver resourceResolver;

    @Inject
    private Page currentPage;

    private String typology;

    private String category;

    private String title;

    private String date;

    private String description;

    private String link;

    private String linkLabel;

    private String target;

    private TagManager tagManager;


    @PostConstruct
    protected void init() throws RepositoryException {

        Session session = resourceResolver.adaptTo(Session.class);

        StringBuilder myXpathQuery;
        String siteName = currentPage.getPath().split("/")[2];
        String language = currentPage.getPath().split("/")[4];
        Resource contentFragRes = null;
        Locale local = Util.getResourceLocale(currentPage);
        SimpleDateFormat formatter = new SimpleDateFormat(getFormat(local),local);
        contentFragRes = resourceResolver.getResource("/content/dam/" + siteName + "/content-fragments/" + language);
        if(contentFragRes != null){
            myXpathQuery = new StringBuilder();
            myXpathQuery.append("SELECT * FROM [dam:Asset] as p ");
            myXpathQuery.append("WHERE ISDESCENDANTNODE('" + contentFragRes.getPath() + "') ");
            myXpathQuery.append(" ORDER BY p.[jcr:created] ASC ");
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(myXpathQuery.toString(), Query.JCR_SQL2);
            QueryResult queryResult = query.execute();
            if(queryResult.getNodes().hasNext()){
                Node node = queryResult.getNodes().nextNode();
                Resource itemRes = resourceResolver.getResource(node.getPath());
                if(itemRes.getResourceType().equals("dam:Asset")) {
                    ContentFragment cf = itemRes.adaptTo(ContentFragment.class);
                    tagManager = resourceResolver.adaptTo(TagManager.class);
                    title = cf.getTitle();
                    category = itemRes.getPath().split("/"+language)[1].split("/")[1];
                    description = cf.getElement("description").getContent();
                    link = cf.getElement("link").getContent();
                    linkLabel = cf.getElement("linkLabel").getContent();
                    target = cf.getElement("linkTarget").getContent();
                    Tag typologyCf = getTagFromContentFragment(cf);
                    typology = typologyCf != null ? typologyCf.getTitle() : "";
                    ContentElement articleDateField = cf.getElement("articleDate");
                    if(!articleDateField.getContent().equals("")){
                        FragmentData articleDate = articleDateField.getValue();
                        Calendar c = articleDate.getValue(Calendar.class);
                        date = formatter.format(c.getTime());
                    }

                }
            }
        }

    }

    protected Tag getTagFromContentFragment( ContentFragment cf){
        return tagManager.resolve(cf.getElement("typology").getValue().getValue(String.class));
    }

    public String getTypology() {
        return typology;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getLinkLabel() {
        return linkLabel;
    }

    public String getTarget() {
        return target;
    }
}
