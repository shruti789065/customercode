package com.jakala.menarini.core.models;

import com.adobe.granite.ui.components.formbuilder.FormResourceManager;
import com.day.cq.i18n.I18n;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.PostConstruct;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DisabledCheckboxModelTest {

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private FormResourceManager formResourceManager;

    @Mock
    private Resource resource;

    @Mock
    private ValueMap valueMap;

    @Mock
    private Resource wrappedResource;

    @InjectMocks
    private DisabledCheckboxModel disabledCheckboxModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getResource()).thenReturn(resource);
        when(resource.getValueMap()).thenReturn(valueMap);
        when(formResourceManager.getDefaultPropertyFieldResource(any(Resource.class), any(HashMap.class))).thenReturn(wrappedResource);
    }

    @Test
    void testInit() {
        when(valueMap.get("disabled", false)).thenReturn(true);
        when(resource.getName()).thenReturn("testResource");

        disabledCheckboxModel.init();

        verify(formResourceManager).getDefaultPropertyFieldResource(eq(resource), any(HashMap.class));
    }

    @Test
    void testGetResource() {
        when(valueMap.get("disabled", false)).thenReturn(true);
        when(resource.getName()).thenReturn("testResource");

        disabledCheckboxModel.init();

        Resource result = disabledCheckboxModel.getResource();
        assertEquals(wrappedResource, result);
    }
}