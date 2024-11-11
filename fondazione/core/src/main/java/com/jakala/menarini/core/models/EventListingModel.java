package com.jakala.menarini.core.models;

import java.util.*;

import com.jakala.menarini.core.dto.EventModelDto;
import com.jakala.menarini.core.dto.EventModelReturnDto;
import com.jakala.menarini.core.service.interfaces.EventListingServiceInterface;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, adapters = EventListingModel.class, resourceType = "fondazione/components/fragmentlist")
public class EventListingModel extends GenericBaseModel {

    @OSGiService
    private EventListingServiceInterface eventListingService;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventListingModel.class);

    private static final String ET_EVENT = "event";
    private static final String ET_COURSE = "course";
    private static final String ET_WEBINAR = "webinar";
    private static final String EF_LIVE_STREAM = "liveStreaming";
    private static final String EF_RESIDENTIAL = "residential";


    private int totalResults;
    private int currentPage;
    private int pageSize;
    private int totalPages;


    private List<String> getTopicFilters() {
        String[] topicsFilter = request.getParameterValues("topics");
        if (topicsFilter != null && topicsFilter.length == 1 && topicsFilter[0].contains("-")) {
            topicsFilter = topicsFilter[0].split("-");
        }
        if(topicsFilter != null) {
            return Arrays.asList(topicsFilter);
        }
        return new ArrayList<>();

    }

    private List<String> getEventTypeFilter() {
        String[] eventTypeFilter = request.getParameterValues("eventTypes");
        if (eventTypeFilter != null && eventTypeFilter.length == 1 && eventTypeFilter[0].contains("-")) {
            eventTypeFilter = eventTypeFilter[0].split("-");
        }
        if(eventTypeFilter != null) {
            return Arrays.asList(eventTypeFilter);
        }
        return new ArrayList<>();

    }

    private List<String> getCityFilter() {
        String[] locationFilter = request.getParameterValues("locations");
        if (locationFilter != null && locationFilter.length == 1 && locationFilter[0].contains("-")) {
            locationFilter = locationFilter[0].split("-");
        }
        if (locationFilter != null ) {
            return Arrays.asList(locationFilter);
        }
        return new ArrayList<>();
    }

    private String getScheduledFilter() {
        return request.getParameter("eventStatus");

    }

    private String[] getDateFilter() {

        String dateOrPeriod = request.getParameter("dateOrPeriod");
        if (dateOrPeriod != null && !dateOrPeriod.isEmpty()) {
            if (dateOrPeriod.contains("-to-")) {
                dateOrPeriod = dateOrPeriod.replace("-to-", " to ");
            }
            if (dateOrPeriod.contains(" to ")) {
                return dateOrPeriod.split(" to ");

            } else {
                // Singole date
                return new String[] {dateOrPeriod};
            }
        }
        return null;

    }

    private int[] getPagination() {
        String pageParam = request.getParameter("page");
        String pageSizeParam = request.getParameter("pageSize");
        int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
        currentPage = page;
        pageSize = (pageSizeParam != null) ? Integer.parseInt(pageSizeParam) : 6;
        int offset = (page - 1) * pageSize;
        return new int[] {currentPage,offset};
    }

    /**
     * Retrieves a list of event selections based on the current page language.
     * 
     * @return a list of EventSelection objects
     */
    public List<EventSelection> getEventSelections() {
        String language = ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource);
        List<EventSelection> selections = new ArrayList<>();

        if (language.equals("it")) {
            selections.add(new EventSelection(ET_COURSE, "Corso"));
            selections.add(new EventSelection(ET_EVENT, "Evento"));
            selections.add(new EventSelection(ET_WEBINAR, "Webinar"));
            selections.add(new EventSelection(EF_LIVE_STREAM, "Streaming Live"));
            selections.add(new EventSelection(EF_RESIDENTIAL, "Residenziale"));
        } else if (language.equals("es")) {
            selections.add(new EventSelection(ET_COURSE, "Curso"));
            selections.add(new EventSelection(ET_EVENT, "Evento"));
            selections.add(new EventSelection(ET_WEBINAR, "Webinar"));
            selections.add(new EventSelection(EF_LIVE_STREAM, "Transmisión en Vivo"));
            selections.add(new EventSelection(EF_RESIDENTIAL, "Residencial"));
        } else {
            selections.add(new EventSelection(ET_COURSE, "Course"));
            selections.add(new EventSelection(ET_EVENT, "Event"));
            selections.add(new EventSelection(ET_WEBINAR, "Webinar"));
            selections.add(new EventSelection(EF_LIVE_STREAM, "Live Streaming"));
            selections.add(new EventSelection(EF_RESIDENTIAL, "Residential"));
        }

        return selections;
    }

    /**
     * Retrieves a list of events based on the current filters and pagination settings.
     * 
     * @return a list of Event objects
     */
    public List<EventModelDto> getEvents() {
        String language = ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource);
        List<String> topicsFilter = this.getTopicFilters();
        List<String> eventTypeFilter = this.getEventTypeFilter();
        List<String> citiesFilter = this.getCityFilter();
        String scheduleTypeFilter = this.getScheduledFilter();
        String startData = "";
        String endDate = "";
        String[] dateFilter = this.getDateFilter();
        
        if(dateFilter != null) {
            if(dateFilter.length >1) {
                startData = dateFilter[0];
                endDate = dateFilter[1];
            } else if(dateFilter.length == 1) {
                startData = dateFilter[0];
            }
        }
        int[] pagination= this.getPagination();
        EventModelReturnDto eventData = eventListingService.getEvents(
                resourceResolver,
                language,
                topicsFilter,
                eventTypeFilter,
                citiesFilter,
                scheduleTypeFilter,
                startData,
                endDate,
                pagination[0],
                pagination[1]
        );
        totalResults = eventData.getTotalMatches();
        totalPages = (int) Math.ceil((double) totalResults / pageSize);

        return eventData.getEvents();
    }


    public static class EventSelection {
        private String id;
        private String name;

        public EventSelection(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
    
    // Getter pagination data
    public int getCurrentPage() {
        return currentPage;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public int getTotalResults() {
        return totalResults;
    }

    // Navigation Methods
    public int getPreviousPage() {
        return currentPage - 1;
    }

    public int getNextPage() {
        return currentPage + 1;
    }

    public boolean getHasPreviousPage() {
        return currentPage > 1;
    }

    public boolean getHasNextPage() {
        return currentPage < totalPages;
    }

    // Pagination indexes
    public int getStartIndex() {
        return (currentPage - 1) * pageSize + 1;
    }

    public int getEndIndex() {
        int calculatedEnd = currentPage * pageSize;
        return Math.min(calculatedEnd, totalResults);
    }


}
