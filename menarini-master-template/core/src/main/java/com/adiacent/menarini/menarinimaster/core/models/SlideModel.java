package com.adiacent.menarini.menarinimaster.core.models;

import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
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

@Model(
		adaptables = {Resource.class, SlingHttpServletRequest.class},
		adapters = SlideModelI.class, // Adapts to the CC model interface
		resourceType = SlideModel.RESOURCE_TYPE, // Maps to OUR component, not the CC component
		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL // No properties? No problem!
)
public class SlideModel extends GenericBaseModel implements SlideModelI {

	public static final String RESOURCE_TYPE = "menarinimaster/components/slideshow/slide-video";
	@Self // Indicates that we are resolving the current resource
	@Via(type = ResourceSuperType.class) // Resolve not as this model, but as the model of our supertype (ie: CC Teaser)


	@OSGiService
	private ModelFactory modelFactory;

	@Inject
	@Optional
	@Via("resource")
	private String videoFilePath;
	private String videoFormat;

	private String duration;

	@PostConstruct
	protected void init() {
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

	public String getVideoFilePath() {
		if (StringUtils.isNotBlank(this.videoFilePath)) {
			return this.videoFilePath;
		}
		return null;
	}

	public String getVideoFormat() {
		if (StringUtils.isNotBlank(this.videoFormat)) {
			return this.videoFormat;
		}
		return null;
	}

	public String getDuration() {
		if (StringUtils.isNotBlank(this.duration)) {
			return this.duration;
		}
		return null;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public void setVideoFilePath(String videoFilePath) {
		this.videoFilePath = videoFilePath;
	}

	public void setVideoFormat(String videoFormat) {
		this.videoFormat = videoFormat;
	}

}
