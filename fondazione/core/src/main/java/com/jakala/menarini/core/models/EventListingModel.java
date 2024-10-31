package com.jakala.menarini.core.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, adapters = EventListingModel.class, resourceType = "fondazione/components/fragmentlist")
public class EventListingModel {
    
    @Inject
    private ResourceResolver resourceResolver;

    @SlingObject
    private Resource currentResource;

    @ScriptVariable
    private SlingHttpServletRequest request;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventListingModel.class);

    private static Map<String, String> citiesMap = new HashMap<>();
    private static Map<String, String> topicsMap = new HashMap<>();
    
    private static final String EVENT_PATH = "/content/dam/fondazione/events/";
    private static final String TOPIC_PATH = "/content/dam/fondazione/topics/";
    private static final String CITY_PATH = "/content/dam/fondazione/cities/";
    private static final String ET_EVENT = "event";
    private static final String ET_COURSE = "course";
    private static final String ET_WEBINAR = "webinar";
    private static final String EF_LIVE_STREAM = "liveStreaming";
    private static final String EF_RESIDENTIAL = "residential";

    private static final String Q_START_DATE = "/data/master/startDate";
    private static final String Q_END_DATE = "/data/master/endDate";

    private int totalResults;
    private int currentPage;
    private int pageSize;
    private int totalPages;


    protected Map<String, String> buildBasePredicate() {
        Map<String, String> predicate = new HashMap<>();
        predicate.put("type", DamConstants.NT_DAM_ASSET);
        predicate.put("path", EVENT_PATH);
        predicate.put("p.limit", "-1");
        predicate.put("orderby", "@" + JcrConstants.JCR_CONTENT + Q_END_DATE);
        predicate.put("orderby.sort", "desc");

        return predicate;
    }

    protected void addTopicFilterToPredicate(Map<String, String> predicate) {
        String[] topicsFilter = request.getParameterValues("topics");
        if (topicsFilter != null && topicsFilter.length == 1 && topicsFilter[0].contains("-")) {
            topicsFilter = topicsFilter[0].split("-");
        }
        if (topicsFilter != null && topicsFilter.length > 0) {
            boolean addOr = false;
            for (int i = 0; i < topicsFilter.length; i++) {
                Resource topic = null;
                try {
                    topic = ModelHelper.findResourceById(resourceResolver, topicsFilter[i], TOPIC_PATH);
                } catch (RepositoryException e) {
                    LOGGER.error("{} not found", topicsFilter[i]);
                }
                if (topic == null) {
                    continue;
                }
                predicate.put("group.1_group." + (i + 1) + "_property", JcrConstants.JCR_CONTENT + "/data/master/topics");
                predicate.put("group.1_group." + (i + 1) + "_property.value", topic.getPath());
                predicate.put("group.1_group." + (i + 1) + "_property.operation", "equals");
                if (i == 1) {
                    addOr = true;
                }
            }
            // Set OR operator between groups
            if (addOr) {
                predicate.put("group.1_group.p.or", "true");
            }
        }
    }

    protected void addEventTypeFilterToPredicate(Map<String, String> predicate) {
        String[] eventTypeFilter = request.getParameterValues("eventTypes");
        if (eventTypeFilter != null && eventTypeFilter.length == 1 && eventTypeFilter[0].contains("-")) {
            eventTypeFilter = eventTypeFilter[0].split("-");
        }
        if (eventTypeFilter != null && eventTypeFilter.length > 0) {
            boolean addOr = false;
            boolean addBothFormats = false;
            for (int i = 0; i < eventTypeFilter.length; i++) {
                if (eventTypeFilter[i].equals(EF_LIVE_STREAM) || eventTypeFilter[i].equals(EF_RESIDENTIAL)) {
                    addBothFormats = true;
                    predicate.put("group.2_group." + (i + 1) + "_property", JcrConstants.JCR_CONTENT + "/data/master/format");
                    predicate.put("group.2_group." + (i + 1) + "_property.value", eventTypeFilter[i]);
                } else {
                    predicate.put("group.2_group." + (i + 1) + "_property", JcrConstants.JCR_CONTENT + "/data/master/eventType");
                    predicate.put("group.2_group." + (i + 1) + "_property.value", eventTypeFilter[i]);
                }
                if (i == 1) {
                    addOr = true;
                }
            }
            if (addBothFormats) {
                predicate.put("group.2_group." + (eventTypeFilter.length + 1) + "_property", JcrConstants.JCR_CONTENT + "/data/master/format");
                predicate.put("group.2_group." + (eventTypeFilter.length + 1) + "_property.value", "inSiteAndStreaming");
            }
            // Set OR operator between groups
            if (addOr) {
                predicate.put("group.2_group.p.or", "true");
            }
        }
    }

    protected void addCityFilterToPredicate(Map<String, String> predicate) {
        String[] locationFilter = request.getParameterValues("locations");
        if (locationFilter != null && locationFilter.length == 1 && locationFilter[0].contains("-")) {
            locationFilter = locationFilter[0].split("-");
        }
        if (locationFilter != null && locationFilter.length > 0) {
            boolean addOr = false;
            for (int i = 0; i < locationFilter.length; i++) {
                Resource city = null;
                try {
                    city = ModelHelper.findResourceById(resourceResolver, locationFilter[i], CITY_PATH);
                } catch (RepositoryException e) {
                    LOGGER.error("{} not found", locationFilter[i]);
                }
                if (city == null) {
                    continue;
                }
                predicate.put("group.3_group." + (i + 1) + "_property", JcrConstants.JCR_CONTENT + "/data/master/city");
                predicate.put("group.3_group." + (i + 1) + "_property.value", city.getPath());
                predicate.put("group.3_group." + (i + 1) + "_property.operation", "equals");
                if (i == 1) {
                    addOr = true;
                }
            }
            // Set OR operator between groups
            if (addOr) {
                predicate.put("group.3_group.p.or", "true");
            }
        }
    }

    protected void addScheduledFilterToPredicate(Map<String, String> predicate) {
        String eventStatus = request.getParameter("eventStatus");
        if (eventStatus != null && !eventStatus.isEmpty()) {
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            if (eventStatus.equals("scheduled")) {               
                predicate.put("group.3_daterange.property", JcrConstants.JCR_CONTENT + Q_START_DATE);
                predicate.put("group.3_daterange.lowerOperation", ">=");
                predicate.put("group.3_daterange.lowerBound", today);
            } else {
                predicate.put("group.3_daterange.property", JcrConstants.JCR_CONTENT + Q_END_DATE);
                predicate.put("group.3_daterange.upperOperation", "<=");
                predicate.put("group.3_daterange.upperBound", today);
            }
        }
    }

    protected void addDateFilterToPredicate(Map<String, String> predicate) {
        String dateOrPeriod = request.getParameter("dateOrPeriod");
        if (dateOrPeriod != null && !dateOrPeriod.isEmpty()) {
            if (dateOrPeriod.contains("-to-")) {
                dateOrPeriod = dateOrPeriod.replace("-to-", " to ");
            }
            if (dateOrPeriod.contains(" to ")) {
                String[] dates = dateOrPeriod.split(" to ");
                String fromDate = dates[0];
                String toDate = dates[1];

                // Gruppo 4: L'evento inizia durante il periodo selezionato
                predicate.put("group.4_group.1_daterange.property", JcrConstants.JCR_CONTENT + Q_START_DATE);
                predicate.put("group.4_group.1_daterange.lowerOperation", ">=");
                predicate.put("group.4_group.1_daterange.lowerBound", fromDate);
                predicate.put("group.4_group.1_daterange.upperOperation", "<=");
                predicate.put("group.4_group.1_daterange.upperBound", toDate);
                
                // OPPURE L'evento finisce durante il periodo selezionato
                predicate.put("group.4_group.2_daterange.property", JcrConstants.JCR_CONTENT + Q_END_DATE);
                predicate.put("group.4_group.2_daterange.lowerOperation", ">=");
                predicate.put("group.4_group.2_daterange.lowerBound", fromDate);
                predicate.put("group.4_group.2_daterange.upperOperation", "<=");
                predicate.put("group.4_group.2_daterange.upperBound", toDate);
                
                // Imposta l'operatore OR tra i gruppi
                predicate.put("group.4_group.p.or", "true");
            } else {
                // Singola data
                String selectedDate = dateOrPeriod;
                
                // Gruppo 4: La data selezionata cade tra la data di inizio e fine dell'evento
                predicate.put("group.1_daterange.property", JcrConstants.JCR_CONTENT + Q_START_DATE);
                predicate.put("group.1_daterange.upperOperation", "<=");
                predicate.put("group.1_daterange.upperBound", selectedDate);
                predicate.put("group.2_daterange.property", JcrConstants.JCR_CONTENT + Q_END_DATE);
                predicate.put("group.2_daterange.lowerOperation", ">=");
                predicate.put("group.2_daterange.lowerBound", selectedDate);
            }
        }
    }

    protected void addPaginationToPredicate(Map<String, String> predicate) {
        String pageParam = request.getParameter("page");
        String pageSizeParam = request.getParameter("pageSize");
        int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
        currentPage = page;
        pageSize = (pageSizeParam != null) ? Integer.parseInt(pageSizeParam) : 6;
        int offset = (page - 1) * pageSize;

        predicate.put("p.offset", String.valueOf(offset));
        predicate.put("p.limit", String.valueOf(pageSize));
    }

    private Event buildEvent(Resource resource, String language) {
        ContentFragment fragment = resource.adaptTo(ContentFragment.class);
        if (fragment != null) {
            String id = fragment.getElement("id").getContent();
            String title = ModelHelper.getLocalizedElementValue(fragment, language, "title", fragment.getTitle());
            String description = ModelHelper.getLocalizedElementValue(fragment, language, "description", fragment.getDescription());
            String startDateStr = fragment.getElement("startDate").getContent();
            String endDateStr = fragment.getElement("endDate").getContent();
            String presentationImage = fragment.getElement("presentationImage").getContent();
            
            FragmentData fragmentData = fragment.getElement("topics").getValue();
            Object elementContent = fragmentData.getValue();
            String topics = null;
            if (elementContent instanceof String[] && ((String[])elementContent).length > 0) {
                StringBuilder topicsBuilder = new StringBuilder();
                for (String topic : (String[]) elementContent) {
                    String topicName = getTopicName(topic, language);
                    if (topicsBuilder.length() > 0) {
                        topicsBuilder.append(" / ");
                    }
                    topicsBuilder.append(topicName);
                }
                topics = topicsBuilder.toString();
            } 
            String eventType = fragment.getElement("eventType").getContent();
            String location = fragment.getElement("city").getContent();
            location = getLocationName(location, language);

            return new Event(id, title, description, resource.getPath(), startDateStr, endDateStr, topics, eventType, location, presentationImage);
        } else {
            return null;
        }
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
    public List<Event> getEvents() {

        String language = ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource);
        QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
        Session session = resourceResolver.adaptTo(Session.class);
        
        Map<String, String> predicate = buildBasePredicate();

        // Filter by topics selection
        addTopicFilterToPredicate(predicate);

        // Filter by eventType
        addEventTypeFilterToPredicate(predicate);

        // Filter by location (citta/nazione)
        addCityFilterToPredicate(predicate);

        // Filter by scheduled or past events
        addScheduledFilterToPredicate(predicate);

        // Filter by date range
        addDateFilterToPredicate(predicate);
        
        // Pagination
        addPaginationToPredicate(predicate);

        // Execute the query
        Query query = queryBuilder.createQuery(PredicateGroup.create(predicate), session);

        // DEBUG
        // String debugQuery = ModelHelper.convertPredicatesToDebugFormat(predicate);
        // LOGGER.info(debugQuery);

        SearchResult result = query.getResult();

        List<Event> filteredEvents = new ArrayList<>();
        for (Hit hit : result.getHits()) {
            try {
                Resource resource = hit.getResource();
                Event event = buildEvent(resource, language);
                if (event != null) {
                    filteredEvents.add(event);
                }
            } catch (RepositoryException e) {
                LOGGER.error("Error while retrieving event", e);
            }
        }

        totalResults = (int)result.getTotalMatches();
        totalPages = (int) Math.ceil((double) totalResults / pageSize);

        return filteredEvents;
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

    public static class Event {
        private String id;
        private String title;
        private String description;
        private String path;
        private Date startDate;
        private Date endDate;
        private String startDateText;
        private String endDateText;
        private String topics;
        private String eventType;
        private String location;
        private String presentationImage;

        public Event(String id, String title, String description, String path, String startDateStr, String endDateStr, String topics, String eventType, String location, String presentationImage) {
            this.id = id;
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
                LOGGER.error("Error while parsing date", e);
                this.startDate = null;
                this.endDate = null;
            }
            this.topics = topics;
            this.eventType = eventType;
            this.location = location;
            this.presentationImage = presentationImage;
        }

        public String getId() {
            return id;
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
            return startDate != null ? new Date(startDate.getTime()) : null;
        }

        public Date getEndDate() {
            return endDate != null ? new Date(endDate.getTime()) : null;
        }

        public String getStartDateText() {
            return startDateText;
        }

        public String getEndDateText() {
            return endDateText;
        }

        public String getTopics() {
            return topics;
        }

        public String getEventType() {
            return eventType;
        }

        public String getLocation() {
            return location;
        }

        public String getPresentationImage() {
            return presentationImage;
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

    private String getTopicName(String topic, String language) {
        if (topic != null && !topic.isEmpty()) {
            if (topicsMap.containsKey(topic)) {
                return topicsMap.get(topic);
            } else {
                ContentFragment fragment = findFragmentByPath(topic);
                if (fragment != null) {
                    String name = ModelHelper.getLocalizedElementValue(fragment, language, "name", null);
                    topicsMap.put(topic, name);
                    return name;
                }
            }
        } 
        return "";
    }

    private String getLocationName(String location, String language) {
        if (location != null && !location.isEmpty()) {
            if (citiesMap.containsKey(location)) {
                return citiesMap.get(location);
            } else {
                ContentFragment fragment = findFragmentByPath(location);
                if (fragment != null) {
                    String name = ModelHelper.getLocalizedElementValue(fragment, language, "name", null);
                    citiesMap.put(location, name);
                    return name;
                }
            }
        } 
        return "";
    }

    /**
     * Find the fragment by path
     */
    private ContentFragment findFragmentByPath(String path) {
        Resource fragmentResource = resourceResolver.getResource(path);
        if (fragmentResource != null) {
            return fragmentResource.adaptTo(ContentFragment.class);
        }
        return null;
    }

}
