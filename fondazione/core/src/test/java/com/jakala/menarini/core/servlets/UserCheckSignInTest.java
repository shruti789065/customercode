package com.jakala.menarini.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCheckSignInTest {

    @InjectMocks
    UserCheckSignIn userCheckSignIn;

    @Mock
    SlingHttpServletRequest request;

    @Mock
    SlingHttpServletResponse response;

    @Mock
    PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testDoGetWithAllCookies() throws IOException {
        Cookie[] cookies = {
            new Cookie("p-idToken", "value1"),
            new Cookie("p-aToken", "value2"),
            new Cookie("p-rToken", "value3")
        };
        when(request.getCookies()).thenReturn(cookies);

        userCheckSignIn.doGet(request, response);

        verify(response).setStatus(200);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(printWriter).write("{\"isAuth\":\"true\"}");
    }

    @Test
    void testDoGetWithMissingCookies() throws IOException {
        Cookie[] cookies = {
            new Cookie("p-idToken", "value1"),
            new Cookie("p-aToken", "value2")
        };
        when(request.getCookies()).thenReturn(cookies);

        userCheckSignIn.doGet(request, response);

        verify(response, never()).setStatus(200);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(printWriter).write("{\"isAuth\":\"true\"}");
    }

    @Test
    void testDoGetWithoutCookies() throws IOException {
        when(request.getCookies()).thenReturn(null);

        userCheckSignIn.doGet(request, response);

        verify(response).setStatus(401);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(printWriter).write("{\"isAuth\":\"true\"}");
    }
}