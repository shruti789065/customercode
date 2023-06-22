package com.adiacent.menarini.menarinimaster.core.models;

import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
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
import org.apache.xmlbeans.impl.xb.xsdschema.impl.ListDocumentImpl;

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
    public static final String NEWSDATA_RESOURCE_TYPE = "menarinimaster/components/news_data";
    private static final String NEWSDATA_DATE_PROPERTY_NAME = "newsDate";


    @Self // Indicates that we are resolving the current resource
    @Via(type = ResourceSuperType.class) // Resolve not as this model, but as the model of our supertype (ie: CC List)
    @Delegate(excludes = NewsListModel.DelegationExclusion.class) // Delegate all our methods to the CC List except those defined below
    private List delegate;

    private ArrayList tmp;
    @PostConstruct
    protected void init(){

        if(delegate.getListItems() != null && delegate.getListItems().size()  > 0 ) {
            //si controlla che sia un elenco di pagine ( non ho accesso alleproprietà PN_XXX di https://github.com/adobe/aem-core-wcm-components/blob/main/bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/List.java)
            tmp = new ArrayList(delegate.getListItems());
            Optional opt = tmp.stream().findFirst();
            if(opt.isPresent() && opt.get() != null){
                Collections.sort(tmp, new Comparator<ListItem>() {
                    @Override
                    public int compare(ListItem o1, ListItem o2) {
                        Calendar date1 = null;
                        Calendar date2 = null;


                        date1 = getNewsDateValue(o1.getPath());
                        date2 = getNewsDateValue(o2.getPath());

                        if(date1 == null  && date2 == null)
                            return 0;
                        if(date1 == null  && date2 != null)
                            return 1;
                        if(date1 != null  && date2 == null)
                            return -1;
                        return date1.compareTo(date2);

                    }
                });
            }

        }

    }

    private Calendar getNewsDateValue(String path) {

        Resource resource = resourceResolver.getResource(path);
        if(resource == null)
            return null;

        if(!resource.getResourceType().equals(Constants.PAGE_PROPERTY_NAME))
            return null;

        Page page = resource.adaptTo(Page.class);
        if(page == null)
            return null;

        Resource newsDataCmp = ModelUtils.findChildComponentByResourceType(page.getContentResource(), NEWSDATA_RESOURCE_TYPE);
        if(newsDataCmp == null)
            return null;
        ValueMap properties = newsDataCmp.getValueMap();
        return properties == null || properties.get(NEWSDATA_DATE_PROPERTY_NAME) == null ? null :(Calendar)properties.get(NEWSDATA_DATE_PROPERTY_NAME);

        }

    public Collection<ListItem> getListItems(){
        if(delegate.getListItems() == null || delegate.getListItems().size() ==0 )
            return delegate.getListItems();
        return this.tmp;
    }



    private interface DelegationExclusion { // Here we define the methods we want to override
        default Collection<ListItem> getListItems() {
            return Collections.emptyList();
        }

    }
}
