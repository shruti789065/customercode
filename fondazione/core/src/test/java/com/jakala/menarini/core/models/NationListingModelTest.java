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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NationListingModelTest {

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

    @InjectMocks
    private NationListingModel nationListingModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInit() {
        when(resourceResolver.getResource("/content/dam/fondazione/nations/")).thenReturn(parentResource);
        when(parentResource.listChildren()).thenReturn(Collections.singletonList(childResource).iterator());
        when(childResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        ContentElement contentElementId = mock(ContentElement.class);
        ContentElement contentElementName = mock(ContentElement.class);
        when(contentFragment.getElement("id")).thenReturn(contentElementId);
        when(contentFragment.getElement("name")).thenReturn(contentElementName);
        when(contentElementId.getContent()).thenReturn("1");
        when(contentElementName.getContent()).thenReturn("NationName");
        when(contentFragment.getName()).thenReturn("nation-path");

        try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
            mockedHelper.when(() -> ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource)).thenReturn("en");
            mockedHelper.when(() -> ModelHelper.getLocalizedElementValue(any(ContentFragment.class), anyString(), eq("name"), anyString()))
                .thenReturn("NationName");

            nationListingModel.init();

            List<NationListingModel.Nation> nations = nationListingModel.getNations();
            assertEquals(1, nations.size());
            assertEquals("1", nations.get(0).getId());
            assertEquals("NationName", nations.get(0).getName());
            assertEquals("/content/dam/fondazione/nations/nation-path", nations.get(0).getPath());
        }
    }

}