package com.jakala.menarini.core.models.interfaces;

import com.jakala.menarini.core.dto.RegisteredUserTopicDto;

import java.util.List;

public interface HomePageBaseModelInterface {


    public String getFirstName();
    public void setFirstName(String firstName);
    public String getLastName();
    public void setLastName(String lastName);
    public String getEmail();
    public void setEmail(String email);
    public List<RegisteredUserTopicDto> getTopics();
    public void setTopics(List<RegisteredUserTopicDto> topics);

}
