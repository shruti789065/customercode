package com.jakala.menarini.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.jakala.menarini.core.dto.ExternalSocialLinkResponseDto;
import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RegisteredUserPermissionDto;
import com.jakala.menarini.core.dto.RegisteredUserTopicDto;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.service.interfaces.AuthServiceForModelsInterface;
import com.jakala.menarini.core.service.interfaces.ExternalizeUrlServiceInterface;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class HomePageBaseModelTest {

    @Mock
    private ExternalizeUrlServiceInterface externalizeService;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private PageManager pageManager;

    @Mock
    private Page currentPage;

    @Mock
    private Resource resource;

    @Mock
    private RegisteredUserDto registeredUserDto;

    @Mock
    private AuthBaseModel authBaseModel;

    @Mock
    private AuthServiceForModelsInterface jwt;

    @Mock
    private UserRegisteredServiceInterface userService;

    @InjectMocks
    private HomePageBaseModel homePageBaseModel;

    @BeforeEach
    void setUp() throws AccessDeniedException, SQLException {
        MockitoAnnotations.openMocks(this);
        when(request.getResource()).thenReturn(resource);
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getContainingPage(resource)).thenReturn(currentPage);
        when(currentPage.getPath()).thenReturn("/content/path");

        HashMap<String, RoleDto[]> authData = new HashMap<>();
        RoleDto roleDto = new RoleDto();
        RoleDto[] roles = {roleDto};
        authData.put("test@example.com", roles);

        when(jwt.extractCredentialsForComponent(request)).thenReturn(authData);
        RegisteredUserDto mockUser = new RegisteredUserDto();
        mockUser.setCountry("mockCountry");
        mockUser.setFirstname("John");
        mockUser.setLastname("Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setRegisteredUserTopics(Collections.singletonList(new RegisteredUserTopicDto()));
        authBaseModel.setUser(mockUser);
        when(userService.getUserByEmail(anyString(), anySet(), anyList())).thenReturn(mockUser);
        when(userService.generateUserPermission(any(RoleDto.class), anyString())).thenReturn(new RegisteredUserPermissionDto());
        when(request.getResourceResolver()).thenReturn(resourceResolver);
    }

    @Test
    void testInit() {

        List<ExternalSocialLinkResponseDto> links = new ArrayList<>();
        ExternalSocialLinkResponseDto youtubeLink = new ExternalSocialLinkResponseDto();
        youtubeLink.setType("youtube");
        youtubeLink.setRedirect("https://youtube.com/share");
        links.add(youtubeLink);

        ExternalSocialLinkResponseDto linkedinLink = new ExternalSocialLinkResponseDto();
        linkedinLink.setType("linkedin");
        linkedinLink.setRedirect("https://linkedin.com/share");
        links.add(linkedinLink);

        when(externalizeService.generateSocialLink(resourceResolver, "/content/path")).thenReturn(links);

        homePageBaseModel.init();

        assertEquals("https://youtube.com/share", homePageBaseModel.getYoutubeShareLink());
        assertEquals("https://linkedin.com/share", homePageBaseModel.getLinkedinShareLink());
    }

    @Test
    void testInitWithAuth() {
        
        when(authBaseModel.isAuth()).thenReturn(true);
        when(authBaseModel.getUser()).thenReturn(registeredUserDto);

        homePageBaseModel.init();

        assertEquals("John", homePageBaseModel.getFirstName());
        assertEquals("Doe", homePageBaseModel.getLastName());
        assertEquals("john.doe@example.com", homePageBaseModel.getEmail());
        assertEquals(1, homePageBaseModel.getTopics().size());
    }

    @Test
    void testGetAndSetFirstName() {
        homePageBaseModel.setFirstName("John");
        assertEquals("John", homePageBaseModel.getFirstName());
    }

    @Test
    void testGetAndSetLastName() {
        homePageBaseModel.setLastName("Doe");
        assertEquals("Doe", homePageBaseModel.getLastName());
    }

    @Test
    void testGetAndSetEmail() {
        homePageBaseModel.setEmail("john.doe@example.com");
        assertEquals("john.doe@example.com", homePageBaseModel.getEmail());
    }

    @Test
    void testGetAndSetTopics() {
        List<RegisteredUserTopicDto> topics = Collections.singletonList(new RegisteredUserTopicDto());
        homePageBaseModel.setTopics(topics);
        assertEquals(topics, homePageBaseModel.getTopics());
    }

    @Test
    void testGetAndSetYoutubeShareLink() {
        homePageBaseModel.setYoutubeShareLink("https://youtube.com/share");
        assertEquals("https://youtube.com/share", homePageBaseModel.getYoutubeShareLink());
    }

    @Test
    void testGetAndSetLinkedinShareLink() {
        homePageBaseModel.setLinkedinShareLink("https://linkedin.com/share");
        assertEquals("https://linkedin.com/share", homePageBaseModel.getLinkedinShareLink());
    }
}