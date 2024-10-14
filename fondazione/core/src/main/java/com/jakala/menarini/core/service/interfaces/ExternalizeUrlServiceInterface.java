package com.jakala.menarini.core.service.interfaces;

import com.jakala.menarini.core.dto.ExternalSocialLinkResponseDto;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

public interface ExternalizeUrlServiceInterface {

    public String getExternalizeUrl(ResourceResolver resolver, String link);
    public String getExternalizeUrlFromRequest(Boolean isAbsolute, SlingHttpServletRequest request, String link);
    public String getRedirect(ResourceResolver resolver, String prev);
    public List<ExternalSocialLinkResponseDto> genarateSocialLink(ResourceResolver resolver, String link);

}
