package com.adiacent.menarini.menarinimaster.core.models;

import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import lombok.experimental.Delegate;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.apache.sling.models.factory.ModelFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Iterator;

@Model(
        adaptables = {Resource.class, SlingHttpServletRequest.class},
        adapters = TeaserI.class, // Adapts to the CC model interface
        resourceType = TeaserModel.RESOURCE_TYPE, // Maps to OUR component, not the CC component
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL // No properties? No problem!
)
public class TeaserModel extends GenericBaseModel implements TeaserI {

    public static final String RESOURCE_TYPE = "menarinimaster/components/internalheader";
    //private static final String PARENT_TEMPLATE_NAME = "/conf/menarinimaster/settings/wcm/templates/menarini---homepage";//"Menarini MT - Homepage";


    @Self // Indicates that we are resolving the current resource
    @Via(type = ResourceSuperType.class) // Resolve not as this model, but as the model of our supertype (ie: CC Teaser)
    @Delegate(excludes = DelegationExclusion.class) // Delegate all our methods to the CC Image except those defined below
    private Teaser delegate;

    @OSGiService
    private ModelFactory modelFactory;

    @Inject
    @Optional
    @Via("resource")
    private String videoFilePath;
    private String videoFormat;

    private String parentTitle;
    private Resource parentImage;
    private String parentVideoFormat;
    private String parentVideoFilePath;

    private Page currentPage;
    /*self : parentPage == currentPage : la pagina corrente è una pagina di primo livello, quindi parenPage coincide con currentPage */
    private boolean self = false;



    @PostConstruct
    protected void init() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if(pageManager!=null){

            currentPage = pageManager.getContainingPage(currentResource);

            Page homepage = ModelUtils.getHomePage(resourceResolver, currentPage.getPath());
            ValueMap properties = homepage.getProperties();
            String siteName = properties.containsKey("siteName") ? properties.get("siteName", String.class) : "";
            String PARENT_TEMPLATE_NAME = "/conf/"+siteName+"/settings/wcm/templates/menarini---homepage";

            Page parentPage = ModelUtils.findPageByParentTemplate(currentPage, PARENT_TEMPLATE_NAME);
            if(parentPage != null && parentPage.getName().equals(currentPage.getName()))
               self = true;

            if(parentPage!= null && !self){
                //nella pagina parent di primo livello si cerca il componente   con "respourceType == menarinimaster/components/internalheader" a partire dal nodo
                Resource parentInternalHeader = ModelUtils.findChildComponentByResourceType(parentPage.getContentResource(),"menarinimaster/components/internalheader");

                if(parentInternalHeader != null ) {
                    TeaserModel m = modelFactory.getModelFromWrappedRequest(request, parentInternalHeader, TeaserModel.class);
                    parentTitle = m.getTitle();
                    parentImage = m.getImageResource();
                    parentVideoFilePath = m.getVideoFilePath();
                    parentVideoFormat = m.getVideoFormat();
                    if(parentImage == null){
                        //ResourceUtil.getValueMap(parentInternalHeader).put("imageFromPageImage",true); //forzatura flag ereditarietà immagine da proprietò di pagina
                        Iterator<Resource> res = parentPage.getContentResource().listChildren();
                        while(res.hasNext()){
                            Resource r = (Resource)res.next();
                            if(r.getName().equals(Constants.FEATURE_IMAGE_NODE_NAME)) {
                                parentImage = r;
                                break;
                            }
                        }


                    }

                }
            }
            //video setting
            if(StringUtils.isNotBlank(videoFilePath)){
                String extention = StringUtils.substringAfterLast(videoFilePath,Constants.EXTENTION_SEPARATOR);
                switch(extention){
                    case Constants.MP4_FILE_EXT:{
                        videoFormat= "video/mp4";
                        break;
                    }
                    case  Constants.OGG_FILE_EXT:{
                        videoFormat= "video/ogg";
                        break;
                    }
                    default:
                        videoFormat=null;
                }
            }

        }


    }

    @Override
    public String getTitle() {
        if(StringUtils.isNotBlank(this.delegate.getTitle()))
            return this.delegate.getTitle();
        if(StringUtils.isNotBlank(parentTitle) )
            return parentTitle;
        return currentPage.getTitle();

    }

    @Override
    public Resource getImageResource(){
        if(this.delegate.getImageResource() != null)
            return  this.delegate.getImageResource();
        if(parentImage != null )
            return parentImage;
        return null;
    }


    public String getVideoFilePath() {
        if(StringUtils.isNotBlank(this.videoFilePath))
            return  this.videoFilePath;
        if(parentVideoFilePath!= null )
            return parentVideoFilePath;
        return null;
    }

    public String getVideoFormat() {
        if(StringUtils.isNotBlank(this.videoFormat))
            return  this.videoFormat;
        if(parentVideoFormat!= null )
            return parentVideoFormat;
        return null;
    }

    private interface DelegationExclusion { // Here we define the methods we want to override
        String getTitle(); // Override the method which determines the source of the asset
        Resource getImageResource();

    }

}
