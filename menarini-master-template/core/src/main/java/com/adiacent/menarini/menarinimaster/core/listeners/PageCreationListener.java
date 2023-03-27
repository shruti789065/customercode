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
import java.util.*;




@Component(service = EventHandler.class,
        immediate = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Page Creation Event Listener for InternalMenu Configuration ",
                EventConstants.EVENT_TOPIC + "=" + PageEvent.EVENT_TOPIC
        }
)
public class PageCreationListener implements EventHandler{
    public static final Logger LOGGER = LoggerFactory.getLogger(PageCreationListener.class);


    //private static String TEMPLATE_REGEXP = "/conf/[a-zA-Z]+/settings/wcm/templates/(menarini---details-news|menarini---details-page|menarini---details-product|menarini---product-category|menarini---product-list-pharmaceutical|menarini---product-list-healthcare)";
    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Reference
    private SlingSettingsService slingSettingsService;

    @Override
    public void handleEvent(Event event) {
        ResourceResolver resourceResolver = null;
        try {

            try {
                Map<String, Object> param = new HashMap<String, Object>();
                param.put(ResourceResolverFactory.SUBSERVICE, com.adiacent.menarini.menarinimaster.core.utils.Constants.SERVICE_NAME);
                resourceResolver =  resourceResolverFactory.getServiceResourceResolver(param);
            } catch (Exception e) {
                LOGGER.error("RepositoryLogin Error: ", e);
            }

            Resource resource = null;
            Iterator<PageModification> pageInfo=PageEvent.fromEvent(event).getModifications();
            while (pageInfo.hasNext()){
                PageModification pageModification=pageInfo.next();
                //LOGGER.info("\n Type :  {},  Page : {}",pageModification.getType(),pageModification.getPath());
                //pageModification.getEventProperties().forEach((k,v)->LOGGER.info("\n key : {}, Value : {} " , k , v));

                //evento dedicato alla creazione di pagina( non alla sua modifica )
                if (pageModification.getType() == PageModification.ModificationType.CREATED){
                    if(StringUtils.isNotBlank((String)pageModification.getEventProperties().entrySet().stream().filter(p->p.getKey().equals("path")).map(n -> n.getValue()).findFirst().get())) {
                        String path = (String) pageModification.getEventProperties().entrySet().stream().filter(p -> p.getKey().equals("path")).map(n -> n.getValue()).findFirst().get();
                        if (StringUtils.isNotBlank(path)) {
                            if (resourceResolver != null) {
                                resource = resourceResolver.getResource(path);
                            }
                            if (resource != null) {
                                Page currentPage = resource.adaptTo(Page.class);
                                ModelUtils.initializeInternalMenuComponent(currentPage, resourceResolver, isPublishMode());
                            }

                        }
                    }

                }
            }

        }catch (Exception e){
            LOGGER.info("\n Error while Activating/Deactivating - {} " , e.getMessage());
        }
    }

    public boolean isPublishMode() {
        return slingSettingsService.getRunModes().contains("publish");
    }
}
