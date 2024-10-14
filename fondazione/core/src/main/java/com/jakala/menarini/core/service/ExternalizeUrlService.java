package com.jakala.menarini.core.service;

import com.day.cq.commons.Externalizer;
import com.jakala.menarini.core.dto.ExternalSocialLinkResponseDto;
import com.jakala.menarini.core.service.interfaces.ExternalizeUrlServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalizeUrlService implements ExternalizeUrlServiceInterface {



    private static final String LINK_REGEX = "www.([^.]+).";

    private String homePath;
    private String domain;
    private boolean isAuthor;
    private String externalSchema;
    private ArrayList<String> socialLinks;

    @Reference
    private Externalizer externalizer;
    @Reference
    private ResourceResolver resolver;


    @Activate
    private void activate(Map<String, Object> properties) {
        this.homePath = properties.get("home_path").toString();
        this.domain = properties.get("domain").toString();
        this.isAuthor = Boolean.parseBoolean(properties.get("isAuthor").toString());
        this.externalSchema = properties.get("schema").toString();
        this.socialLinks =  (ArrayList<String>)properties.get("socialUrls");
    }

   @Override
    public String getExternalizeUrl(ResourceResolver resolver, String link) {
        if(this.isAuthor) {
            return externalizer.externalLink(resolver, Externalizer.AUTHOR ,link);
        } else {
            return externalizer.externalLink(resolver, Externalizer.PUBLISH, link);
        }

    }

    @Override
    public String getExternalizeUrlFromRequest(Boolean isAbsolute, SlingHttpServletRequest request, String link) {
        if(Boolean.TRUE.equals(isAbsolute)) {
            return externalizer.absoluteLink(request,this.externalSchema,link);
        }
        return externalizer.relativeLink(request,link);
    }

    @Override
    public String getRedirect(ResourceResolver resolver, String prev) {
        String link = prev;
        boolean isInternal = link.contains(this.domain);
        if(isInternal) {
           link = link.split(this.domain)[1];
        }else {
            link = this.homePath;
        }
        if(this.isAuthor) {
            return externalizer.authorLink(resolver, link);
        }
        return externalizer.publishLink(resolver, link);
    }

    @Override
    public List<ExternalSocialLinkResponseDto> genarateSocialLink(ResourceResolver resolver, String link) {
        String istance = this.isAuthor ? Externalizer.AUTHOR : Externalizer.PUBLISH;
        ArrayList<ExternalSocialLinkResponseDto> links = new ArrayList<>();
        Pattern pattern = Pattern.compile(LINK_REGEX);
        for(String socialBaseLink : this.socialLinks) {
            Matcher matcher = pattern.matcher(socialBaseLink);
            if(matcher.find()) {
                String type = matcher.group(1);
                ExternalSocialLinkResponseDto socLink = new ExternalSocialLinkResponseDto();
                socLink.setType(type);
                socLink.setRedirect(
                        externalizer.externalLink(resolver,
                                istance,
                                link)
                );
                links.add(socLink);
            }

        }
        return links;
    }







}
