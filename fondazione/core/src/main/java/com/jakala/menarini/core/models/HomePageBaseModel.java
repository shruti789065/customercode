package com.jakala.menarini.core.models;


import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RegisteredUserTopicDto;
import com.jakala.menarini.core.models.interfaces.HomePageBaseModelInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(
        adaptables = {
                SlingHttpServletRequest.class
        },
        adapters = HomePageBaseModelInterface.class
)
public class HomePageBaseModel extends AuthBaseModel implements HomePageBaseModelInterface {



    private String firstName;
    private String lastName;
    private String email;
    private List<RegisteredUserTopicDto> topics = new ArrayList<>();


    @Override
    @PostConstruct
    protected void init()  {
        super.init();
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
}
