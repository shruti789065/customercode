package com.jakala.menarini.core.servlets;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.cognitoDto.CognitoAuthResultDto;
import com.jakala.menarini.core.dto.cognitoDto.CognitoSignInErrorResponseDto;
import com.jakala.menarini.core.dto.cognitoDto.SignInDto;
import com.jakala.menarini.core.dto.cognitoDto.SignInResponseDto;
import com.jakala.menarini.core.service.interfaces.AwsCognitoServiceInterface;
import com.jakala.menarini.core.service.interfaces.CookieServiceInterface;
import com.jakala.menarini.core.service.interfaces.EncryptDataServiceInterface;
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
import java.util.HashMap;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignInServletTest {

    @InjectMocks
    private SignInServlet signInServlet;

    @Mock
    private AwsCognitoServiceInterface awsCognitoService;

    @Mock
    private CookieServiceInterface cookieService;

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
        SignInDto signInDto = new SignInDto();
        SignInResponseDto awsResponse = new SignInResponseDto();
        awsResponse.setCognitoAuthResultDto(new CognitoAuthResultDto());
        awsResponse.getCognitoAuthResultDto().setIdToken("idToken");
        awsResponse.getCognitoAuthResultDto().setAccessToken("accessToken");
        awsResponse.getCognitoAuthResultDto().setRefreshToken("refreshToken");

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(signInDto))));
        when(awsCognitoService.loginOnCognito(any(SignInDto.class))).thenReturn(awsResponse);
        when(encryptDataService.encrypt("idToken")).thenReturn("encryptedIdToken");
        when(encryptDataService.encrypt("accessToken")).thenReturn("encryptedAccessToken");
        when(encryptDataService.encrypt("refreshToken")).thenReturn("encryptedRefreshToken");

        signInServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        //verify(response).setStatus(200);
        verify(printWriter).println(gson.toJson(awsResponse));

        HashMap<String, String> mapCookie = new HashMap<>();
        mapCookie.put("p-idToken", "encryptedIdToken");
        mapCookie.put("p-aToken", "encryptedAccessToken");
        mapCookie.put("p-rToken", "encryptedRefreshToken");
        verify(cookieService).setCookie(response, mapCookie, signInDto.getRememberMe());
    }

    @Test
    void testDoPostFailure() throws IOException {
        SignInDto signInDto = new SignInDto();
        SignInResponseDto awsResponse = new SignInResponseDto();
        awsResponse.setCognitoSignInErrorResponseDto(new CognitoSignInErrorResponseDto()); // Mock error response

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(gson.toJson(signInDto))));
        when(awsCognitoService.loginOnCognito(any(SignInDto.class))).thenReturn(awsResponse);

        signInServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(400);
        verify(printWriter).println(gson.toJson(awsResponse));
    }
}