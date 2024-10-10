package com.jakala.menarini.core.models;


import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RegisteredUserServletDto;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.security.Acl;
import com.jakala.menarini.core.security.AclRolePermissions;
import com.jakala.menarini.core.service.JwtAuthenticatorService;
import com.jakala.menarini.core.service.UserRegisteredService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

@Model(adaptables = Resource.class)
public class EditProfile extends RegisteredUserServletDto {

    @OSGiService
    private UserRegisteredService userRegisteredService;
    @OSGiService
    private JwtAuthenticatorService jwtAuthenticatorService;

    @Inject
    private String authToken;
    private boolean isInError;
    private boolean isAuthorized;
    private String errorMessage;


    @PostConstruct
    protected void init()  {

    }

    public boolean isInError() {
        return isInError;
    }

    public void setInError(boolean inError) {
        isInError = inError;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private void setValues(RegisteredUserDto userData) {
        this.setFirstName(userData.getFirstname());
        this.setLastName(userData.getLastname());
        this.setGender(userData.getGender());
        this.setBirthDate(userData.getBirthDate());
        this.setPhone(userData.getPhone());
        this.setCountry(userData.getCountry());
        this.setLinkedinProfile(userData.getLinkedinProfile());
        this.setPrivacyConsent(Boolean.parseBoolean(userData.getPersonalDataProcessingConsent()));
        this.setProfilingConsent(Boolean.parseBoolean(userData.getProfilingConsent()));
        this.setNewsletterConsent(Boolean.parseBoolean(userData.getNewsletterSubscription()));
    }




}
