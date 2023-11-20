package com.adiacent.menarini.menarinimaster.core.models;


import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Tabs;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.foundation.Image;
import lombok.experimental.Delegate;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
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
        adapters = {  MegamenuI.class, ComponentExporter.class},
        resourceType = MegamenuModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class MegamenuModel extends GenericBaseModel implements MegamenuI {

    // points to the the component resource path in ui.apps
    public static final String RESOURCE_TYPE = "menarinimaster/components/header/megamenu";
    public static final String IMAGE_RESOURCE_TYPE = "menarinimaster/components/image";
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

            if(!currentResource.getResourceType().equals(RESOURCE_TYPE))
                return;

            boolean create = true;
            //Filename nuova immagine da usare come logo
            String fileRef = extractProperty(currentResource, "fileReference");
            //get container for logo img
            Resource resource = resourceResolver.getResource(currentResource.getParent().getParent().getPath());
            List<Resource> childList = new ArrayList<Resource>();
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
                //si cancella se esiste la risorsa/nodo che rappresenta l'immagine del logo e questa deve essere aggiornata con il nuovo valore
                Resource r = resourceResolver.getResource(path+ "/" + LOGO_RESOURCE_NAME);
                if(r!= null) {
                    image  = new Image(r);
                    String p = extractProperty(image, "fileReference");
                    if( (fileRef== null && p == null ) ||
                            (fileRef!= null && p != null && fileRef.compareTo(p) ==0 ))

                        create = false;
                }

                if(create && !isPublishMode()) {
                    if(r != null)
                        resourceResolver.delete(r);
                    //Si recuperano le proprietà del componenente megamenù che si riferiscono all'immagine e le si utilizzando per
                    //creare dinamicamente una nuova risorsa/nodo di tipo Image che verrà rendirizzata dal modello
                    HashMap<String, Object> mapJcr = new HashMap<String, Object>();
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


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Image getImage(){
        return image;
    }

    @Override
    public String getId() {
        if(StringUtils.isNotBlank(this.delegate.getId()))
            return "megamenu-"+this.delegate.getId();

        return null;

    }
    private String extractProperty(Resource resource, String pName){
        if(resource == null || StringUtils.isBlank(pName))
            return null;
        if(resource.getValueMap() == null ||  !resource.getValueMap().containsKey(pName)  || resource.getValueMap().get(pName) == null )
            return "";
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
