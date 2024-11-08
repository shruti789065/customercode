package com.jakala.menarini.core.servlets;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.cognito.ForgetPasswordDto;
import com.jakala.menarini.core.dto.cognito.ForgetPasswordResponseDto;
import com.jakala.menarini.core.service.interfaces.AwsCognitoServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForgetPasswordServletTest {

    @InjectMocks
    private ForgetPasswordServlet forgetPasswordServlet;

    @Mock
    private AwsCognitoServiceInterface awsCognitoService;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private PrintWriter printWriter;

    private Gson gson = new Gson();

    @BeforeEach
    void setUp() throws IOException {
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testDoPostSuccess() throws IOException {
        ForgetPasswordDto forgetPasswordDto = new ForgetPasswordDto();
        ForgetPasswordResponseDto responseData = new ForgetPasswordResponseDto();
        responseData.setSuccess(true);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(forgetPasswordDto))));
        when(awsCognitoService.forgetPassword(any(ForgetPasswordDto.class))).thenReturn(responseData);

        forgetPasswordServlet.doPost(request, response);

            
        //verify(response).setStatus(200); ADD set status 200 to doPost method
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(printWriter).write(gson.toJson(responseData));
    }

    @Test
    void testDoPostFailure() throws IOException {
        ForgetPasswordDto forgetPasswordDto = new ForgetPasswordDto();
        ForgetPasswordResponseDto responseData = new ForgetPasswordResponseDto();
        responseData.setSuccess(false);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(forgetPasswordDto))));
        when(awsCognitoService.forgetPassword(any(ForgetPasswordDto.class))).thenReturn(responseData);

        forgetPasswordServlet.doPost(request, response);

        verify(response).setStatus(400);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(printWriter).write(gson.toJson(responseData));
    }
}