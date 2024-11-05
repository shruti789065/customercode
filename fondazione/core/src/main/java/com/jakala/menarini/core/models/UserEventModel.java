package com.jakala.menarini.core.models;

import com.jakala.menarini.core.dto.EventModelDto;
import com.jakala.menarini.core.entities.records.EventEnrollmentRecord;
import com.jakala.menarini.core.service.interfaces.EventListingServiceInterface;
import com.jakala.menarini.core.service.interfaces.JcrResolverServiceInterface;
import com.jakala.menarini.core.service.interfaces.UserEventServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Model(
        adaptables = {
                Resource.class,
                SlingHttpServletRequest.class
        }
)
public class UserEventModel extends AuthBaseModel {

    @OSGiService
    private UserEventServiceInterface userEventService;
    @OSGiService
    private EventListingServiceInterface eventListingService;
    @OSGiService
    private JcrResolverServiceInterface jcrResolverService;
    @SlingObject
    private ResourceResolver resourceResolver;

    private  List<EventModelDto> events;
    private  List<EventModelDto> lastEvents;
    private  List<EventModelDto> futureEvents;


    @Override
    @PostConstruct
    protected void init() {
        super.init();
        if(this.isAuth()) {
            Resource res = resourceResolver.adaptTo(Resource.class);
            String language = ModelHelper.getCurrentPageLanguage(resourceResolver, res);
            long userId = this.getUser().getId();
            List<EventEnrollmentRecord> userEnrollmentEvents = userEventService.getUserEnrollmentEvent((Long)userId);
            if(!userEnrollmentEvents.isEmpty()) {
                List<String> eventIds = new ArrayList<>();
                for (EventEnrollmentRecord enrollRecord : userEnrollmentEvents) {
                    eventIds.add(enrollRecord.getEventId());
                }
                try {
                    List<EventModelDto> eventsData = eventListingService.getEventsByIds(eventIds,jcrResolverService.getResourceResolver(),language);
                    if(!eventsData.isEmpty()) {
                        this.events = eventsData;
                        Collections.sort(this.events,(e1,e2) -> e2.getEndDate().compareTo(e2.getEndDate()));
                        this.futureEvents = new ArrayList<>();
                        this.lastEvents = new ArrayList<>();
                        this.fillTheLists();
                    }
                } catch (LoginException e) {
                    this.events = new ArrayList<>();
                    this.futureEvents = new ArrayList<>();
                    this.lastEvents = new ArrayList<>();
                }

            }
        }
    }

    private void fillTheLists() {
        Date today = new Date();
        for (EventModelDto event : this.events) {
            if(event.getStartDate().before(today) || event.getStartDate().equals(today)) {
                this.lastEvents.add(event);
            } else {
                this.futureEvents.add(event);
            }
        }
    }

    public List<EventModelDto> getEvents() {
        return events;
    }

    public void setEvents(List<EventModelDto> events) {
        this.events = events;
    }

    public List<EventModelDto> getLastEvents() {
        return lastEvents;
    }

    public void setLastEvents(List<EventModelDto> lastEvents) {
        this.lastEvents = lastEvents;
    }

    public List<EventModelDto> getFutureEvents() {
        return futureEvents;
    }

    public void setFutureEvents(List<EventModelDto> futureEvents) {
        this.futureEvents = futureEvents;
    }
}
