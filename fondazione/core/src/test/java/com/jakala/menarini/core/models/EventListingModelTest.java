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

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @InjectMocks
    private EventListingModel eventListingModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBuildBasePredicate() {
        Map<String, String> predicate = eventListingModel.buildBasePredicate();
        assertEquals(DamConstants.NT_DAM_ASSET, predicate.get("type"));
        assertEquals("/content/dam/fondazione/events/", predicate.get("path"));
        assertEquals("-1", predicate.get("p.limit"));
        assertEquals("@" + JcrConstants.JCR_CONTENT + "/data/master/endDate", predicate.get("orderby"));
        assertEquals("desc", predicate.get("orderby.sort"));
    }

    @Test
    void testAddTopicFilterToPredicate() throws RepositoryException {
        Map<String, String> predicate = new HashMap<>();
        when(request.getParameterValues("topics")).thenReturn(new String[]{"topic1-topic2"});
        try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
            mockedHelper.when(() -> ModelHelper.findResourceById(resourceResolver, "topic1", "/content/dam/fondazione/topics/"))
                    .thenReturn(resource);
            when(resource.getPath()).thenReturn("/content/dam/fondazione/topics/topic1");

            eventListingModel.addTopicFilterToPredicate(predicate);
        }
        assertEquals("/content/dam/fondazione/topics/topic1", predicate.get("group.1_group.1_property.value"));
    }

    @Test
    void testAddEventTypeFilterToPredicate() {
        Map<String, String> predicate = new HashMap<>();
        when(request.getParameterValues("eventTypes")).thenReturn(new String[]{"eventType1-eventType2"});

        eventListingModel.addEventTypeFilterToPredicate(predicate);

        assertEquals("eventType1", predicate.get("group.2_group.1_property.value"));
        assertEquals("eventType2", predicate.get("group.2_group.2_property.value"));
    }

    @Test
    void testAddCityFilterToPredicate() throws RepositoryException {
        Map<String, String> predicate = new HashMap<>();
        when(request.getParameterValues("locations")).thenReturn(new String[]{"city1-city2"});
        try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
            mockedHelper.when(() -> ModelHelper.findResourceById(resourceResolver, "city1", "/content/dam/fondazione/cities/"))
                    .thenReturn(resource);
            when(resource.getPath()).thenReturn("/content/dam/fondazione/cities/city1");

            eventListingModel.addCityFilterToPredicate(predicate);
        }

        assertEquals("/content/dam/fondazione/cities/city1", predicate.get("group.3_group.1_property.value"));
    }

    @Test
    void testAddScheduledFilterToPredicate() {
        Map<String, String> predicate = new HashMap<>();
        when(request.getParameter("eventStatus")).thenReturn("scheduled");

        eventListingModel.addScheduledFilterToPredicate(predicate);

        assertEquals(">=", predicate.get("group.3_daterange.lowerOperation"));
    }

    @Test
    void testAddDateFilterToPredicate() {
        Map<String, String> predicate = new HashMap<>();
        when(request.getParameter("dateOrPeriod")).thenReturn("2023-01-01 to 2023-12-31");

        eventListingModel.addDateFilterToPredicate(predicate);

        assertEquals("2023-01-01", predicate.get("group.4_group.1_daterange.lowerBound"));
        assertEquals("2023-12-31", predicate.get("group.4_group.1_daterange.upperBound"));
    }

    @Test
    void testAddPaginationToPredicate() {
        Map<String, String> predicate = new HashMap<>();
        when(request.getParameter("page")).thenReturn("2");
        when(request.getParameter("pageSize")).thenReturn("10");

        eventListingModel.addPaginationToPredicate(predicate);

        assertEquals("10", predicate.get("p.limit"));
        assertEquals("10", predicate.get("p.offset"));
    }

    // @Test
    // void testBuildEvent() {
    //     ContentElement contentElementId = mock(ContentElement.class);
    //     ContentElement contentElementTitle = mock(ContentElement.class);
    //     ContentElement contentElementDescription = mock(ContentElement.class);
    //     ContentElement contentElementStartDate = mock(ContentElement.class);
    //     ContentElement contentElementEndDate = mock(ContentElement.class);
    //     ContentElement contentElementPresentationImage = mock(ContentElement.class);
    //     FragmentData   fragmentDataTopics = mock(FragmentData.class);
    //     ContentElement contentElementTopics = mock(ContentElement.class);
    //     ContentElement contentElementEventType = mock(ContentElement.class);
    //     ContentElement contentElementCity = mock(ContentElement.class);

    //     when(resource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
    //     when(contentFragment.getElement("id")).thenReturn(contentElementId);
    //     when(contentElementId.getContent()).thenReturn("1");
    //     when(contentFragment.getElement("title")).thenReturn(contentElementTitle);
    //     when(contentElementTitle.getContent()).thenReturn("Event Title");
    //     when(contentFragment.getElement("description")).thenReturn(contentElementDescription);
    //     when(contentElementDescription.getContent()).thenReturn("Event Description");
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

    //     EventListingModel.Event event = eventListingModel.buildEvent(resource, "en");

    //     assertNotNull(event);
    //     assertEquals("1", event.getId());
    //     assertEquals("Event Title", event.getTitle());
    //     assertEquals("Event Description", event.getDescription());
    //     assertEquals("2023-01-01", new SimpleDateFormat("yyyy-MM-dd").format(event.getStartDate()));
    //     assertEquals("2023-12-31", new SimpleDateFormat("yyyy-MM-dd").format(event.getEndDate()));
    //     assertEquals("image.jpg", event.getPresentationImage());
    // }

    @Test
    void testGetEventSelections() {
        List<EventListingModel.EventSelection> selections = null;
        try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
            mockedHelper.when(() -> ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource))
                    .thenReturn("en");

            selections = eventListingModel.getEventSelections();
        }
        assertEquals(5, selections.size());
        assertEquals("Course", selections.get(0).getName());
    }

    @Test
    void testGetEvents() throws RepositoryException {
        List<EventListingModel.Event> events = null;
        when(resource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);

        ContentElement contentElementId = mock(ContentElement.class);
        ContentElement contentElementStartDate = mock(ContentElement.class);
        ContentElement contentElementEndDate = mock(ContentElement.class);
        ContentElement contentElementPresentationImage = mock(ContentElement.class);
        ContentElement contentElementTopics = mock(ContentElement.class);
        FragmentData   fragmentDataTopics = mock(FragmentData.class);
        ContentElement contentElementEventType = mock(ContentElement.class);
        ContentElement contentElementCity = mock(ContentElement.class);
        when(contentFragment.getElement("id")).thenReturn(contentElementId);
        when(contentElementId.getContent()).thenReturn("1");
        when(contentFragment.getElement("startDate")).thenReturn(contentElementStartDate);
        when(contentElementStartDate.getContent()).thenReturn("2023-01-01");
        when(contentFragment.getElement("endDate")).thenReturn(contentElementEndDate);
        when(contentElementEndDate.getContent()).thenReturn("2023-12-31");
        when(contentFragment.getElement("presentationImage")).thenReturn(contentElementPresentationImage);
        when(contentElementPresentationImage.getContent()).thenReturn("image.jpg");
        when(contentFragment.getElement("topics")).thenReturn(contentElementTopics);
        when(contentElementTopics.getValue()).thenReturn(fragmentDataTopics);
        when(fragmentDataTopics.getValue()).thenReturn(new String[]{"topic1", "topic2"});
        when(contentFragment.getElement("eventType")).thenReturn(contentElementEventType);
        when(contentElementEventType.getContent()).thenReturn("eventType");
        when(contentFragment.getElement("city")).thenReturn(contentElementCity);
        when(contentElementCity.getContent()).thenReturn("city");

        when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(queryBuilder);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(queryBuilder.createQuery(any(PredicateGroup.class), eq(session))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        when(searchResult.getHits()).thenReturn(Collections.singletonList(hit));
        when(hit.getResource()).thenReturn(resource);
        when(resource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);

        try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
            mockedHelper.when(() -> ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource))
                .thenReturn("en");
            mockedHelper.when(() -> ModelHelper.getLocalizedElementValue(any(ContentFragment.class), anyString(), eq("title"), anyString()))
                .thenReturn("Event Title");
            mockedHelper.when(() -> ModelHelper.getLocalizedElementValue(any(ContentFragment.class), anyString(), eq("description"), anyString()))
                .thenReturn("Event Description");

            events = eventListingModel.getEvents();
        }
        assertNotNull(events);
        assertFalse(events.isEmpty());
    }
}