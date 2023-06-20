package com.adiacent.menarini.menarinimaster.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.foundation.Image;
import lombok.experimental.Delegate;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import javax.annotation.PostConstruct;
import javax.jcr.Node;
import java.util.*;
import java.util.stream.Collectors;

@Model(
        adaptables = {Resource.class, SlingHttpServletRequest.class},
        adapters = {  NewsListI.class, ComponentExporter.class},
        resourceType = MegamenuModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class NewsListModel extends GenericBaseModel implements NewsListI{
    public static final String RESOURCE_TYPE = "menarinimaster/components/news_list";


    @Self // Indicates that we are resolving the current resource
    @Via(type = ResourceSuperType.class) // Resolve not as this model, but as the model of our supertype (ie: CC List)
    @Delegate(excludes = NewsListModel.DelegationExclusion.class) // Delegate all our methods to the CC List except those defined below
    private List delegate;

    public Collection<ListItem> getListItems(){

        if(delegate.getListItems() == null || delegate.getListItems().size() ==0 )
            return delegate.getListItems();

        //si controlla che sia un elenco di pagine ( non ho accesso alleproprietà PN_XXX di https://github.com/adobe/aem-core-wcm-components/blob/main/bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/List.java)
        ArrayList tmp = new ArrayList(delegate.getListItems());
        Optional opt = tmp.stream().findFirst();
        if(!opt.isPresent() || opt.get() == null)
            return delegate.getListItems();

        ListItem item = (ListItem)opt.get();
        Resource resource = resourceResolver.getResource(item.getPath());
        if(!resource.getResourceType().equals("cq:Page"))
            return delegate.getListItems();



        Collections.sort(tmp, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                Resource resource1 = resourceResolver.getResource(o1.getPath());
                Resource resource2 = resourceResolver.getResource(o2.getPath());
                if(resource1 == null  && resource2 == null)
                    return 0;
                if(resource1 == null  && resource2 != null)
                    return 1;
                if(resource1 != null  && resource2 == null)
                    return -1;
                if(resource1.getResourceType().equals(resource2.getResourceType())){
                    Page page1 = resource1.adaptTo(Page.class);
                    Page page2 = resource2.adaptTo(Page.class);
                    ValueMap properties1 = page1.getProperties();
                    ValueMap properties2 = page2.getProperties();
                    Calendar created1 = (Calendar)properties1.get("jcr:created");
                    Calendar created2 = (Calendar)properties2.get("jcr:created");
                    return created1.compareTo(created2);
                }
                return 0;
            }


        });

        return tmp;
    }

    private interface DelegationExclusion { // Here we define the methods we want to override
        default Collection<ListItem> getListItems() {
            return Collections.emptyList();
        }

    }
}
