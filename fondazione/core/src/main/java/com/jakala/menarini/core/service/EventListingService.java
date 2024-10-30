package com.jakala.menarini.core.service;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.jakala.menarini.core.dto.EventModelDto;
import com.jakala.menarini.core.dto.EventModelRetrunDto;
import com.jakala.menarini.core.models.ModelHelper;
import com.jakala.menarini.core.service.interfaces.EventListingServiceInterface;
import com.jakala.menarini.core.service.utils.EventListingServiceConfiguration;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.text.SimpleDateFormat;
import java.util.*;



@Component(
        service = EventListingServiceInterface.class,
        immediate = true
)
@Designate(ocd = EventListingServiceConfiguration.class)
public class EventListingService implements EventListingServiceInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventListingService.class);

    private final  Map<String, String> citiesMap = new HashMap<>();
    private final  Map<String, String> topicsMap = new HashMap<>();

    private String eventPath;
    private String topicPath;
    private String cityPath;
    private static final String EF_LIVE_STREAM = "liveStreaming";
    private static final String EF_RESIDENTIAL = "residential";

    private String qStartDate;
    private String qEndDate;


    @Activate
    private void activate(EventListingServiceConfiguration config) {
        this.eventPath = config.eventPath();
        this.topicPath = config.topicPath();
        this.cityPath = config.cityPath();
        this.qStartDate = config.startDateProperty();
        this.qEndDate = config.endDateProperty();

    }

    @Override
    public EventModelRetrunDto getEvents(
            ResourceResolver resolver,
            String language,
            List<String> topicsFilterIds,
            List<String> eventTypesFilterIds,
            List<String> citiesFilterIds,
            String statusFilter,
            String startDate,
            String endDate,
            int page,
            int pageSlot
    ) {
        Map<String, String> predicate = this.buildBasePredicate();
        if(!topicsFilterIds.isEmpty()){
            this.addTopicFilterToPredicate(predicate,topicsFilterIds,resolver);
        }
        if(eventTypesFilterIds !=null && !eventTypesFilterIds.isEmpty()) {
            this.addEventTypeFilterToPredicate(predicate,eventTypesFilterIds);
        }
        if(citiesFilterIds != null && !citiesFilterIds.isEmpty()) {
            this.addCityFilterToPredicate(predicate,citiesFilterIds,resolver);
        }
        if(statusFilter != null && !statusFilter.isEmpty()) {
            this.addScheduledFilterToPredicate(predicate,statusFilter);
        }
        if(!startDate.isEmpty() || !endDate.isEmpty()) {
            this.addDateFilterToPredicate(predicate,startDate,endDate);
        }
        int actPage = page <= 0 ? 1 : page;
        int slotSize = pageSlot <= 0 ? 6 : pageSlot;
        this.addPaginationToPredicate(predicate,actPage,slotSize);
        return this.getFilteredEvents(predicate,resolver,language);
    }

    private Map<String, String> buildBasePredicate() {
        Map<String, String> predicate = new HashMap<>();
        predicate.put("type", DamConstants.NT_DAM_ASSET);
        predicate.put("path", this.eventPath);
        predicate.put("p.limit", "-1");
        predicate.put("orderby", "@" + JcrConstants.JCR_CONTENT + this.qEndDate);
        predicate.put("orderby.sort", "desc");

        return predicate;
    }


    @Override
    public List<EventModelDto> getEventsByIds(
            List<String> eventIds,
            ResourceResolver resolver,
            String language
    ) {
        ArrayList<EventModelDto> events = new ArrayList<>();
        try {
            List<Hit> hits = ModelHelper.findResourceByIds(resolver,eventIds, eventPath);
            for (Hit hitData : hits) {
                Resource resource = hitData.getResource();
                events.add(buildEvent(resource,language,resolver));
            }
        } catch (RepositoryException e) {
            return events;
        }
        return events;
    }



    private EventModelRetrunDto getFilteredEvents(
            Map<String, String> predicate,
            ResourceResolver resolver,
            String language

    ){
        int matches = 0;
        EventModelRetrunDto returnData = new EventModelRetrunDto();
        List<EventModelDto> filteredEvents = new ArrayList<>();
        QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);
        Session session = resolver.adaptTo(Session.class);
        Query query = queryBuilder.createQuery(PredicateGroup.create(predicate), session);
        if(query != null) {
            LOGGER.error("============================= GET EVENT : query created ==============");
            SearchResult result = query.getResult();
            LOGGER.error("============================= GET EVENT : result size ==============");
            matches = (int)result.getTotalMatches();
            for (Hit hit : result.getHits()) {
                LOGGER.error("============================= GET EVENT : on process hits ==============");

                try {
                    Resource resource = hit.getResource();
                    EventModelDto event = buildEvent(resource, language,resolver);
                    if (event != null) {
                        filteredEvents.add(event);
                    }
                } catch (RepositoryException e) {
                    LOGGER.error("============================= GET EVENT : ERROR ON HIT ==============");

                    LOGGER.error("Error while retrieving event", e);
                }
            }
        }
        returnData.setEvents(filteredEvents);
        returnData.setTotalMatches(matches);
        return returnData;
    }


    private void addTopicFilterToPredicate(
            Map<String, String> predicate,
            List<String> topicsFilterIds,
            ResourceResolver resolver
    ) {
        if (topicsFilterIds != null && !topicsFilterIds.isEmpty()) {
            boolean addOr = false;
            for (int i = 0; i < topicsFilterIds.size(); i++) {
                Resource topic = null;
                try {
                    topic = ModelHelper.findResourceById(resolver, topicsFilterIds.get(i), topicPath);
                } catch (RepositoryException e) {
                    LOGGER.error("{} not found", topicsFilterIds.get(i));
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

    private void addEventTypeFilterToPredicate(
            Map<String, String> predicate,
            List<String> eventTypeFilter

    ) {
        if (eventTypeFilter != null && !eventTypeFilter.isEmpty()) {
            boolean addOr = false;
            boolean addBothFormats = false;
            for (int i = 0; i < eventTypeFilter.size(); i++) {
                if (eventTypeFilter.get(i).equals(EF_LIVE_STREAM) || eventTypeFilter.get(i).equals(EF_RESIDENTIAL)) {
                    addBothFormats = true;
                    predicate.put("group.2_group." + (i + 1) + "_property", JcrConstants.JCR_CONTENT + "/data/master/format");
                    predicate.put("group.2_group." + (i + 1) + "_property.value", eventTypeFilter.get(i));
                } else {
                    predicate.put("group.2_group." + (i + 1) + "_property", JcrConstants.JCR_CONTENT + "/data/master/eventType");
                    predicate.put("group.2_group." + (i + 1) + "_property.value", eventTypeFilter.get(i));
                }
                if (i == 1) {
                    addOr = true;
                }
            }
            if (addBothFormats) {
                predicate.put("group.2_group." + (eventTypeFilter.size() + 1) + "_property", JcrConstants.JCR_CONTENT + "/data/master/format");
                predicate.put("group.2_group." + (eventTypeFilter.size() + 1) + "_property.value", "inSiteAndStreaming");
            }
            // Set OR operator between groups
            if (addOr) {
                predicate.put("group.2_group.p.or", "true");
            }
        }
    }

    private void addPaginationToPredicate(
            Map<String, String> predicate,
            int page,
            int pageSlot
        ) {
        
        int offset = (page - 1) * pageSlot;
        predicate.put("p.offset", String.valueOf(offset));
        predicate.put("p.limit", String.valueOf(pageSlot));
    }


    private void addCityFilterToPredicate(
            Map<String, String> predicate,
            List<String> locationFilter,
            ResourceResolver resolver
    ) {
        if (locationFilter != null && !locationFilter.isEmpty()) {
            boolean addOr = false;
            for (int i = 0; i < locationFilter.size(); i++) {
                Resource city = null;
                try {
                    city = ModelHelper.findResourceById(resolver, locationFilter.get(i), cityPath);
                } catch (RepositoryException e) {
                    LOGGER.error("{} not found", locationFilter.get(i));
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


    private void addScheduledFilterToPredicate(
            Map<String, String> predicate,
            String eventStatus
    ) {
        if (eventStatus != null && !eventStatus.isEmpty()) {
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            if (eventStatus.equals("scheduled")) {
                predicate.put("group.3_daterange.property", JcrConstants.JCR_CONTENT + qStartDate);
                predicate.put("group.3_daterange.lowerOperation", ">=");
                predicate.put("group.3_daterange.lowerBound", today);
            } else {
                predicate.put("group.3_daterange.property", JcrConstants.JCR_CONTENT + qEndDate);
                predicate.put("group.3_daterange.upperOperation", "<=");
                predicate.put("group.3_daterange.upperBound", today);
            }
        }
    }


    private void addDateFilterToPredicate(
            Map<String, String> predicate,
            String startDate,
            String endDate
    ) {
            if (startDate != null && endDate != null) {
                // Gruppo 4: L'evento inizia durante il periodo selezionato
                predicate.put("group.4_group.1_daterange.property", JcrConstants.JCR_CONTENT + qStartDate);
                predicate.put("group.4_group.1_daterange.lowerOperation", ">=");
                predicate.put("group.4_group.1_daterange.lowerBound", startDate);
                predicate.put("group.4_group.1_daterange.upperOperation", "<=");
                predicate.put("group.4_group.1_daterange.upperBound", endDate);

                // OPPURE L'evento finisce durante il periodo selezionato
                predicate.put("group.4_group.2_daterange.property", JcrConstants.JCR_CONTENT + qEndDate);
                predicate.put("group.4_group.2_daterange.lowerOperation", ">=");
                predicate.put("group.4_group.2_daterange.lowerBound", startDate);
                predicate.put("group.4_group.2_daterange.upperOperation", "<=");
                predicate.put("group.4_group.2_daterange.upperBound", endDate);

                // Imposta l'operatore OR tra i gruppi
                predicate.put("group.4_group.p.or", "true");
            } else {
                // Singola data
                String selectedDate = startDate != null ? startDate : endDate;
                // Gruppo 4: La data selezionata cade tra la data di inizio e fine dell'evento
                predicate.put("group.1_daterange.property", JcrConstants.JCR_CONTENT + qStartDate);
                predicate.put("group.1_daterange.upperOperation", "<=");
                predicate.put("group.1_daterange.upperBound", selectedDate);
                predicate.put("group.2_daterange.property", JcrConstants.JCR_CONTENT + qEndDate);
                predicate.put("group.2_daterange.lowerOperation", ">=");
                predicate.put("group.2_daterange.lowerBound", selectedDate);
            }

    }








    private EventModelDto buildEvent(Resource resource, String language, ResourceResolver resolver) {
        ContentFragment fragment = resource.adaptTo(ContentFragment.class);
        if (fragment != null) {
            String id = fragment.getElement("id").getContent();
            LOGGER.error("=================== GET EVENT: build event ============================");
            String title = ModelHelper.getLocalizedElementValue(fragment, language, "title", fragment.getTitle());
            String description = ModelHelper.getLocalizedElementValue(fragment, language, "description", fragment.getDescription());
            String startDateStr = fragment.getElement("startDate").getContent();
            String endDateStr = fragment.getElement("endDate").getContent();
            String presentationImage = fragment.getElement("presentationImage").getContent();

            FragmentData fragmentData = fragment.getElement("topics").getValue();
            Object elementContent = fragmentData.getValue();
            String topics = null;
            if (elementContent instanceof String[] && ((String[]) elementContent).length > 0) {
                StringBuilder topicsBuilder = new StringBuilder();
                for (String topic : (String[]) elementContent) {
                    String topicName = getTopicName(topic, language, resolver);
                    if (topicsBuilder.length() > 0) {
                        topicsBuilder.append(" / ");
                    }
                    topicsBuilder.append(topicName);
                }
                topics = topicsBuilder.toString();
            }
            String eventType = fragment.getElement("eventType").getContent();
            String location = fragment.getElement("city").getContent();
            location = getLocationName(location, language,resolver);

            return new EventModelDto(id, title, description, resource.getPath(), startDateStr, endDateStr, topics, eventType, location, presentationImage);
        } else {
            return null;
        }
    }

    private String getLocationName(String location, String language, ResourceResolver resolver) {
        if (location != null && !location.isEmpty()) {
            if (citiesMap.containsKey(location)) {
                return citiesMap.get(location);
            } else {
                ContentFragment fragment = findFragmentByPath(location,resolver);
                if (fragment != null) {
                    String name = ModelHelper.getLocalizedElementValue(fragment, language, "name", null);
                    citiesMap.put(location, name);
                    return name;
                }
            }
        }
        return "";
    }


    private String getTopicName(String topic, String language, ResourceResolver resolver) {
        if (topic != null && !topic.isEmpty()) {
            if (topicsMap.containsKey(topic)) {
                return topicsMap.get(topic);
            } else {
                ContentFragment fragment = findFragmentByPath(topic,resolver);
                if (fragment != null) {
                    String name = ModelHelper.getLocalizedElementValue(fragment, language, "name", null);
                    topicsMap.put(topic, name);
                    return name;
                }
            }
        }
        return "";
    }

    private ContentFragment findFragmentByPath(String path, ResourceResolver resolver) {
        Resource fragmentResource = resolver.getResource(path);
        if (fragmentResource != null) {
            return fragmentResource.adaptTo(ContentFragment.class);
        }
        return null;
    }

}
