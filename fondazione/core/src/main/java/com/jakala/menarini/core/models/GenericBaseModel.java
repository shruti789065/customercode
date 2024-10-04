package com.jakala.menarini.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.settings.SlingSettingsService;

import javax.inject.Inject;

public class GenericBaseModel {

    @Inject
    private SlingSettingsService slingSettingsService;

    @SlingObject
    protected ResourceResolver resourceResolver;

    @SlingObject
    protected Resource currentResource;

    @Self
    protected SlingHttpServletRequest request;

    public boolean isPublishMode() {
        return slingSettingsService.getRunModes().contains("publish");
    }
}
