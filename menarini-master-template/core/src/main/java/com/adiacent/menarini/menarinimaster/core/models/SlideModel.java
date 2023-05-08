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
		resourceType = SlideModel.RESOURCE_TYPE, // Maps to OUR component, not the CC component
		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL // No properties? No problem!
)
public class SlideModel extends GenericBaseModel implements TeaserI {

	public static final String RESOURCE_TYPE = "menarinimaster/components/internalheader";
	//private static final String PARENT_TEMPLATE_NAME = "/conf/menarinimaster/settings/wcm/templates/menarini---homepage";//"Menarini MT - Homepage";


	@Self // Indicates that we are resolving the current resource
	@Via(type = ResourceSuperType.class) // Resolve not as this model, but as the model of our supertype (ie: CC Teaser)
	@Delegate(excludes = DelegationExclusion.class)
	// Delegate all our methods to the CC Image except those defined below
	private Teaser delegate;

	@OSGiService
	private ModelFactory modelFactory;

	@Inject
	@Optional
	@Via("resource")
	private String videoFilePath;
	private String videoFormat;
	private String description;

	private String parentTitle;
	private Resource parentImage;
	private String parentVideoFormat;
	private String parentVideoFilePath;
	private String duration;
	private Page currentPage;
	/*self : parentPage == currentPage : la pagina corrente è una pagina di primo livello, quindi parenPage coincide con currentPage */
	private boolean self = false;


	@PostConstruct
	protected void init() {
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		if (pageManager != null) {

			currentPage = pageManager.getContainingPage(currentResource);

			Page homepage = ModelUtils.getHomePage(resourceResolver, currentPage.getPath());
			ValueMap properties = homepage.getProperties();
			String siteName = properties.containsKey("siteName") ? properties.get("siteName", String.class) : "";

			//video setting
			if (StringUtils.isNotBlank(videoFilePath)) {

				Resource resource = request.getResourceResolver().getResource(videoFilePath);
				if (resource != null) {
					Resource durationVideoResource = resource.getChild("jcr:content/metadata/xmpDM:duration");
					if (durationVideoResource != null) {
						duration = durationVideoResource.getValueMap().get("xmpDM:value", String.class);
					}
				}


				String extention = StringUtils.substringAfterLast(videoFilePath, Constants.EXTENTION_SEPARATOR);
				switch (extention) {
					case Constants.MP4_FILE_EXT: {
						videoFormat = "video/mp4";
						break;
					}
					case Constants.OGG_FILE_EXT: {
						videoFormat = "video/ogg";
						break;
					}
					default:
						videoFormat = null;
				}
			}
		}
	}

	public String getVideoFilePath() {
		if (StringUtils.isNotBlank(this.videoFilePath))
			return this.videoFilePath;
		if (parentVideoFilePath != null)
			return parentVideoFilePath;
		return null;
	}

	public String getVideoFormat() {
		if (StringUtils.isNotBlank(this.videoFormat))
			return this.videoFormat;
		if (parentVideoFormat != null)
			return parentVideoFormat;
		return null;
	}

	public String getDuration() {
		if (StringUtils.isNotBlank(this.duration)) {
			return this.duration;
		}
		return null;
	}

	private interface DelegationExclusion { // Here we define the methods we want to override
	/*	String getTitle();

		String getDescription();

		Resource getImageResource()*/;// Override the method which determines the source of the asset

	}

}
