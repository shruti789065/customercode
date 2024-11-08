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
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    private ExternalizeUrlService externalizeUrlService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        EventListingServiceConfiguration config = mock(EventListingServiceConfiguration.class);
        when(config.eventPath()).thenReturn("/content/events");
        when(config.topicPath()).thenReturn("/content/topics");
        when(config.cityPath()).thenReturn("/content/cities");
        when(config.startDateProperty()).thenReturn("startDate");
        when(config.endDateProperty()).thenReturn("endDate");
        when(config.baseUrl()).thenReturn("/content/fondazione/LANG/courses-and-events/");

        when(externalizeUrlService.getExternalizeUrl(any(ResourceResolver.class), anyString())).thenAnswer(invocation -> invocation.getArgument(1));

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

        ContentElement idElement = mock(ContentElement.class);
        ContentElement startDateElement = mock(ContentElement.class);
        ContentElement endDateElement = mock(ContentElement.class);
        ContentElement presentationImageElement = mock(ContentElement.class);
        ContentElement topicsElement = mock(ContentElement.class);
        FragmentData topicsFragmentData = mock(FragmentData.class);
        ContentElement eventTypeElement = mock(ContentElement.class);
        ContentElement cityElement = mock(ContentElement.class);
        ContentElement subscriptionElement = mock(ContentElement.class);
        ContentElement slugElement = mock(ContentElement.class);
        when(contentFragment.getElement("id")).thenReturn(idElement);
        when(idElement.getContent()).thenReturn("1");
        when(contentFragment.getElement("startDate")).thenReturn(startDateElement);
        when(startDateElement.getContent()).thenReturn("2023-01-01");
        when(contentFragment.getElement("endDate")).thenReturn(endDateElement);
        when(endDateElement.getContent()).thenReturn("2023-12-31");
        when(contentFragment.getElement("presentationImage")).thenReturn(presentationImageElement);
        when(presentationImageElement.getContent()).thenReturn("image.jpg");
        when(contentFragment.getElement("topics")).thenReturn(topicsElement);
        when(topicsElement.getValue()).thenReturn(topicsFragmentData);
        when(topicsFragmentData.getValue()).thenReturn(new String[]{"topic1", "topic2"});
        when(contentFragment.getElement("eventType")).thenReturn(eventTypeElement);
        when(eventTypeElement.getContent()).thenReturn("eventType");
        when(contentFragment.getElement("city")).thenReturn(cityElement);
        when(cityElement.getContent()).thenReturn("city");
        when(contentFragment.getElement("subscription")).thenReturn(subscriptionElement);
        when(subscriptionElement.getContent()).thenReturn("subscription");
        when(contentFragment.getTitle()).thenReturn("Event Title");
        when(contentFragment.getDescription()).thenReturn("Event Description");
        when(contentFragment.getElement("slug")).thenReturn(slugElement);
        when(slugElement.getContent()).thenReturn("event-slug");

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