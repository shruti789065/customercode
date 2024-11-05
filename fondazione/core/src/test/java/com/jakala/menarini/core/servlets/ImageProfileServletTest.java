package com.jakala.menarini.core.servlets;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.awslambda.ImageProfileServiceResponseDto;
import com.jakala.menarini.core.dto.awslambda.LambdaGetFileDto;
import com.jakala.menarini.core.dto.awslambda.LambdaPutFileDto;
import com.jakala.menarini.core.dto.awslambda.PutImageDto;
import com.jakala.menarini.core.service.interfaces.EncryptDataServiceInterface;
import com.jakala.menarini.core.service.interfaces.ImageProfileServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageProfileServletTest {

    @InjectMocks
    private ImageProfileServlet imageProfileServlet;

    @Mock
    private ImageProfileServiceInterface imageProfileService;

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

    void beforeData() throws IOException {
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testDoGetSuccess() throws IOException {
        beforeData();

        String token = "testToken";
        String userEmail = "user@example.com";
        String username = "user_example_com";
        ImageProfileServiceResponseDto responseDto = new ImageProfileServiceResponseDto();
        responseDto.setSuccess(true);
        responseDto.setImageData("imageData");

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("p-idToken", "encryptedToken")});
        when(encryptDataService.decrypt("encryptedToken")).thenReturn(token);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userEmail")).thenReturn(userEmail);
        when(imageProfileService.getImageProfile(any(LambdaGetFileDto.class), eq(token))).thenReturn(responseDto);

        imageProfileServlet.doGet(request, response);

        //verify(response).setStatus(200);
        verify(response).setContentType("application/json");
        verify(printWriter).write(gson.toJson(responseDto));
    }

    @Test
    void testDoGetUnauthorized() throws IOException {
        when(request.getCookies()).thenReturn(null);

        imageProfileServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(401);
    }

    @Test
    void testDoPostSuccess() throws IOException {
        beforeData();

        String token = "testToken";
        String userEmail = "user@example.com";
        String username = "user_example_com";
        PutImageDto putImageDto = new PutImageDto();
        putImageDto.setImageData("imageData");
        ImageProfileServiceResponseDto responseDto = new ImageProfileServiceResponseDto();
        responseDto.setSuccess(true);

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("p-idToken", "encryptedToken")});
        when(encryptDataService.decrypt("encryptedToken")).thenReturn(token);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userEmail")).thenReturn(userEmail);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(putImageDto))));
        when(imageProfileService.saveImageProfile(any(LambdaPutFileDto.class), eq(token))).thenReturn(responseDto);

        imageProfileServlet.doPost(request, response);

        //verify(response).setStatus(200);
        verify(response).setContentType("application/json");
        verify(printWriter).write(gson.toJson(responseDto));
    }

    @Test
    void testDoPostUnauthorized() throws IOException {
        when(request.getCookies()).thenReturn(null);

        imageProfileServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(401);
    }
}