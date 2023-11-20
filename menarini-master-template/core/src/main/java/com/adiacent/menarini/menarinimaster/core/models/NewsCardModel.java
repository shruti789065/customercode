package com.adiacent.menarini.menarinimaster.core.models;

import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import lombok.experimental.Delegate;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.adiacent.menarini.menarinimaster.core.utils.ModelUtils.getFormat;

@Model(
        adaptables = {Resource.class, SlingHttpServletRequest.class},
        adapters = {NewsCardI.class, ComponentExporter.class}, // Adapts to the CC model interface
        resourceType = NewsCardModel.RESOURCE_TYPE, // Maps to OUR component, not the CC component
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL // No properties? No problem!
)
public class NewsCardModel extends GenericBaseModel implements NewsCardI {

    public static final String RESOURCE_TYPE = "menarinimaster/components/news_card";

    @Self // Indicates that we are resolving the current resource
    @Via(type = ResourceSuperType.class) // Resolve not as this model, but as the model of our supertype (ie: CC Teaser)
    @Delegate(excludes = DelegationExclusion.class) // Delegate all our methods to the CC Image except those defined below
    private Teaser delegate;

    @Inject
    private Node currentNode;

    private String pageLink;

    private Calendar date;

    private String formattedValue ;

    private Page currentPage;



    @PostConstruct
    protected void init() throws RepositoryException {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        currentPage = pageManager.getContainingPage(currentResource);
        Locale local = ModelUtils.getResourceLocale(currentPage);
        SimpleDateFormat formatter = new SimpleDateFormat(getFormat(local));
        if(pageManager!=null){
            if(delegate.getActions().size() > 0){
                pageLink = getActions().get(0).getPath().toString();
                if(!pageLink.isEmpty() && pageLink !=null){
                    Node linkedPageNode = pageManager.getContainingPage(pageLink).adaptTo(Node.class);
                    Node nodePathJcr = linkedPageNode != null && linkedPageNode.hasNode("jcr:content/root/container/container/container/news_data") ? linkedPageNode.getNode("jcr:content/root/container/container/container/news_data") : null;
                    if(nodePathJcr != null){
                        date = nodePathJcr.hasProperty("newsDate") ? nodePathJcr.getProperty("newsDate").getDate() : null;
                        if(date == null){
                            String status = linkedPageNode.getNode(JcrConstants.JCR_CONTENT).hasProperty(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION) ? linkedPageNode.getNode(JcrConstants.JCR_CONTENT).getProperty(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION).getString() : "";
                            if (status.equals("Activate")){
                                date = linkedPageNode.getNode(JcrConstants.JCR_CONTENT).getProperty(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED).getDate();
                            }else {
                                date = linkedPageNode.getNode(JcrConstants.JCR_CONTENT).getProperty(JcrConstants.JCR_CREATED).getDate();
                            }
                        }
                    }
                    formattedValue = formatter.format(date.getTime()) ;
                }
            } else if(currentNode.hasProperty("date")) {
                date = currentNode.getProperty("date").getDate();
                formattedValue = formatter.format(date.getTime()) ;
            }
        }
    }

    public String getFormattedValue() {
        return formattedValue;
    }

    private interface DelegationExclusion { // Here we define the methods we want to override
    }

}
