package com.jakala.menarini.core.models;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RegisteredUserPermissionDto;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.service.interfaces.AuthServiceForModelsInterface;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

class TopicListingModelTest {

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
    private TopicListingModel topicListingModel;


    @Mock
    private AuthServiceForModelsInterface jwt;

    @Mock
    private UserRegisteredServiceInterface userService;

    @Mock
    private SlingHttpServletRequest request;

    @InjectMocks
    private AuthBaseModel authBaseModel;

    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInit() {
        ContentElement contentElementId = mock(ContentElement.class);
        ContentElement contentElementName = mock(ContentElement.class);
        when(resourceResolver.getResource("/content/dam/fondazione/topics/")).thenReturn(parentResource);
        when(resourceResolver.adaptTo(Resource.class)).thenReturn(currentResource);
        when(parentResource.listChildren()).thenReturn(Collections.singletonList(childResource).iterator());
        when(childResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("id")).thenReturn(contentElementId);
        when(contentFragment.getElement("name")).thenReturn(contentElementName);
        when(contentElementId.getContent()).thenReturn("1");
        when(contentElementName.getContent()).thenReturn("TopicName");
        when(contentFragment.getName()).thenReturn("topic-path");

        HashMap<String, RoleDto[]> authData = new HashMap<>();
        RoleDto roleDto = new RoleDto();
        RoleDto[] roles = {roleDto};
        authData.put("test@example.com", roles);
        when(jwt.extractCredentialsForComponent(request)).thenReturn(authData);
        try {
            when(userService.getUserByEmail(anyString(), anySet(), anyList())).thenReturn(new RegisteredUserDto());
        } catch (AccessDeniedException e) {
            fail("AccessDeniedException should not be thrown");
        } catch (SQLException e) {
            fail("SQLException should not be thrown");
        }
        when(userService.generateUserPermission(any(RoleDto.class), isNull())).thenReturn(new RegisteredUserPermissionDto());

        try (MockedStatic<ModelHelper> mockedHelper = mockStatic(ModelHelper.class)) {
            mockedHelper.when(() -> ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource))
                .thenReturn("en");
            mockedHelper.when(() -> ModelHelper.getLocalizedElementValue(any(ContentFragment.class), anyString(), eq("name"), anyString()))
                .thenReturn("TopicName");
             
            topicListingModel.init();
        }
        List<TopicListingModel.Topic> topics = topicListingModel.getTopics();
        assertEquals(1, topics.size());
        assertEquals("1", topics.get(0).getId());
        assertEquals("TopicName", topics.get(0).getName());
        assertEquals("/content/dam/fondazione/topics/topic-path", topics.get(0).getPath());
    }

    @Test
    void testGetTopics() {
        List<TopicListingModel.Topic> topics = topicListingModel.getTopics();
        assertNotNull(topics);
        assertTrue(topics.isEmpty());
    }
}