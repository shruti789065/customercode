package com.jakala.menarini.core.servlets;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.cognito.ConfirmForgetPasswordDto;
import com.jakala.menarini.core.dto.cognito.ConfirmForgetPasswordResponseDto;
import com.jakala.menarini.core.service.interfaces.AwsCognitoServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmForgetPasswordTest {

    @InjectMocks
    private ConfirmForgetPassword confirmForgetPassword;

    @Mock
    private AwsCognitoServiceInterface awsCognitoService;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private PrintWriter printWriter;

    private Gson gson;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        gson = new Gson();
    }

    @Test
    void testDoPostSuccess() throws IOException {
        ConfirmForgetPasswordDto dto = new ConfirmForgetPasswordDto();
        ConfirmForgetPasswordResponseDto responseDto = new ConfirmForgetPasswordResponseDto();
        responseDto.setSuccess(true);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(dto))));
        when(awsCognitoService.confirmForgetPassword(any(ConfirmForgetPasswordDto.class))).thenReturn(responseDto);

        confirmForgetPassword.doPost(request, response);

        verify(response, never()).setStatus(400);
        verify(printWriter, never()).println(anyString());
    }

    @Test
    void testDoPostFailure() throws IOException {
        ConfirmForgetPasswordDto dto = new ConfirmForgetPasswordDto();
        ConfirmForgetPasswordResponseDto responseDto = new ConfirmForgetPasswordResponseDto();
        responseDto.setSuccess(false);
        when(response.getWriter()).thenReturn(printWriter);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(dto))));
        when(awsCognitoService.confirmForgetPassword(any(ConfirmForgetPasswordDto.class))).thenReturn(responseDto);

        confirmForgetPassword.doPost(request, response);

        verify(response).setStatus(400);
        verify(response).setContentType("application/json");
        verify(printWriter).println(gson.toJson(responseDto));
    }
}