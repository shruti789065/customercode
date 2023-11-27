package com.adiacent.menarini.menarinimaster.core.models;

import com.adobe.cq.wcm.core.components.models.ListItem;
import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Objects;

@Model(adaptables = SlingHttpServletRequest.class)
public class ResourceResolverHelper {

    @Inject
    @Optional
    private ListItem item;
    @SlingObject
    private static ResourceResolver resourceResolver;

    @Getter
    private String resourceType;

    /**
     * This method formats the URL.
     *
     */
    @PostConstruct
    protected void init() {
        if (item != null && resourceResolver != null) {
            String path = item.getPath();
            if (path != null) {
                Resource resource = resourceResolver.getResource(path);
                if (resource != null) {
                    Object resourceTypeProperty = resource.getValueMap().get(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY);
                    if (resourceTypeProperty != null) {
                        resourceType = resourceTypeProperty.toString();
                    }
                }
            }
        }
    }

}
