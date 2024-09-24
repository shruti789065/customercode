package com.jakala.menarini.core.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.adobe.cq.dam.cfm.ContentFragment;

@Model(adaptables = Resource.class, adapters = EventListingModel.class, resourceType = "fondazione/components/fragmentlist")
public class EventListingModel {
    
    @Inject
    private ResourceResolver resourceResolver;

    @SlingObject
    private Resource currentResource;

    private List<Event> events = new ArrayList<>();
    
    private static final String EVENT_PATH = "/content/dam/fondazione/events";

    @PostConstruct
    protected void init() {
        
        Resource parentResource = resourceResolver.getResource(EVENT_PATH);
        
        String language = ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource);

        if (parentResource != null) {
            Iterator<Resource> children = parentResource.listChildren();
            while (children.hasNext()) {
                Resource child = children.next();
                ContentFragment fragment = child.adaptTo(ContentFragment.class);

                if (fragment != null) {
                    String title = ModelHelper.getLocalizedElementValue(fragment, language, "titolo", fragment.getTitle());
                    String description = ModelHelper.getLocalizedElementValue(fragment, language, "descrizione", fragment.getDescription());
                    String startDateStr = fragment.getElement("data_inizio").getContent();
                    String endDateStr = fragment.getElement("data_fine").getContent();
                    
                    events.add(new Event(title, description, child.getPath(), startDateStr, endDateStr));
                }
            }
            
            // Sort the fragments by startDate in descending order (most recent first)
            events.sort((e1, e2) -> e2.getStartDate().compareTo(e1.getStartDate()));
        }
    }

    public List<Event> getEvents() {
        return events;
    }

    public static class Event {
        private String title;
        private String description;
        private String path;
        private Date startDate;
        private Date endDate;
        private String startDateText;
        private String endDateText;

        public Event(String title, String description, String path, String startDateStr, String endDateStr) {
            this.title = title;
            this.description = description;
            this.path = path;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat stringFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                this.startDate = (startDateStr != null && !startDateStr.isEmpty()) ? dateFormat.parse(startDateStr) : null;
                this.endDate = (endDateStr != null && !endDateStr.isEmpty()) ? dateFormat.parse(endDateStr) : null;
                this.startDateText = (startDate != null) ? stringFormat.format(startDate) : null;
                this.endDateText = (endDate != null) ? stringFormat.format(endDate) : null;
            } catch (ParseException e) {
                e.printStackTrace();
                this.startDate = null;
                this.endDate = null;
            }
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getPath() {
            return path;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public String getStartDateText() {
            return startDateText;
        }

        public String getEndDateText() {
            return endDateText;
        }
    }

}
