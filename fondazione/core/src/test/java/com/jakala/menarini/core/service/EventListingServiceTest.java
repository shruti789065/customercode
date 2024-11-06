package com.jakala.menarini.core.service;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.jakala.menarini.core.dto.EventModelReturnDto;
import com.jakala.menarini.core.models.ModelHelper;
import com.jakala.menarini.core.service.utils.EventListingServiceConfiguration;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import java.util.*;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EventListingServiceTest {

    @Mock
    private ResourceResolver resourceResolver;

    @InjectMocks
    private EventListingService eventListingService;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        EventListingServiceConfiguration config = mock(EventListingServiceConfiguration.class);
        when(config.eventPath()).thenReturn("/content/events");
        when(config.topicPath()).thenReturn("/content/topics");
        when(config.cityPath()).thenReturn("/content/cities");
        when(config.startDateProperty()).thenReturn("startDate");
        when(config.endDateProperty()).thenReturn("endDate");

        // Use reflection to call the private activate method
        try {
            java.lang.reflect.Method method = EventListingService.class.getDeclaredMethod("activate", EventListingServiceConfiguration.class);
            method.setAccessible(true);
            method.invoke(eventListingService, config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetEvents() throws RepositoryException {
        List<String> topicsFilterIds = Arrays.asList("topic1", "topic2");
        List<String> eventTypesFilterIds = Arrays.asList("eventType1", "eventType2");
        List<String> citiesFilterIds = Arrays.asList("city1", "city2");
        String statusFilter = "scheduled";
        String startDate = "2023-01-01";
        String endDate = "2023-12-31";
        int page = 1;
        int pageSlot = 0;

        when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(queryBuilder);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(queryBuilder.createQuery(any(PredicateGroup.class), eq(session))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        when(searchResult.getHits()).thenReturn(Collections.singletonList(hit));
        when(searchResult.getTotalMatches()).thenReturn(1L);
        when(hit.getResource()).thenReturn(resource);
        when(resource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);

        ContentElement contentElementId = mock(ContentElement.class);
        ContentElement contentElementStartDate = mock(ContentElement.class);
        ContentElement contentElementEndDate = mock(ContentElement.class);
        ContentElement contentElementPresentationImage = mock(ContentElement.class);
        ContentElement contentElementTopics = mock(ContentElement.class);
        FragmentData   fragmentDataTopics = mock(FragmentData.class);
        ContentElement contentElementEventType = mock(ContentElement.class);
        ContentElement contentElementCity = mock(ContentElement.class);
        ContentElement contentElementSubscription = mock(ContentElement.class);
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
        when(contentFragment.getElement("subscription")).thenReturn(contentElementSubscription);
        when(contentElementSubscription.getContent()).thenReturn("subscription");
        when(contentFragment.getTitle()).thenReturn("Event Title");
        when(contentFragment.getDescription()).thenReturn("Event Description");

        try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
            mockedHelper.when(() -> ModelHelper.getCurrentPageLanguage(resourceResolver, null)).thenReturn("en");
            mockedHelper.when(() -> ModelHelper.getLocalizedElementValue(any(ContentFragment.class), anyString(), eq("title"), anyString()))
                .thenReturn("Event Title");
            mockedHelper.when(() -> ModelHelper.getLocalizedElementValue(any(ContentFragment.class), anyString(), eq("description"), anyString()))
                .thenReturn("Event Description");
            EventModelReturnDto result = eventListingService.getEvents(resourceResolver, "en", topicsFilterIds, eventTypesFilterIds, citiesFilterIds, statusFilter, startDate, endDate, page, pageSlot);

            assertNotNull(result);
            assertEquals(1, result.getTotalMatches());
            assertEquals(1, result.getEvents().size());
            assertEquals("Event Title", result.getEvents().get(0).getTitle());
        }
    }
}