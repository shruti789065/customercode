package com.jakala.menarini.core.models;


import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.jakala.menarini.core.dto.ExternalSocialLinkResponseDto;
import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RegisteredUserTopicDto;
import com.jakala.menarini.core.models.interfaces.HomePageBaseModelInterface;
import com.jakala.menarini.core.service.interfaces.ExternalizeUrlServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("squid:S2384")
@Model(
        adaptables = {
                SlingHttpServletRequest.class
        },
        adapters = HomePageBaseModelInterface.class
)
public class HomePageBaseModel extends AuthBaseModel implements HomePageBaseModelInterface {

    @OSGiService
    private ExternalizeUrlServiceInterface externalizeService;


    private String firstName;
    private String lastName;
    private String email;
    private List<RegisteredUserTopicDto> topics = new ArrayList<>();

    private String youtubeShareLink;
    private String linkedinShareLink;


    @Override
    @PostConstruct
    protected void init()  {
        super.init();
        ResourceResolver resolver = super.request.getResourceResolver();
        PageManager manager = resolver.adaptTo(PageManager.class);
        if(manager != null) {
            Page currentPage = manager.getContainingPage(super.request.getResource());
            String path =  currentPage.getPath();
            List<ExternalSocialLinkResponseDto> links =
                    externalizeService.genarateSocialLink(resolver,path);
            for(ExternalSocialLinkResponseDto link : links) {
                if(link.getType().equals("youtube")) {
                    this.youtubeShareLink = link.getRedirect();
                } else if(link.getType().equals("linkedin")) {
                    this.linkedinShareLink = link.getRedirect();
                }
            }
        }
        if(this.isAuth()) {
            RegisteredUserDto user = this.getUser();
            this.firstName = user.getFirstname();
            this.lastName = user.getLastname();
            this.email = user.getEmail();
            this.topics = user.getRegisteredUserTopics();
        }

    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public List<RegisteredUserTopicDto> getTopics() {
        return topics;
    }

    @Override
    public void setTopics(List<RegisteredUserTopicDto> topics) {
        this.topics = topics;
    }

    @Override
    public String getYoutubeShareLink() {
        return youtubeShareLink;
    }

    @Override
    public void setYoutubeShareLink(String youtubeShareLink) {
        this.youtubeShareLink = youtubeShareLink;
    }

    @Override
    public String getLinkedinShareLink() {
        return linkedinShareLink;
    }

    @Override
    public void setLinkedinShareLink(String linkedinShareLink) {
        this.linkedinShareLink = linkedinShareLink;
    }
}
