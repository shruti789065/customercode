package com.jakala.menarini.core.service;

import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.dto.cognitoDto.*;
import com.jakala.menarini.core.service.interfaces.RoleServiceInterface;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AwsCognitoServiceTest {

    @InjectMocks
    private AwsCognitoService awsCognitoService;

    @Mock
    private UserRegisteredServiceInterface userRegisteredService;

    @Mock
    private RoleServiceInterface roleService;

    @Mock
    private HttpEntity httpEntity;

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private CloseableHttpResponse httpResponse;

    @Mock
    private StatusLine statusLine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Map<String, Object> properties = new HashMap<>();
        properties.put("clientId", "testClientId");
        properties.put("clientSecret", "testClientSecret");
        properties.put("accessKey", "testAccessKey");
        properties.put("secretKey", "testSecretKey");
        properties.put("awsRegion", "testRegion");
        properties.put("idpUrl", "http://testUrl");
        awsCognitoService.activate(properties);
    }

    @Test
    void testLoginOnCognito() throws Exception {
        SignInDto signInDto = new SignInDto();
        signInDto.setEmail("test@example.com");
        signInDto.setPassword("password");
        String jsonResponse = "{\"AuthenticationResult\":{\"AccessToken\":\"testToken\"}}";

        try (MockedStatic<HttpClients> mockedHttpClients = mockStatic(HttpClients.class);
             MockedStatic<EntityUtils> mockedEntityUtils = mockStatic(EntityUtils.class)) {
            mockedHttpClients.when(HttpClients::createDefault).thenReturn(httpClient);
            when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
            when(httpResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(200);
            InputStream inputStream = new ByteArrayInputStream(jsonResponse.getBytes(StandardCharsets.UTF_8));
            when(httpResponse.getEntity()).thenReturn(httpEntity);
            when(httpEntity.getContent()).thenReturn(inputStream);
            mockedEntityUtils.when(() -> EntityUtils.toString(httpEntity)).thenReturn(jsonResponse);

            SignInResponseDto response = awsCognitoService.loginOnCognito(signInDto);

            assertNotNull(response);
            assertEquals("testToken", response.getCognitoAuthResultDto().getAccessToken());
        }
    }

    @Test
    void testRefreshOnCognito() throws Exception {
        RefreshDto refreshDto = new RefreshDto();
        refreshDto.setEmail("test@example.com");
        refreshDto.setRefreshToken("refreshToken");
        String jsonResponse = "{\"AuthenticationResult\":{\"AccessToken\":\"testToken\"}}";

        try (MockedStatic<HttpClients> mockedHttpClients = mockStatic(HttpClients.class);
             MockedStatic<EntityUtils> mockedEntityUtils = mockStatic(EntityUtils.class)) {
            mockedHttpClients.when(HttpClients::createDefault).thenReturn(httpClient);
            when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
            when(httpResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(200);
            InputStream inputStream = new ByteArrayInputStream(jsonResponse.getBytes(StandardCharsets.UTF_8));
            when(httpResponse.getEntity()).thenReturn(httpEntity);
            when(httpEntity.getContent()).thenReturn(inputStream);
            mockedEntityUtils.when(() -> EntityUtils.toString(httpEntity)).thenReturn(jsonResponse);

            SignInResponseDto response = awsCognitoService.refreshOnCognito(refreshDto);

            assertNotNull(response);
            assertEquals("testToken", response.getCognitoAuthResultDto().getAccessToken());
        }
    }

    @Test
    void testRegisterOnCognito() throws Exception {
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setEmail("test@example.com");
        signUpDto.setPassword("password");
        signUpDto.setFirstName("First");
        signUpDto.setLastName("Last");
        signUpDto.setProfession("Doctor");
        String jsonResponse = "{\"UserSub\":\"testSub\"}";

        try (MockedStatic<HttpClients> mockedHttpClients = mockStatic(HttpClients.class);
             MockedStatic<EntityUtils> mockedEntityUtils = mockStatic(EntityUtils.class)) {
            mockedHttpClients.when(HttpClients::createDefault).thenReturn(httpClient);
            when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
            when(httpResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(200);
            InputStream inputStream = new ByteArrayInputStream(jsonResponse.getBytes(StandardCharsets.UTF_8));
            when(httpResponse.getEntity()).thenReturn(httpEntity);
            when(httpEntity.getContent()).thenReturn(inputStream);
            mockedEntityUtils.when(() -> EntityUtils.toString(httpEntity)).thenReturn(jsonResponse);
            when(userRegisteredService.addUserForSignUp(any(), any(), any())).thenReturn(true);

            SignUpDtoResponse response = awsCognitoService.registerOnCognito(signUpDto);

            assertNotNull(response);
            assertEquals("testSub", response.getUserSub());
        }
    }

    @Test
    void testForgetPassword() throws Exception {
        ForgetPasswordDto forgetPasswordDto = new ForgetPasswordDto();
        forgetPasswordDto.setEmail("test@example.com");

        String jsonResponse = "{\"CodeDeliveryDetails\":{}}";

        try (MockedStatic<HttpClients> mockedHttpClients = mockStatic(HttpClients.class);
             MockedStatic<EntityUtils> mockedEntityUtils = mockStatic(EntityUtils.class)) {
            mockedHttpClients.when(HttpClients::createDefault).thenReturn(httpClient);
            when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
            when(httpResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(200);
            InputStream inputStream = new ByteArrayInputStream(jsonResponse.getBytes(StandardCharsets.UTF_8));
            when(httpResponse.getEntity()).thenReturn(httpEntity);
            when(httpEntity.getContent()).thenReturn(inputStream);
            mockedEntityUtils.when(() -> EntityUtils.toString(httpEntity)).thenReturn(jsonResponse);

            ForgetPasswordResponseDto response = awsCognitoService.forgetPassword(forgetPasswordDto);

            assertNotNull(response);
            assertTrue(response.isSuccess());
        }
    }

    @Test
    void testConfirmForgetPassword() throws Exception {
        ConfirmForgetPasswordDto confirmForgetPasswordDto = new ConfirmForgetPasswordDto();
        confirmForgetPasswordDto.setEmail("test@example.com");
        confirmForgetPasswordDto.setPassword("newPassword");
        confirmForgetPasswordDto.setConfirmCode("123456");

        String jsonResponse = "{}";

        try (MockedStatic<HttpClients> mockedHttpClients = mockStatic(HttpClients.class);
             MockedStatic<EntityUtils> mockedEntityUtils = mockStatic(EntityUtils.class)) {
            mockedHttpClients.when(HttpClients::createDefault).thenReturn(httpClient);
            when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
            when(httpResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(200);
            InputStream inputStream = new ByteArrayInputStream(jsonResponse.getBytes(StandardCharsets.UTF_8));
            when(httpResponse.getEntity()).thenReturn(httpEntity);
            when(httpEntity.getContent()).thenReturn(inputStream);
            mockedEntityUtils.when(() -> EntityUtils.toString(httpEntity)).thenReturn(jsonResponse);

            ConfirmForgetPasswordResponseDto response = awsCognitoService.confirmForgetPassword(confirmForgetPasswordDto);

            assertNotNull(response);
            assertTrue(response.isSuccess());
        }

    }

    @Test
    void testResetPassword() throws Exception {
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
        resetPasswordDto.setAccessToken("accessToken");
        resetPasswordDto.setPreviousPassword("oldPassword");
        resetPasswordDto.setProposedPassword("newPassword");

        String jsonResponse = "{}";

        try (MockedStatic<HttpClients> mockedHttpClients = mockStatic(HttpClients.class);
             MockedStatic<EntityUtils> mockedEntityUtils = mockStatic(EntityUtils.class)) {
            mockedHttpClients.when(HttpClients::createDefault).thenReturn(httpClient);
            when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
            when(httpResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(200);
            InputStream inputStream = new ByteArrayInputStream(jsonResponse.getBytes(StandardCharsets.UTF_8));
            when(httpResponse.getEntity()).thenReturn(httpEntity);
            when(httpEntity.getContent()).thenReturn(inputStream);
            mockedEntityUtils.when(() -> EntityUtils.toString(httpEntity)).thenReturn(jsonResponse);

            ResetPasswordResponseDto response = awsCognitoService.resetPassword(resetPasswordDto);

            assertNotNull(response);
            assertTrue(response.isSuccess());
        }
    }
}