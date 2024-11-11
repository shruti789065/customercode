package com.jakala.menarini.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.osgi.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jakala.menarini.core.dto.EventModelDto;
import com.jakala.menarini.core.service.interfaces.EventListingServiceInterface;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, adapters = EventDetailModel.class, resourceType = "fondazione/components/eventdetail")
public class EventDetailModel extends GenericBaseModel {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDetailModel.class);
        
    @OSGiService
    private EventListingServiceInterface eventListingService;

    public EventModelDto getDetail() {
        try {
            String language = ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource);
            RequestPathInfo pathInfo = request.getRequestPathInfo();
            String path = pathInfo.getSuffix();
            if (path != null){
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                return eventListingService.getEventBySlug(path, resourceResolver, language);
            }

        } catch (Exception e) {
            LOGGER.error("Error in EventDetailModel.init", e);
        }
        return null;
    }

}
