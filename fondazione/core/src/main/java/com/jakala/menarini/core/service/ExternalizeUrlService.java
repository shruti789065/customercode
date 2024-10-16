package com.jakala.menarini.core.service;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.jakala.menarini.core.dto.ExternalSocialLinkResponseDto;
import com.jakala.menarini.core.service.interfaces.ExternalizeUrlServiceInterface;
import com.jakala.menarini.core.service.utlis.ExternalizerUrlConfig;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component(
        service = ExternalizeUrlServiceInterface.class,
        immediate = true,
        configurationPolicy = ConfigurationPolicy.REQUIRE
)
@Designate(ocd = ExternalizerUrlConfig.class)
public class ExternalizeUrlService implements ExternalizeUrlServiceInterface {


    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalizeUrlService.class);

    private static final String LINK_REGEX = "www.([^.]+).";
    private static final String LINK_REGEX_SPLIT = "([^.]+)/";

    private String homePath;
    private String domain;
    private boolean isAuthor;
    private String externalSchema;
    String[] socialLinks;

    @Reference
    private Externalizer externalizer;


    @Activate
    @Modified
    private void activate(ExternalizerUrlConfig config) {
        this.socialLinks = config.socialUrls();
        this.isAuthor = config.isAuthor();
        this.domain = config.domain();
        this.homePath = config.home_path();
        this.externalSchema = config.schema();
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
    public String getRedirect(ResourceResolver resolver, String prev, String actual) {
        String link = prev;
        Resource resource = resolver.getResource(actual);
        String countryLocale = "en";
        if (resource != null) {
            Page targetPage = resource.adaptTo(Page.class);
            if (targetPage != null) {
                Locale pageLocale = targetPage.getLanguage(true);
                countryLocale = pageLocale.getCountry();
            }
        }
        boolean isInternal = link.contains(this.domain);
        if(isInternal) {
            LOGGER.error("========== on domain ================");
           link = "/" + countryLocale + "/" + link.split(this.externalSchema + LINK_REGEX_SPLIT)[1];
        }else {
            link = "/" + this.homePath +  countryLocale + ".html" ;
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
            LOGGER.error("======= ON SOCIAL LOOP ========");
            LOGGER.error(socialBaseLink);
            Matcher matcher = pattern.matcher(socialBaseLink);
            if(matcher.find()) {
                LOGGER.error("======= MATCH ========");
                String type = matcher.group(1);
                ExternalSocialLinkResponseDto socLink = new ExternalSocialLinkResponseDto();
                socLink.setType(type);
                socLink.setRedirect(
                    socialBaseLink +  externalizer.externalLink(resolver,
                                istance,
                                link)
                );
                LOGGER.error("======= ADD MATCH ========");
                links.add(socLink);
            }

        }
        return links;
    }







}
