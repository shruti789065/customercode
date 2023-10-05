package com.adiacent.menarini.menarinimaster.core.listeners;

import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component(service = EventHandler.class, immediate = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Page Creation Event Listener for InternalMenu Configuration",
                EventConstants.EVENT_TOPIC + "=" + PageEvent.EVENT_TOPIC
        })
public class PageCreationListener implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageCreationListener.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private SlingSettingsService slingSettingsService;

    @Override
    public void handleEvent(Event event) {
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(null)) {
            Iterator<PageModification> pageInfo = PageEvent.fromEvent(event).getModifications();
            while (pageInfo.hasNext()) {
                PageModification pageModification = pageInfo.next();
                if (pageModification.getType() == PageModification.ModificationType.CREATED) {
                    String path = pageModification.getPath();
                    if (StringUtils.isNotBlank(path)) {
                        Resource resource = resourceResolver.getResource(path);
                        if (resource != null) {
                            Page currentPage = resource.adaptTo(Page.class);
                            ModelUtils.initializeInternalMenuComponent(currentPage, resourceResolver, isPublishMode());
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error while handling page creation event: ", e);
        }
    }

    public boolean isPublishMode() {
        return slingSettingsService.getRunModes().contains("publish");
    }
}
