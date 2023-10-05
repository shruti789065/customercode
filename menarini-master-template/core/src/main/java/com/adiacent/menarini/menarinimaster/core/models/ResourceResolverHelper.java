package com.adiacent.menarini.menarinimaster.core.models;

import com.adobe.cq.wcm.core.components.models.ListItem;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class)
public class ResourceResolverHelper {

    @Inject
    @Optional
    private ListItem item;
    @SlingObject
    private static ResourceResolver resourceResolver;

    private String resourceType;

    /**
     * This method formats the URL.
     *
     */
    @PostConstruct
    protected void init() {

        if(item != null){
            resourceType = resourceResolver.getResource(item.getPath()).getValueMap().get(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY).toString();
        }
    }


    public String getResourceType(){
        return resourceType;

    }

}
