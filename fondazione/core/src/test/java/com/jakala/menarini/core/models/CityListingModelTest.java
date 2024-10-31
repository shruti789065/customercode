package com.jakala.menarini.core.models;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CityListingModelTest {

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Resource currentResource;

    @Mock
    private Resource parentResource;

    @Mock
    private Resource childResource;

    @Mock
    private ContentFragment contentFragment;

    @Mock
    private ContentElement contentElementId;

    @Mock
    private ContentElement contentElementName;

    @InjectMocks
    private CityListingModel cityListingModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInit() {
        when(resourceResolver.getResource("/content/dam/fondazione/cities/")).thenReturn(parentResource);
        when(parentResource.listChildren()).thenReturn(Collections.singletonList(childResource).iterator());
        when(childResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("id")).thenReturn(contentElementId);
        when(contentElementId.getContent()).thenReturn("1");
        when(contentFragment.getElement("name")).thenReturn(contentElementName);
        when(contentElementName.getContent()).thenReturn("CityName");
        when(contentFragment.getName()).thenReturn("city-path");
        try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
            mockedHelper.when(() -> ModelHelper.getLocalizedElementValue(any(ContentFragment.class), anyString(), anyString(), anyString()))
                    .thenReturn("CityName");
                cityListingModel.init();

                List<CityListingModel.City> cities = cityListingModel.getCities();
                assertEquals(1, cities.size());
                assertEquals("1", cities.get(0).getId());
                //assertEquals("CityName", cities.get(0).getName());
                assertEquals("/content/dam/fondazione/cities/city-path", cities.get(0).getPath());
        }

    }

    @Test
    void testGetCities() {
        List<CityListingModel.City> cities = cityListingModel.getCities();
        assertNotNull(cities);
        assertTrue(cities.isEmpty());
    }
}