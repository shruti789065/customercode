package com.jakala.menarini.core.servlets;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.cognitoDto.CognitoSignInErrorResponseDto;
import com.jakala.menarini.core.dto.cognitoDto.SignUpDto;
import com.jakala.menarini.core.dto.cognitoDto.SignUpDtoResponse;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignupServletTest {

    @InjectMocks
    private SignupServlet signupServlet;

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
        SignUpDto signUpDto = new SignUpDto();
        SignUpDtoResponse awsResponse = new SignUpDtoResponse();

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(signUpDto))));
        when(awsCognitoService.registerOnCognito(any(SignUpDto.class))).thenReturn(awsResponse);

        signupServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        //verify(response).setStatus(200);
        verify(printWriter).println(gson.toJson(awsResponse));
    }

    @Test
    void testDoPostFailure() throws IOException {
        SignUpDto signUpDto = new SignUpDto();
        SignUpDtoResponse awsResponse = new SignUpDtoResponse();
        awsResponse.setCognitoSignUpErrorResponseDto(new CognitoSignInErrorResponseDto()); // Mock error response

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(signUpDto))));
        when(awsCognitoService.registerOnCognito(any(SignUpDto.class))).thenReturn(awsResponse);

        signupServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(400);
        verify(printWriter).println(gson.toJson(awsResponse));
    }
}