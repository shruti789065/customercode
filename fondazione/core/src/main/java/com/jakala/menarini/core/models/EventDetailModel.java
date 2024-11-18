package com.jakala.menarini.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jakala.menarini.core.dto.EventDetailDto;
import com.jakala.menarini.core.service.interfaces.EventListingServiceInterface;

@Model(
    adaptables = SlingHttpServletRequest.class, 
    adapters = EventDetailModel.class, 
    resourceType = "fondazione/components/eventdetail"
)
public class EventDetailModel extends AuthBaseModel {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDetailModel.class);
        
    @OSGiService
    private EventListingServiceInterface eventListingService;

    @SlingObject
    protected ResourceResolver resourceResolver;

    @SlingObject
    protected Resource resource;

    public EventDetailDto getDetail() {
        try {
            String language = ModelHelper.getCurrentPageLanguage(resourceResolver, resource);
            RequestPathInfo pathInfo = request.getRequestPathInfo();
            String path = pathInfo.getSuffix();
            if (path != null){
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                return eventListingService.getEventBySlug(path, resourceResolver, language);
            }

        } catch (IllegalArgumentException e) {
            LOGGER.error("Illegal argument exception in EventDetailModel.getDetail", e);
        } 
        return null;
    }

}
