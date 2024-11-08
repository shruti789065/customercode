package com.jakala.menarini.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.jakala.menarini.core.dto.EventModelDto;
import com.jakala.menarini.core.dto.EventModelReturnDto;
import com.jakala.menarini.core.service.interfaces.EventListingServiceInterface;

import javax.jcr.Session;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EventListingModelTest {

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Resource currentResource;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private Session session;

    @Mock
    private Query query;

    @Mock
    private SearchResult searchResult;

    @Mock
    private Hit hit;

    @Mock
    private Resource resource;

    @Mock
    private ContentFragment contentFragment;

    @Mock
    private EventListingServiceInterface eventListingService;


    @InjectMocks
    private EventListingModel eventListingModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEventSelections() {
        List<EventListingModel.EventSelection> selections = null;
        try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
            mockedHelper.when(() -> ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource)).thenReturn("en");
            selections = eventListingModel.getEventSelections();
        }
        assertEquals(5, selections.size());
        assertEquals("Course", selections.get(0).getName());
    }

    @Test
    void testGetEvents() {
        when(request.getParameterValues("topics")).thenReturn(new String[]{"topic1", "topic2"});
        when(request.getParameterValues("eventTypes")).thenReturn(new String[]{"eventType1", "eventType2"});
        when(request.getParameterValues("locations")).thenReturn(new String[]{"location1", "location2"});
        when(request.getParameter("eventStatus")).thenReturn("scheduled");
        when(request.getParameter("dateOrPeriod")).thenReturn("2023-01-01 to 2023-12-31");
        when(request.getParameter("page")).thenReturn("1");
        when(request.getParameter("pageSize")).thenReturn("10");

        EventModelReturnDto eventModelReturnDto = new EventModelReturnDto();
        eventModelReturnDto.setTotalMatches(1);
        EventModelDto eventModelDto = new EventModelDto(
                "1",
                "Event Title",
                "Event Description",
                "/content/path",
                "2023-01-01",
                "2023-12-31",
                "Topic1, Topic2",
                "EventType",
                "Location",
                "presentationImage.jpg",
                "subscription"
        );
        eventModelReturnDto.setEvents(Collections.singletonList(eventModelDto));

        when(eventListingService.getEvents(any(), anyString(), anyList(), anyList(), anyList(), anyString(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(eventModelReturnDto);

        List<EventModelDto> events = null;
        try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
            mockedHelper.when(() -> ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource)).thenReturn("en");
            events = eventListingModel.getEvents();
        }
        
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertEquals(1, events.size());
    }

    @Test
    void testPaginationMethods() {
        when(request.getParameter("page")).thenReturn("2");
        when(request.getParameter("pageSize")).thenReturn("10");

        EventModelReturnDto eventModelReturnDto = new EventModelReturnDto();
        eventModelReturnDto.setTotalMatches(100);
        List<EventModelDto> eventList = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            eventList.add(new EventModelDto(
                    String.valueOf(i),
                    "Event Title " + i,
                    "Event Description " + i,
                    "/content/path" + i,
                    "2023-01-01",
                    "2023-12-31",
                    "Topic1, Topic2",
                    "EventType",
                    "Location",
                    "presentationImage" + i + ".jpg",
                    "subscription"
            ));
        }
        eventModelReturnDto.setEvents(eventList);

        when(eventListingService.getEvents(any(), anyString(), anyList(), anyList(), anyList(), isNull(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(eventModelReturnDto);

        try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
            mockedHelper.when(() -> ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource)).thenReturn("en");
            eventListingModel.getEvents();
        }

        assertEquals(2, eventListingModel.getCurrentPage());
        assertEquals(10, eventListingModel.getPageSize());
        assertEquals(1, eventListingModel.getPreviousPage());
        assertEquals(3, eventListingModel.getNextPage());
        assertTrue(eventListingModel.getHasPreviousPage());
        assertTrue(eventListingModel.getHasNextPage());
        assertEquals(11, eventListingModel.getStartIndex());
        assertEquals(20, eventListingModel.getEndIndex());
    }
    
}