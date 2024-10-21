package com.jakala.menarini.core.servlets;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RegisteredUserServletDto;
import com.jakala.menarini.core.dto.RegisteredUseServletResponseDto;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.security.Acl;
import com.jakala.menarini.core.service.interfaces.AwsCognitoServiceInterface;
import com.jakala.menarini.core.service.interfaces.CookieServiceInterface;
import com.jakala.menarini.core.service.interfaces.EncryptDataServiceInterface;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegisteredServletTest {

    @InjectMocks
    private UserRegisteredServlet userRegisteredServlet;

    @Mock
    private UserRegisteredServiceInterface userService;

    @Mock
    private CookieServiceInterface cookieService;

    @Mock
    private AwsCognitoServiceInterface awsCognitoService;

    @Mock
    private EncryptDataServiceInterface encryptDataService;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private PrintWriter printWriter;

    private Gson gson = new Gson();

    @BeforeEach
    void setUp() throws IOException {
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testDoGetSuccess() throws IOException, SQLException, ServletException {
        String userMail = "user@example.com";
        RoleDto[] roles = new RoleDto[]{new RoleDto()};
        Set<Acl> acls = new HashSet<>();
        RegisteredUserDto user = new RegisteredUserDto();
        RegisteredUseServletResponseDto responseDto = new RegisteredUseServletResponseDto();
        responseDto.setSuccess(true);
        responseDto.setUpdatedUser(user);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userEmail")).thenReturn(userMail);
        when(session.getAttribute("roles")).thenReturn(roles);
        when(userService.getUserByEmail(userMail, acls, Arrays.asList(roles))).thenReturn(user);

        userRegisteredServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        //verify(response).setStatus(200);
        verify(printWriter).write(gson.toJson(responseDto));
    }

    @Test
    void testDoGetUserNotFound() throws IOException, SQLException, ServletException {
        String userMail = "user@example.com";
        RoleDto[] roles = new RoleDto[]{new RoleDto()};
        Set<Acl> acls = new HashSet<>();
        RegisteredUseServletResponseDto responseDto = new RegisteredUseServletResponseDto();
        responseDto.setSuccess(false);
        responseDto.setErrorMessage("Email not found");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userEmail")).thenReturn(userMail);
        when(session.getAttribute("roles")).thenReturn(roles);
        when(userService.getUserByEmail(userMail, acls, Arrays.asList(roles))).thenReturn(null);

        userRegisteredServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(404);
        verify(printWriter).write(gson.toJson(responseDto));
    }

    @Test
    void testDoGetAccessDenied() throws IOException, SQLException, ServletException {
        String userMail = "user@example.com";
        RoleDto[] roles = new RoleDto[]{new RoleDto()};
        Set<Acl> acls = new HashSet<>();

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userEmail")).thenReturn(userMail);
        when(session.getAttribute("roles")).thenReturn(roles);
        when(userService.getUserByEmail(userMail, acls, Arrays.asList(roles))).thenThrow(new AccessDeniedException("Access denied"));

        userRegisteredServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(403);
        verify(printWriter).println("");
    }

    @Test
    void testDoGetSQLException() throws IOException, SQLException, ServletException {
        String userMail = "user@example.com";
        RoleDto[] roles = new RoleDto[]{new RoleDto()};
        Set<Acl> acls = new HashSet<>();
        RegisteredUseServletResponseDto responseDto = new RegisteredUseServletResponseDto();
        responseDto.setSuccess(false);
        responseDto.setErrorMessage("SQL error");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userEmail")).thenReturn(userMail);
        when(session.getAttribute("roles")).thenReturn(roles);
        when(userService.getUserByEmail(userMail, acls, Arrays.asList(roles))).thenThrow(new SQLException("SQL error"));

        userRegisteredServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(500);
        verify(printWriter).write(gson.toJson(responseDto));
    }

    @Test
    void testDoPostSuccess() throws IOException, SQLException, ServletException {
        String userMail = "user@example.com";
        RoleDto[] roles = new RoleDto[]{new RoleDto()};
        Set<Acl> acls = new HashSet<>();
        RegisteredUserServletDto updateData = new RegisteredUserServletDto();
        RegisteredUseServletResponseDto result = new RegisteredUseServletResponseDto();
        result.setSuccess(true);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userEmail")).thenReturn(userMail);
        when(session.getAttribute("roles")).thenReturn(roles);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(updateData))));
        when(userService.updateUserData(anyString(), any(RegisteredUserDto.class), anyList(), anySet(), anyList())).thenReturn(result);

        userRegisteredServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(200);
        verify(printWriter).write(gson.toJson(result));
    }

    @Test
    void testDoPostFailure() throws IOException, SQLException, ServletException {
        String userMail = "user@example.com";
        RoleDto[] roles = new RoleDto[]{new RoleDto()};
        Set<Acl> acls = new HashSet<>();
        RegisteredUserServletDto updateData = new RegisteredUserServletDto();
        RegisteredUseServletResponseDto result = new RegisteredUseServletResponseDto();
        result.setSuccess(false);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userEmail")).thenReturn(userMail);
        when(session.getAttribute("roles")).thenReturn(roles);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(updateData))));
        when(userService.updateUserData(anyString(), any(RegisteredUserDto.class), anyList(), anySet(), anyList())).thenReturn(result);

        userRegisteredServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(500);
        verify(printWriter).write(gson.toJson(result));
    }

    @Test
    void testDoPostAccessDenied() throws IOException, SQLException, ServletException {
        String userMail = "user@example.com";
        RoleDto[] roles = new RoleDto[]{new RoleDto()};
        
        RegisteredUserServletDto updateData = new RegisteredUserServletDto();

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userEmail")).thenReturn(userMail);
        when(session.getAttribute("roles")).thenReturn(roles);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(updateData))));
        when(userService.updateUserData(anyString(), any(RegisteredUserDto.class), anyList(), anySet(), anyList())).thenThrow(new AccessDeniedException("Access denied"));

        userRegisteredServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(403);
        verify(printWriter).println("");
    }

    @Test
    void testDoPostSQLException() throws IOException, SQLException, ServletException {
        String userMail = "user@example.com";
        RoleDto[] roles = new RoleDto[]{new RoleDto()};
        Set<Acl> acls = new HashSet<>();
        RegisteredUserServletDto updateData = new RegisteredUserServletDto();
        RegisteredUseServletResponseDto responseDto = new RegisteredUseServletResponseDto();
        responseDto.setSuccess(false);
        responseDto.setErrorMessage("SQL error");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userEmail")).thenReturn(userMail);
        when(session.getAttribute("roles")).thenReturn(roles);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(updateData))));
        when(userService.updateUserData(anyString(), any(RegisteredUserDto.class), anyList(), anySet(), anyList())).thenThrow(new SQLException("SQL error"));

        userRegisteredServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(500);
        verify(printWriter).write(gson.toJson(responseDto));
    }
}