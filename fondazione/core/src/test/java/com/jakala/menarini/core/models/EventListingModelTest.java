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

    // @Test
    // void testBuildBasePredicate() {
    //     Map<String, String> predicate = eventListingModel.buildBasePredicate();
    //     assertEquals(DamConstants.NT_DAM_ASSET, predicate.get("type"));
    //     assertEquals("/content/dam/fondazione/events/", predicate.get("path"));
    //     assertEquals("-1", predicate.get("p.limit"));
    //     assertEquals("@" + JcrConstants.JCR_CONTENT + "/data/master/endDate", predicate.get("orderby"));
    //     assertEquals("desc", predicate.get("orderby.sort"));
    // }

    // @Test
    // void testAddTopicFilterToPredicate() throws RepositoryException {
    //     Map<String, String> predicate = new HashMap<>();
    //     when(request.getParameterValues("topics")).thenReturn(new String[]{"topic1-topic2"});
    //     try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
    //         mockedHelper.when(() -> ModelHelper.findResourceById(resourceResolver, "topic1", "/content/dam/fondazione/topics/"))
    //                 .thenReturn(resource);
    //         when(resource.getPath()).thenReturn("/content/dam/fondazione/topics/topic1");

    //         eventListingModel.addTopicFilterToPredicate(predicate);
    //     }
    //     assertEquals("/content/dam/fondazione/topics/topic1", predicate.get("group.1_group.1_property.value"));
    // }

    // @Test
    // void testAddEventTypeFilterToPredicate() {
    //     Map<String, String> predicate = new HashMap<>();
    //     when(request.getParameterValues("eventTypes")).thenReturn(new String[]{"eventType1-eventType2"});

    //     eventListingModel.addEventTypeFilterToPredicate(predicate);

    //     assertEquals("eventType1", predicate.get("group.2_group.1_property.value"));
    //     assertEquals("eventType2", predicate.get("group.2_group.2_property.value"));
    // }

    // @Test
    // void testAddCityFilterToPredicate() throws RepositoryException {
    //     Map<String, String> predicate = new HashMap<>();
    //     when(request.getParameterValues("locations")).thenReturn(new String[]{"city1-city2"});
    //     try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
    //         mockedHelper.when(() -> ModelHelper.findResourceById(resourceResolver, "city1", "/content/dam/fondazione/cities/"))
    //                 .thenReturn(resource);
    //         when(resource.getPath()).thenReturn("/content/dam/fondazione/cities/city1");

    //         eventListingModel.addCityFilterToPredicate(predicate);
    //     }

    //     assertEquals("/content/dam/fondazione/cities/city1", predicate.get("group.3_group.1_property.value"));
    // }

    // @Test
    // void testAddScheduledFilterToPredicate() {
    //     Map<String, String> predicate = new HashMap<>();
    //     when(request.getParameter("eventStatus")).thenReturn("scheduled");

    //     eventListingModel.addScheduledFilterToPredicate(predicate);

    //     assertEquals(">=", predicate.get("group.3_daterange.lowerOperation"));
    // }

    // @Test
    // void testAddDateFilterToPredicate() {
    //     Map<String, String> predicate = new HashMap<>();
    //     when(request.getParameter("dateOrPeriod")).thenReturn("2023-01-01 to 2023-12-31");

    //     eventListingModel.addDateFilterToPredicate(predicate);

    //     assertEquals("2023-01-01", predicate.get("group.4_group.1_daterange.lowerBound"));
    //     assertEquals("2023-12-31", predicate.get("group.4_group.1_daterange.upperBound"));
    // }

    // @Test
    // void testAddPaginationToPredicate() {
    //     Map<String, String> predicate = new HashMap<>();
    //     when(request.getParameter("page")).thenReturn("2");
    //     when(request.getParameter("pageSize")).thenReturn("10");

    //     eventListingModel.addPaginationToPredicate(predicate);

    //     assertEquals("10", predicate.get("p.limit"));
    //     assertEquals("10", predicate.get("p.offset"));
    // }

    // @Test
    // void testGetEventSelections() {
    //     List<EventListingModel.EventSelection> selections = null;
    //     try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
    //         mockedHelper.when(() -> ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource))
    //                 .thenReturn("en");

    //         selections = eventListingModel.getEventSelections();
    //     }
    //     assertEquals(5, selections.size());
    //     assertEquals("Course", selections.get(0).getName());
    // }

    // @Test
    // void testGetEvents() throws RepositoryException {
    //     List<EventListingModel.Event> events = null;
    //     when(resource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);

    //     ContentElement contentElementId = mock(ContentElement.class);
    //     ContentElement contentElementStartDate = mock(ContentElement.class);
    //     ContentElement contentElementEndDate = mock(ContentElement.class);
    //     ContentElement contentElementPresentationImage = mock(ContentElement.class);
    //     ContentElement contentElementTopics = mock(ContentElement.class);
    //     FragmentData   fragmentDataTopics = mock(FragmentData.class);
    //     ContentElement contentElementEventType = mock(ContentElement.class);
    //     ContentElement contentElementCity = mock(ContentElement.class);
    //     when(contentFragment.getElement("id")).thenReturn(contentElementId);
    //     when(contentElementId.getContent()).thenReturn("1");
    //     when(contentFragment.getElement("startDate")).thenReturn(contentElementStartDate);
    //     when(contentElementStartDate.getContent()).thenReturn("2023-01-01");
    //     when(contentFragment.getElement("endDate")).thenReturn(contentElementEndDate);
    //     when(contentElementEndDate.getContent()).thenReturn("2023-12-31");
    //     when(contentFragment.getElement("presentationImage")).thenReturn(contentElementPresentationImage);
    //     when(contentElementPresentationImage.getContent()).thenReturn("image.jpg");
    //     when(contentFragment.getElement("topics")).thenReturn(contentElementTopics);
    //     when(contentElementTopics.getValue()).thenReturn(fragmentDataTopics);
    //     when(fragmentDataTopics.getValue()).thenReturn(new String[]{"topic1", "topic2"});
    //     when(contentFragment.getElement("eventType")).thenReturn(contentElementEventType);
    //     when(contentElementEventType.getContent()).thenReturn("eventType");
    //     when(contentFragment.getElement("city")).thenReturn(contentElementCity);
    //     when(contentElementCity.getContent()).thenReturn("city");

    //     when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(queryBuilder);
    //     when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
    //     when(queryBuilder.createQuery(any(PredicateGroup.class), eq(session))).thenReturn(query);
    //     when(query.getResult()).thenReturn(searchResult);
    //     when(searchResult.getHits()).thenReturn(Collections.singletonList(hit));
    //     when(hit.getResource()).thenReturn(resource);
    //     when(resource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);

    //     try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
    //         mockedHelper.when(() -> ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource))
    //             .thenReturn("en");
    //         mockedHelper.when(() -> ModelHelper.getLocalizedElementValue(any(ContentFragment.class), anyString(), eq("title"), anyString()))
    //             .thenReturn("Event Title");
    //         mockedHelper.when(() -> ModelHelper.getLocalizedElementValue(any(ContentFragment.class), anyString(), eq("description"), anyString()))
    //             .thenReturn("Event Description");

    //         events = eventListingModel.getEvents();
    //     }
    //     assertNotNull(events);
    //     assertFalse(events.isEmpty());
    // }
}