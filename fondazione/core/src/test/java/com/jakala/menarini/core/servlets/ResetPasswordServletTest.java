package com.jakala.menarini.core.servlets;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.cognitoDto.CognitoSignInErrorResponseDto;
import com.jakala.menarini.core.dto.cognitoDto.ResetPasswordDto;
import com.jakala.menarini.core.dto.cognitoDto.ResetPasswordResponseDto;
import com.jakala.menarini.core.service.interfaces.AwsCognitoServiceInterface;
import com.jakala.menarini.core.service.interfaces.EncryptDataServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.Cookie;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordServletTest {

    @InjectMocks
    private ResetPasswordServlet resetPasswordServlet;

    @Mock
    private AwsCognitoServiceInterface awsCognitoService;

    @Mock
    private EncryptDataServiceInterface encryptDataService;

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
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        ResetPasswordResponseDto awsResponse = new ResetPasswordResponseDto();
        awsResponse.setSuccess(true);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(resetPasswordDto))));
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("p-aToken", "encryptedToken")});
        when(encryptDataService.decrypt("encryptedToken")).thenReturn("decodedToken");
        when(awsCognitoService.resetPassword(any(ResetPasswordDto.class))).thenReturn(awsResponse);

        resetPasswordServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        //verify(response).setStatus(200);
        verify(printWriter).write(gson.toJson(awsResponse));
    }

    @Test
    void testDoPostMissingToken() throws IOException {
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        ResetPasswordResponseDto errorResponseDto = new ResetPasswordResponseDto();
        errorResponseDto.setSuccess(false);
        CognitoSignInErrorResponseDto cognitoSignInErrorResponseDto = new CognitoSignInErrorResponseDto();
        cognitoSignInErrorResponseDto.set__type("BadRequest");
        cognitoSignInErrorResponseDto.setMessage("missing access token");
        errorResponseDto.setError(cognitoSignInErrorResponseDto);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(resetPasswordDto))));
        when(request.getCookies()).thenReturn(null);

        resetPasswordServlet.doPost(request, response);

        verify(response).setStatus(400);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(printWriter).print(gson.toJson(errorResponseDto));
    }

    @Test
    void testDoPostFailure() throws IOException {
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        ResetPasswordResponseDto awsResponse = new ResetPasswordResponseDto();
        awsResponse.setSuccess(false);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(resetPasswordDto))));
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("p-aToken", "encryptedToken")});
        when(encryptDataService.decrypt("encryptedToken")).thenReturn("decodedToken");
        when(awsCognitoService.resetPassword(any(ResetPasswordDto.class))).thenReturn(awsResponse);

        resetPasswordServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(400);
        verify(printWriter).write(gson.toJson(awsResponse));
    }
}