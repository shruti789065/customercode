package com.jakala.menarini.core.models;

import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RegisteredUserPermissionDto;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.models.utils.RequestDataConstants;
import com.jakala.menarini.core.service.interfaces.AuthServiceForModelsInterface;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthBaseModelTest {

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
    void testInit() throws AccessDeniedException, SQLException {
        HashMap<String, RoleDto[]> authData = new HashMap<>();
        RoleDto roleDto = new RoleDto();
        RoleDto[] roles = {roleDto};
        authData.put("test@example.com", roles);

        when(jwt.extractCredentialsForComponent(request)).thenReturn(authData);
        RegisteredUserDto mockUser = new RegisteredUserDto();
        mockUser.setCountry("mockCountry");
        authBaseModel.setUser(mockUser);
        when(userService.getUserByEmail(anyString(), anySet(), anyList())).thenReturn(mockUser);
        when(userService.generateUserPermission(any(RoleDto.class), anyString())).thenReturn(new RegisteredUserPermissionDto());

        authBaseModel.init();

        assertTrue(authBaseModel.isAuth());
        assertNotNull(authBaseModel.getUser());
        verify(request).setAttribute(RequestDataConstants.KEY_USER, authBaseModel.getUser());
    }

    @Test
    void testInitWithException() throws AccessDeniedException, SQLException {
        HashMap<String, RoleDto[]> authData = new HashMap<>();
        RoleDto roleDto = new RoleDto();
        RoleDto[] roles = {roleDto};
        authData.put("test@example.com", roles);

        when(jwt.extractCredentialsForComponent(request)).thenReturn(authData);
        when(userService.getUserByEmail(anyString(), anySet(), anyList())).thenThrow(new SQLException());

        authBaseModel.init();

        assertFalse(authBaseModel.isAuth());
        //verify(request).setAttribute(RequestDataConstants.KEY_IS_AUTH, false);
    }

    @Test
    void testIsAuth() {
        authBaseModel.setAuth(true);
        assertTrue(authBaseModel.isAuth());
    }

    @Test
    void testSetAuth() {
        authBaseModel.setAuth(true);
        assertTrue(authBaseModel.isAuth());
    }

    @Test
    void testGetUser() {
        RegisteredUserDto user = new RegisteredUserDto();
        authBaseModel.setUser(user);
        assertEquals(user, authBaseModel.getUser());
    }

    @Test
    void testSetUser() {
        RegisteredUserDto user = new RegisteredUserDto();
        authBaseModel.setUser(user);
        assertEquals(user, authBaseModel.getUser());
    }

    @Test
    void testIsMagazineSubscription() {
        authBaseModel.setMagazineSubscription(true);
        assertTrue(authBaseModel.isMagazineSubscription());
    }

    @Test
    void testSetMagazineSubscription() {
        authBaseModel.setMagazineSubscription(true);
        assertTrue(authBaseModel.isMagazineSubscription());
    }

    @Test
    void testIsMaterialAccess() {
        authBaseModel.setMaterialAccess(true);
        assertTrue(authBaseModel.isMaterialAccess());
    }

    @Test
    void testSetMaterialAccess() {
        authBaseModel.setMaterialAccess(true);
        assertTrue(authBaseModel.isMaterialAccess());
    }

    @Test
    void testIsEventSubscription() {
        authBaseModel.setEventSubscription(true);
        assertTrue(authBaseModel.isEventSubscription());
    }

    @Test
    void testSetEventSubscription() {
        authBaseModel.setEventSubscription(true);
        assertTrue(authBaseModel.isEventSubscription());
    }
}