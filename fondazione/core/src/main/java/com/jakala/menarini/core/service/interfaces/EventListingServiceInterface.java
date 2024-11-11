package com.jakala.menarini.core.service.interfaces;

import com.jakala.menarini.core.dto.EventModelDto;
import com.jakala.menarini.core.dto.EventModelReturnDto;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

public interface EventListingServiceInterface {


    public EventModelReturnDto getEvents(
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
    );

    public List<EventModelDto> getEventsByIds(
            List<String> eventIds,
            ResourceResolver resolver,
            String language
    );

    public EventModelDto getEventBySlug(
            String slug,
            ResourceResolver resolver,
            String language
    );


}
