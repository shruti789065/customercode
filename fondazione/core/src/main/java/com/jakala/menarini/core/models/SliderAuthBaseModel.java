package com.jakala.menarini.core.models;

import com.jakala.menarini.core.dto.EventModelDto;
import com.jakala.menarini.core.dto.EventModelReturnDto;
import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RegisteredUserTopicDto;
import com.jakala.menarini.core.service.interfaces.EventListingServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Model(adaptables = {
        Resource.class,
        SlingHttpServletRequest.class
}
)
public class SliderAuthBaseModel extends AuthBaseModel {


    private static boolean IS_TEST = true;

    @SlingObject
    private ResourceResolver resourceResolver;


    @OSGiService
    private EventListingServiceInterface eventListingService;

    private List<RegisteredUserTopicDto> topics = new ArrayList<>();
    private List<EventModelDto> events = new ArrayList<>();

    @Override
    @PostConstruct
    protected void init()  {
        super.init();
        ArrayList<String> idTopics = new ArrayList<>();
        Resource res = resourceResolver.adaptTo(Resource.class);
        String language = ModelHelper.getCurrentPageLanguage(resourceResolver, res);
        if(this.isAuth()) {
            RegisteredUserDto user = this.getUser();
            if(!this.rolesData[0].getName().equals("user")) {
                this.topics = user.getRegisteredUserTopics();
                if(!this.topics.isEmpty()) {
                    for (RegisteredUserTopicDto topic : topics ) {
                        idTopics.add(topic.getTopic().getId());
                    }

                }
                String today = IS_TEST ? "2021-01-01T00:00:00" :
                        new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss").format(new Date());
                EventModelReturnDto returnDto = eventListingService.getEvents(
                        resourceResolver,
                        language,
                        idTopics,
                        null,
                        null,
                        null,
                        today,
                        "",
                        1,
                        3
                );
                this.events = returnDto.getEvents();
            }

        }

    }

    public List<RegisteredUserTopicDto> getTopics() {
        return topics;
    }

    public void setTopics(List<RegisteredUserTopicDto> topics) {
        this.topics = topics;
    }

    public List<EventModelDto> getEvents() {
        return events;
    }

    public void setEvents(List<EventModelDto> events) {
        this.events = events;
    }
}
