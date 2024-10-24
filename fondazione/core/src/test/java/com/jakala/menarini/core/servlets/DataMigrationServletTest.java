package com.jakala.menarini.core.servlets;

import com.jakala.menarini.core.service.DataMigrationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataMigrationServletTest {

    @InjectMocks
    private DataMigrationServlet dataMigrationServlet;

    @Mock
    private DataMigrationService migrationService;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testDoGetSuccess() throws Exception {
        when(request.getParameter("object")).thenReturn("testObject");
        when(request.getParameter("exclusions")).thenReturn("testExclusions");
        when(request.getParameter("delete")).thenReturn("testDelete");

        dataMigrationServlet.doGet(request, response);

        verify(migrationService).migrateData("testObject", "testExclusions", "testDelete");
        verify(printWriter).write("{\"status\":\"success\"}");
    }

    @Test
    void testDoGetInterruptedException() throws Exception {
        when(request.getParameter("object")).thenReturn("testObject");
        when(request.getParameter("exclusions")).thenReturn("testExclusions");
        when(request.getParameter("delete")).thenReturn("testDelete");

        doThrow(new InterruptedException("Test InterruptedException")).when(migrationService).migrateData(anyString(), anyString(), anyString());

        dataMigrationServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(printWriter).write("{\"status\":\"error\", \"message\": Test InterruptedException}");
    }

    @Test
    void testDoGetOtherExceptions() throws Exception {
        when(request.getParameter("object")).thenReturn("testObject");
        when(request.getParameter("exclusions")).thenReturn("testExclusions");
        when(request.getParameter("delete")).thenReturn("testDelete");

        doThrow(new LoginException("Test LoginException")).when(migrationService).migrateData(anyString(), anyString(), anyString());

        dataMigrationServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(printWriter).write("{\"status\":\"error\", \"message\": Test LoginException}");
    }
}