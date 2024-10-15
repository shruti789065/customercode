package com.jakala.menarini.core.service;


import com.jakala.menarini.core.service.interfaces.ReCaptchaCredentialServiceInterface;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.Map;


@Component(
        service = ReCaptchaCredentialServiceInterface.class,
        immediate = true
)
public class ReCaptchaCredentialService implements ReCaptchaCredentialServiceInterface {

    private String recaptchaSite;



    @Activate
    private void activate(Map<String, Object> properties) {
        this.recaptchaSite = (String) properties.get("recaptchaSiteKey");
    }


    public String getRecaptchaSite() {
        return recaptchaSite;
    }
}
