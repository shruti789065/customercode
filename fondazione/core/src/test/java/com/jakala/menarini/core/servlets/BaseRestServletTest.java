package com.jakala.menarini.core.servlets;

import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.security.Acl;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseRestServletTest {

    @InjectMocks
    private BaseRestServlet baseRestServlet = new BaseRestServlet() {};

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private PrintWriter printWriter;

    void beforeSend() throws IOException {
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testGetAclsWithRoles() {
        RoleDto role1 = new RoleDto();
        RoleDto role2 = new RoleDto();
        RoleDto[] roles = {role1, role2};

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("roles")).thenReturn(roles);

        Set<Acl> acls = baseRestServlet.getAcls(request);

        assertNotNull(acls);
        verify(session).getAttribute("roles");
    }

    @Test
    void testGetAclsWithoutRoles() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("roles")).thenReturn(null);

        Set<Acl> acls = baseRestServlet.getAcls(request);

        assertNull(acls);
        verify(session).getAttribute("roles");
    }

    @Test
    void testSendJsonResponseWithData() throws IOException {
        beforeSend();

        String status = "success";
        int statusCode = 200;
        Object data = new Object();

        baseRestServlet.sendJsonResponse(response, status, statusCode, data);

        verify(response).setStatus(statusCode);
        verify(response).setContentType("application/json");
        verify(printWriter).write(contains("\"status\":\"success\""));
        verify(printWriter).write(contains("\"data\":"));
    }

    @Test
    void testSendJsonResponseWithoutData() throws IOException {
        beforeSend();

        String status = "success";
        int statusCode = 200;
        Object data = null;

        baseRestServlet.sendJsonResponse(response, status, statusCode, data);

        verify(response).setStatus(statusCode);
        verify(response).setContentType("application/json");
        verify(printWriter).write("{\"status\":\"success\"}");
    }
}