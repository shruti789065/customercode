package com.jakala.menarini.core.models;


import com.jakala.menarini.core.service.interfaces.ReCaptchaCredentialServiceInterface;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import javax.annotation.PostConstruct;

@Model(adaptables = Resource.class)
public class RecaptchaCredentialModel {

    private String siteKey;

    @OSGiService
    private ReCaptchaCredentialServiceInterface reCaptchaCredentialService;


    @PostConstruct
    protected void init() {
        this.siteKey = this.reCaptchaCredentialService.getRecaptchaSite();
    }

    public String getSiteKey() {
        return siteKey;
    }

    public void setSiteKey(String siteKey) {
        this.siteKey = siteKey;
    }

}
