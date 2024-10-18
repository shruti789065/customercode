package com.jakala.menarini.core.servlets;

import com.jakala.menarini.core.service.interfaces.CookieServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignOutServletTest {

    @InjectMocks
    private SignOutServlet signOutServlet;

    @Mock
    private CookieServiceInterface cookieService;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDoPostWithCookies() throws IOException {
        Cookie[] cookies = { new Cookie("cookie1", "value1"), new Cookie("cookie2", "value2") };
        when(request.getCookies()).thenReturn(cookies);

        signOutServlet.doPost(request, response);

        ArrayList<String> expectedCookies = new ArrayList<>();
        expectedCookies.add("cookie1");
        expectedCookies.add("cookie2");

        verify(cookieService).removeCookie(response, expectedCookies);
    }

    @Test
    void testDoPostWithoutCookies() throws IOException {
        when(request.getCookies()).thenReturn(null);

        signOutServlet.doPost(request, response);

        verify(cookieService, never()).removeCookie(any(SlingHttpServletResponse.class), anyList());
    }
}