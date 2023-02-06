package com.adiacent.menarini.mhos.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class })
public class RunModeModel {

    @Inject
    private SlingSettingsService slingSettingsService;

    private Boolean isPublishMode = false;

    @PostConstruct
    protected void init() {
        isPublishMode = slingSettingsService.getRunModes().contains("publish");
    }

    public Boolean getPublishMode() {
        return isPublishMode;
    }
}
