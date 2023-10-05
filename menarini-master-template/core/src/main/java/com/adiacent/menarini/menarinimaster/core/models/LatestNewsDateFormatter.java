package com.adiacent.menarini.menarinimaster.core.models;

import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.crx.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static com.adiacent.menarini.menarinimaster.core.utils.ModelUtils.getFormat;

@Model(adaptables = SlingHttpServletRequest.class)
public class LatestNewsDateFormatter extends GenericBaseModel{
    @Inject
    @Optional
    private String item;

    @SlingObject
    private static ResourceResolver resourceResolver;

    private String formattedValue ;

    private Calendar date;

    @Inject
    private Page currentPage;

    @PostConstruct
    protected void init() throws RepositoryException {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Session session = resourceResolver.adaptTo(Session.class);
        Locale local = ModelUtils.getResourceLocale(currentPage);
        SimpleDateFormat formatter = new SimpleDateFormat(getFormat(local));
        if (Objects.nonNull(session)) {
            if(item != null){
                String path = item.contains(".html") ? item.replace(".html","") : item ;
                Node itemNode = pageManager.getContainingPage(path).adaptTo(Node.class);
                QueryManager queryManager = session.getWorkspace().getQueryManager();
                String expression = "SELECT * FROM [nt:unstructured] AS comp " + "WHERE ISDESCENDANTNODE(comp ,'"
                        + path + "') "
                        + "AND [sling:resourceType] LIKE 'menarinimaster/components/news_data'" +
                        "ORDER BY comp.[jcr:created] ASC";

                Query query = queryManager.createQuery(expression, Query.JCR_SQL2);
                NodeIterator nodeIterator = query.execute().getNodes();
                if(nodeIterator.hasNext()) {
                    Node componentNode = nodeIterator.nextNode();
                    if (componentNode.hasProperty("newsDate")){
                        date  = componentNode.getProperty("newsDate").getDate();
                        formattedValue = formatter.format(date.getTime());
                    }else{
                        String status = itemNode.getNode(JcrConstants.JCR_CONTENT).hasProperty(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION) ? itemNode.getNode(JcrConstants.JCR_CONTENT).getProperty(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION).getString() : "";
                        if (status.equals("Activate")){
                            date = itemNode.getNode(JcrConstants.JCR_CONTENT).getProperty(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED).getDate();
                        }else {
                            date = itemNode.getProperty(JcrConstants.JCR_CREATED).getDate();
                        }
                        formattedValue = formatter.format(date.getTime()) ;
                    }
                }

            }
        }

    }

    public String getFormattedValue() {
        return formattedValue;
    }
}
