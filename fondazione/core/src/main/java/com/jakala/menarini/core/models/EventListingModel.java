package com.jakala.menarini.core.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
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

    private static final Logger LOG = LoggerFactory.getLogger(EventListingModel.class);

    private static Map<String, String> citiesMap = new HashMap<>();
    private static Map<String, String> topicsMap = new HashMap<>();
    
    private static final String EVENT_PATH = "/content/dam/fondazione/events/";
    private static final String TOPIC_PATH = "/content/dam/fondazione/topics/";
    private static final String CITY_PATH = "/content/dam/fondazione/cities/";

    private int totalResults;
    private int currentPage;
    private int pageSize;
    private int totalPages;

    @PostConstruct
    protected void init() {
    }

    private Map<String, String> buildBasePredicate() {
        Map<String, String> predicate = new HashMap<>();
        predicate.put("type", "dam:Asset");
        predicate.put("path", EVENT_PATH);
        predicate.put("p.limit", "-1");
        predicate.put("orderby", "@jcr:content/data/master/data_fine");
        predicate.put("orderby.sort", "desc");

        return predicate;
    }


    private void addTopicFilterToPredicate(Map<String, String> predicate) {
        String[] topicsFilter = request.getParameterValues("topics");
        if (topicsFilter != null && topicsFilter.length == 1 && topicsFilter[0].contains(",")) {
            topicsFilter = topicsFilter[0].split(",");
        }
        if (topicsFilter != null && topicsFilter.length > 0) {
            boolean addOr = false;
            for (int i = 0; i < topicsFilter.length; i++) {
                predicate.put("group.1_group." + (i + 1) + "_property.value", TOPIC_PATH + topicsFilter[i] + "-%");
                predicate.put("group.1_group." + (i + 1) + "_property", "jcr:content/data/master/topics");
                predicate.put("group.1_group." + (i + 1) + "_property.operation", "like");
                addOr = true;
            }
            // Imposta l'operatore OR tra i gruppi
            if (addOr) {
                predicate.put("group.1_group.p.or", "true");
            }
        }
    }

    private void addEventTypeFilterToPredicate(Map<String, String> predicate) {
        String eventTypeFilter = request.getParameter("eventType");
        if (eventTypeFilter != null && !eventTypeFilter.isEmpty()) {
            predicate.put("group.2_property", "jcr:content/data/master/eventType");
            predicate.put("group.2_property.value", eventTypeFilter);
        }
    }

    private void addCityFilterToPredicate(Map<String, String> predicate) {
        String locationFilter = request.getParameter("location");
        if (locationFilter != null && !locationFilter.isEmpty()) {
            predicate.put("group.3_property", "jcr:content/data/master/citta");
            predicate.put("group.3_property.value", CITY_PATH + locationFilter + "-%");
            predicate.put("group.3_property.operation", "like");
        }
    }

    private void addDateFilterToPredicate(Map<String, String> predicate) {
        String dateOrPeriod = request.getParameter("dateOrPeriod");
        if (dateOrPeriod != null && !dateOrPeriod.isEmpty()) {
            if (dateOrPeriod.contains(" to ")) {
                String[] dates = dateOrPeriod.split(" to ");
                String fromDate = dates[0];
                String toDate = dates[1];

                // Gruppo 4: L'evento inizia durante il periodo selezionato
                predicate.put("group.4_group.1_daterange.property", "jcr:content/data/master/data_inizio");
                predicate.put("group.4_group.1_daterange.lowerOperation", ">=");
                predicate.put("group.4_group.1_daterange.lowerBound", fromDate);
                predicate.put("group.4_group.1_daterange.upperOperation", "<=");
                predicate.put("group.4_group.1_daterange.upperBound", toDate);
                
                // OPPURE L'evento finisce durante il periodo selezionato
                predicate.put("group.4_group.2_daterange.property", "jcr:content/data/master/data_fine");
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
                predicate.put("group.1_daterange.property", "jcr:content/data/master/data_inizio");
                predicate.put("group.1_daterange.upperOperation", "<=");
                predicate.put("group.1_daterange.upperBound", selectedDate);
                predicate.put("group.2_daterange.property", "jcr:content/data/master/data_fine");
                predicate.put("group.2_daterange.lowerOperation", ">=");
                predicate.put("group.2_daterange.lowerBound", selectedDate);
            }
        }
    }

    private void addPaginationToPredicate(Map<String, String> predicate) {
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
            String title = ModelHelper.getLocalizedElementValue(fragment, language, "titolo", fragment.getTitle());
            String description = ModelHelper.getLocalizedElementValue(fragment, language, "descrizione", fragment.getDescription());
            String startDateStr = fragment.getElement("data_inizio").getContent();
            String endDateStr = fragment.getElement("data_fine").getContent();
            String presentationImage = fragment.getElement("presentation_image").getContent();
            
            FragmentData fragmentData = fragment.getElement("topics").getValue();
            Object elementContent = fragmentData.getValue();
            String topics = null;
            if (elementContent != null && elementContent instanceof String[] && ((String[])elementContent).length > 0) {
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
            String location = fragment.getElement("citta").getContent();
            location = getLocationName(location, language);

            return new Event(id, title, description, resource.getPath(), startDateStr, endDateStr, topics, eventType, location, presentationImage);
        } else {
            return null;
        }
    }

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

        // Filter by date range
        addDateFilterToPredicate(predicate);
        
        // Pagination
        addPaginationToPredicate(predicate);

        // Execute the query
        Query query = queryBuilder.createQuery(PredicateGroup.create(predicate), session);

        // DEBUG
        String debugQuery = ModelHelper.convertPredicatesToDebugFormat(predicate);
        LOG.info(debugQuery);

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
                e.printStackTrace();
            }
        }

        totalResults = (int)result.getTotalMatches();
        totalPages = (int) Math.ceil((double) totalResults / pageSize);

        return filteredEvents;
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
                e.printStackTrace();
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
    
    // Getter per i dati della paginazione
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

    // Metodi per la navigazione
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

    // Metodi per il calcolo degli indici
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
                    String name = ModelHelper.getLocalizedElementValue(fragment, language, "nome_disciplina", null);
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
