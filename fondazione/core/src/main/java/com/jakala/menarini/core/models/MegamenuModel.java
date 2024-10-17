package com.jakala.menarini.core.models;


import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Tabs;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.constants.NameConstants;
import com.day.cq.wcm.foundation.Image;
import lombok.experimental.Delegate;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import java.util.*;
import java.util.stream.Collectors;


@Model(
        adaptables = {Resource.class, SlingHttpServletRequest.class},
        adapters = {  MegamenuI.class, ComponentExporter.class},
        resourceType = MegamenuModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class MegamenuModel extends GenericBaseModel implements MegamenuI {

    private static final Logger LOGGER = LoggerFactory.getLogger(MegamenuModel.class.getName());

    // points to the the component resource path in ui.apps
    public static final String RESOURCE_TYPE = "fondazione/components/header/megamenu";
    public static final String IMAGE_RESOURCE_TYPE = "fondazione/components/image";
    public static final String LOGO_RESOURCE_NAME = "logo";
    public static final String LOGO_CONTAINER_ID = "cmp-logo-container";

    // With sling inheritance (sling:resourceSuperType) we can adapt the current resource to the Tabs class
    // this allows us to re-use all of the functionality of the Tabs class, without having to implement it ourself
    // see https://github.com/adobe/aem-core-wcm-components/wiki/Delegation-Pattern-for-Sling-Models
    @Self
    @Via(type = ResourceSuperType.class)
    @Delegate(excludes = DelegationExclusion.class)
    private Tabs delegate; //parent core component

    private Image image;

    @PostConstruct
    protected void init() {
        try {

            if(!currentResource.getResourceType().equals(RESOURCE_TYPE)) {
                return;
            }

            boolean create = true;
            // Filename of the new image to be used as logo
            String fileRef = extractProperty(currentResource, "fileReference");
            //get container for logo img
            Resource resource = resourceResolver.getResource(currentResource.getParent().getParent().getPath());
            List<Resource> childList = new ArrayList<>();
            resource.listChildren().forEachRemaining(childList::add);

            Optional<Resource> optionalRes = childList.stream().filter(child->{

                  return ( child.getValueMap().get("id") != null  &&
                          LOGO_CONTAINER_ID.equals(child.getValueMap().get("id").toString()));

            }
            ).findFirst();

            Resource container = null;
            if(optionalRes.isPresent()){
                container = optionalRes.get();
            }
            String path = container!= null ? container.getPath() : null;

            if(StringUtils.isNotBlank(path)) {
                // delete if the resource/node representing the logo image exists and needs to be updated with the new value
                Resource r = resourceResolver.getResource(path+ "/" + LOGO_RESOURCE_NAME);
                if(r!= null) {
                    image  = new Image(r);
                    String p = extractProperty(image, "fileReference");
                    if( (fileRef== null && p == null ) || (fileRef!= null && p != null && fileRef.compareTo(p) ==0 )) {
                        create = false;
                    }
                }

                if(create && !isPublishMode()) {
                    if(r != null) {
                        resourceResolver.delete(r);
                    }
                    // Retrieve the properties of the megamenu component that refer to the image and use them to
                    // dynamically create a new resource/node of type Image that will be rendered by the model
                    HashMap<String, Object> mapJcr = new HashMap<>();
                    mapJcr.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
                    mapJcr.put(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, IMAGE_RESOURCE_TYPE);
                    mapJcr.put(NameConstants.PN_PAGE_LAST_MOD, Calendar.getInstance());
                    mapJcr.put("titleValueFromDAM", extractProperty(currentResource, "titleValueFromDAM"));
                    mapJcr.put("alt", extractProperty(currentResource, "alt"));
                    mapJcr.put("fileReference", extractProperty(currentResource, "fileReference"));
                    mapJcr.put("altValueFromDAM", extractProperty(currentResource, "altValueFromDAM"));
                    mapJcr.put("altValueFromPageImage", extractProperty(currentResource, "altValueFromPageImage"));
                    mapJcr.put("imageFromPageImage", extractProperty(currentResource, "imageFromPageImage"));

                    Resource testResource = ResourceUtil.getOrCreateResource(resourceResolver, path + "/" + LOGO_RESOURCE_NAME, mapJcr, JcrConstants.NT_UNSTRUCTURED, false);

                    testResource.adaptTo(Node.class).getSession().save();

                    image = new Image(testResource);
                }

            }

        } catch (PersistenceException e) {
            LOGGER.error("PersistenceException occurred while processing the logo image.", e);
        } catch (AccessDeniedException e) {
            LOGGER.error("AccessDeniedException occurred while processing the logo image.", e);
        } catch (ItemExistsException e) {
            LOGGER.error("ItemExistsException occurred while processing the logo image.", e);
        } catch (ReferentialIntegrityException e) {
            LOGGER.error("ReferentialIntegrityException occurred while processing the logo image.", e);
        } catch (ConstraintViolationException e) {
            LOGGER.error("ConstraintViolationException occurred while processing the logo image.", e);
        } catch (InvalidItemStateException e) {
            LOGGER.error("InvalidItemStateException occurred while processing the logo image.", e);
        } catch (VersionException e) {
            LOGGER.error("VersionException occurred while processing the logo image.", e);
        } catch (LockException e) {
            LOGGER.error("LockException occurred while processing the logo image.", e);
        } catch (NoSuchNodeTypeException e) {
            LOGGER.error("NoSuchNodeTypeException occurred while processing the logo image.", e);
        } catch (RepositoryException e) {
            LOGGER.error("RepositoryException occurred while processing the logo image.", e);
        }
    }

    public Image getImage(){
        return image;
    }

    @Override
    public String getId() {
        if(StringUtils.isNotBlank(this.delegate.getId())) {
            return "megamenu-"+this.delegate.getId();
        }

        return null;

    }

    private String extractProperty(Resource resource, String pName){
        if(resource == null || StringUtils.isBlank(pName)) {
            return null;
        }
        if(resource.getValueMap() == null ||  !resource.getValueMap().containsKey(pName)  || resource.getValueMap().get(pName) == null ) {
            return "";
        }
        return resource.getValueMap().get(pName).toString();

    }

    public List<ListItem> getItems(){
        return delegate.getItems().stream()
                .map(listItem -> new CustomTabsListItem( listItem))
                .collect(Collectors.toList());
    }

    private interface DelegationExclusion { // Here we define the methods we want to override
        String getId();
        public List<ListItem> getItems();

    }

}
