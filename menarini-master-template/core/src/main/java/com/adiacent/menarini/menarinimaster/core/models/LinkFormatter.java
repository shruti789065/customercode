package com.adiacent.menarini.menarinimaster.core.models;

import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = SlingHttpServletRequest.class)
public class LinkFormatter {

	public String authoredLink;

    private String formattedLink;

	/**
	 * This method formats the URL.
	 * 
	 */
	@PostConstruct
	protected void init() {
		
		if(StringUtils.isNotEmpty(authoredLink)){
			formattedLink = ModelUtils.getModifiedLink(authoredLink);
		}
	}
	public String getFormattedLink() {
		return formattedLink;
	}
}
