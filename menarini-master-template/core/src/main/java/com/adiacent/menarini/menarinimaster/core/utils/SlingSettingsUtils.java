package com.adiacent.menarini.menarinimaster.core.utils;

import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class SlingSettingsUtils {

    private static final Logger logger = LoggerFactory.getLogger(SlingSettingsUtils.class);

    private static SlingSettingsUtils _instance = null;

    @Reference
    private SlingSettingsService settingsService;


    @Activate
    protected void activate() {
        SlingSettingsUtils._instance = this;
    }

    /**
     * @return the _instance
     */
    public static SlingSettingsUtils get_instance() {
        return _instance;
    }

    public  boolean checkLocalMode() {
        return settingsService != null && settingsService.getRunModes() != null && settingsService.getRunModes().contains("local");
    }


}
