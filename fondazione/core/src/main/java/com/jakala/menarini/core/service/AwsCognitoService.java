package com.jakala.menarini.core.service;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.dto.cognito.*;
import com.jakala.menarini.core.exceptions.AwsServiceException;
import com.jakala.menarini.core.service.interfaces.AwsCognitoServiceInterface;
import com.jakala.menarini.core.service.interfaces.RoleServiceInterface;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;
import io.jsonwebtoken.io.IOException;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component(
        service = AwsCognitoServiceInterface.class
)
public class AwsCognitoService implements AwsCognitoServiceInterface {

    public static final String USER_MAIL_NOT_CONFIRMED = "waiting_confirm";
    private static final Logger LOGGER = LoggerFactory.getLogger(AwsCognitoService.class);

    @Reference
    private UserRegisteredServiceInterface userRegisteredService;
    @Reference
    private RoleServiceInterface roleService;


    private static final String USER_ROLE_DOCTOR = "UserDoctor";
    private static final String USER_ROLE_HEALTH = "UserHealthCare";


    private static final Map<String,String> MAP_PROFESSION_TO_ROLE;
    static {
        MAP_PROFESSION_TO_ROLE  = new HashMap<>();
        MAP_PROFESSION_TO_ROLE .put("Biologist", USER_ROLE_HEALTH);
        MAP_PROFESSION_TO_ROLE .put("Diagnostic Laboratory Technician", USER_ROLE_HEALTH);
        MAP_PROFESSION_TO_ROLE .put("Doctor", USER_ROLE_DOCTOR);
        MAP_PROFESSION_TO_ROLE .put("Healthcare Worker", USER_ROLE_HEALTH);
        MAP_PROFESSION_TO_ROLE .put("No Healthcare", "User");
        MAP_PROFESSION_TO_ROLE .put("Nurse", USER_ROLE_DOCTOR);
        MAP_PROFESSION_TO_ROLE .put("Nurse’s Assistant", USER_ROLE_DOCTOR);
        MAP_PROFESSION_TO_ROLE .put("Obstetrician", USER_ROLE_DOCTOR);
        MAP_PROFESSION_TO_ROLE .put("Pharmacist", USER_ROLE_HEALTH);
        MAP_PROFESSION_TO_ROLE .put("Psichologist", USER_ROLE_DOCTOR);
        MAP_PROFESSION_TO_ROLE .put("Student", "User");
    }


    private String clientId;
    private String clientSecret;
    private String accessKey;
    private String secretKey;
    private String awsRegion;
    private String idpUrl;

    @Activate
    void activate(Map<String, Object> properties) {
        this.clientId = (String) properties.get("clientId");
        this.clientSecret = (String) properties.get("clientSecret");
        this.accessKey = (String) properties.get("accessKey");
        this.secretKey = (String) properties.get("secretKey");
        this.awsRegion = (String) properties.get("awsRegion");
        this.idpUrl = (String) properties.get("idpUrl");
    }

    @Override
    public SignInResponseDto loginOnCognito(SignInDto signInDto) {
        Gson gson = new Gson();
        String username = signInDto.getEmail().replace("@","_");
        String secretHash = calculateSecretHash(username);
        CognitoRequestSignInDto cognitoRequestSignInDto = new CognitoRequestSignInDto();
        cognitoRequestSignInDto.setClientId(clientId);
        CognitoAuthParametersDto cognitoAuthParametersDto = new CognitoAuthParametersDto();
        cognitoAuthParametersDto.setPASSWORD(signInDto.getPassword());
        cognitoAuthParametersDto.setUSERNAME(username);
        cognitoAuthParametersDto.setSECRET_HASH(secretHash);
        cognitoRequestSignInDto.setAuthParameters(cognitoAuthParametersDto);
        String body = gson.toJson(cognitoRequestSignInDto);
        HttpPost httpPost = new HttpPost(this.idpUrl);
        return handleSignInRequest(body,httpPost);

    }

    @Override
    public SignInResponseDto refreshOnCognito(RefreshDto requestRefreshDto) {
        Gson gson = new Gson();
        String username = requestRefreshDto.getEmail().replace("@","_");
        String secretHash = calculateSecretHash(username);

        CognitoRefreshAuthParamDto cognitoRefreshAuthParamDto = new CognitoRefreshAuthParamDto();
        cognitoRefreshAuthParamDto.setREFRESH_TOKEN(requestRefreshDto.getRefreshToken());
        cognitoRefreshAuthParamDto.setSECRET_HASH(secretHash);
        CognitoRequestRefreshDto cognitoRequestRefreshDto = new CognitoRequestRefreshDto();
        cognitoRequestRefreshDto.setClientId(clientId);
        cognitoRequestRefreshDto.setAuthParameters(cognitoRefreshAuthParamDto);

        String body = gson.toJson(cognitoRequestRefreshDto);

        HttpPost httpPost = new HttpPost(this.idpUrl);
        return handleSignInRequest(body, httpPost);



    }

    private SignInResponseDto handleSignInRequest(String body, HttpPost httpPost) {
        Gson gson = new Gson();
        Date now = new Date();
        String dateStr = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ").format(now);
        httpPost.setHeader("Content-Type", "application/x-amz-json-1.1");
        httpPost.setHeader("X-Amz-Target", "AWSCognitoIdentityProviderService.InitiateAuth");
        httpPost.setHeader("X-Amz-Date", dateStr);
        String authString = generateAuthString();
        httpPost.setHeader("Authorization", authString);
        httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        try(CloseableHttpResponse httpResponse = (HttpClients.createDefault()).execute(httpPost)) {
            String response = readHttpResponse(httpResponse).toString();
            if(httpResponse.getStatusLine().getStatusCode() == 200) {
                return gson.fromJson(response, SignInResponseDto.class);
            } else {
                CognitoSignInErrorResponseDto sigInResponse =
                        gson.fromJson(response, CognitoSignInErrorResponseDto.class);
                SignInResponseDto signInResponseDto = new SignInResponseDto();
                signInResponseDto.setCognitoSignInErrorResponseDto(sigInResponse);
                return signInResponseDto;

            }
        } catch (java.io.IOException io) {
            SignInResponseDto signInResponseDto = new SignInResponseDto();
            CognitoSignInErrorResponseDto cognitoSignInErrorResponseDto = new CognitoSignInErrorResponseDto();
            cognitoSignInErrorResponseDto.set__type("ExecutionException");
            cognitoSignInErrorResponseDto.setMessage(io.getMessage());
            signInResponseDto.setCognitoSignInErrorResponseDto(cognitoSignInErrorResponseDto);
            return signInResponseDto;
        }
    }

    @Override
    public SignUpDtoResponse registerOnCognito(SignUpDto registrationData) {
        registrationData.setRegistrationStatus(USER_MAIL_NOT_CONFIRMED);
        registrationData.getRolesNames().add(MAP_PROFESSION_TO_ROLE
                .get(registrationData.getProfession()));
        LOGGER.error("====== on call service =====");

        Gson gson = new Gson();
        Date now = new Date();
        String dateStr = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ").format(now);
        HttpPost httpPost = new HttpPost(this.idpUrl);
        httpPost.setHeader("Content-Type", "application/x-amz-json-1.1");
        httpPost.setHeader("X-Amz-Target", "AWSCognitoIdentityProviderService.SignUp");
        httpPost.setHeader("X-Amz-Date", dateStr);
        String authString = generateAuthString();
        httpPost.setHeader("Authorization", authString);

        String userMail = registrationData.getEmail();
        String userName = userMail.replace("@","_");
        String secretHash = calculateSecretHash(userName);
        CognitoRequestSignUpDto cognitoRequest = new CognitoRequestSignUpDto();
        cognitoRequest.setClientId(clientId);
        cognitoRequest.setUsername(userName);
        cognitoRequest.setSecretHash(secretHash);
        cognitoRequest.setPassword(registrationData.getPassword());
        CognitoRequestUserAttributeDto userAttributeEmail = new CognitoRequestUserAttributeDto();
        userAttributeEmail.setName("email");
        userAttributeEmail.setValue(userMail);
        cognitoRequest.getUserAttributes().add(userAttributeEmail);
        CognitoRequestUserAttributeDto userAttributeFirstName = new CognitoRequestUserAttributeDto();
        userAttributeFirstName.setName("given_name");
        userAttributeFirstName.setValue(registrationData.getFirstName());
        cognitoRequest.getUserAttributes().add(userAttributeFirstName);
        CognitoRequestUserAttributeDto userAttributeFamilyName = new CognitoRequestUserAttributeDto();
        userAttributeFamilyName.setName("family_name");
        userAttributeFamilyName.setValue(registrationData.getLastName());
        cognitoRequest.getUserAttributes().add(userAttributeFamilyName);

        String payload = gson.toJson(cognitoRequest);
        httpPost.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));
        LOGGER.error("====== on call aws =====");
        try(CloseableHttpResponse httpResponse = (HttpClients.createDefault()).execute(httpPost)) {
            LOGGER.error("========== Response ============");
            StringBuffer responseString = readHttpResponse(httpResponse);
            if(httpResponse.getStatusLine().getStatusCode() == 200) {
                SignUpDtoResponse dataResponse = gson.fromJson(responseString.toString(), SignUpDtoResponse.class);
                List<RoleDto> listOfRoles =  roleService.getRoles();
                dataResponse.copyRegistrationData(registrationData,listOfRoles);
                RegisteredUserDto dto = dataResponse.generateRegisteredUser(userName);
                final boolean isSaved = userRegisteredService.addUserForSignUp(dto, dataResponse.getRoles(), dataResponse.getTopics());
                if(!isSaved) {
                    LOGGER.error("========== error on save ============");
                    SignUpDtoResponse errorResponse = new SignUpDtoResponse();
                    CognitoSignInErrorResponseDto error = new CognitoSignInErrorResponseDto();
                    errorResponse.setCognitoSignUpErrorResponseDto(error);
                    error.set__type("AccountNotSavedException");
                    error.setMessage("Account not saved");
                    return errorResponse;
                }
                return dataResponse;
            } else {
                CognitoSignInErrorResponseDto cognitoSignInErrorResponseDto = gson.fromJson(responseString.toString(),
                        CognitoSignInErrorResponseDto.class);
                SignUpDtoResponse errorResponse = new SignUpDtoResponse();
                errorResponse.setCognitoSignUpErrorResponseDto(cognitoSignInErrorResponseDto);
                LOGGER.error("========== Invalid Response ============");
                return errorResponse;
            }

        } catch (java.io.IOException io) {
            throw new AwsServiceException(io.getMessage());
        }
    }

    @Override
    public ForgetPasswordResponseDto forgetPassword(ForgetPasswordDto forgetPasswordDto) {
        ForgetPasswordResponseDto forgetPasswordResponseDto = new ForgetPasswordResponseDto();
        Gson gson = new Gson();
        Date now = new Date();
        String dateStr = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ").format(now);
        HttpPost httpPost = new HttpPost(this.idpUrl);
        httpPost.setHeader("Content-Type", "application/x-amz-json-1.1");
        httpPost.setHeader("X-Amz-Target", "AWSCognitoIdentityProviderService.ForgotPassword");
        httpPost.setHeader("X-Amz-Date", dateStr);
        String authString = generateAuthString();
        httpPost.setHeader("Authorization", authString);
        String secretHash = calculateSecretHash(forgetPasswordDto.getEmail());
        CognitoForgetPasswordDto cognitoForgetPasswordDto = new CognitoForgetPasswordDto();
        cognitoForgetPasswordDto.setClientId(clientId);
        cognitoForgetPasswordDto.setSecretHash(secretHash);
        cognitoForgetPasswordDto.setUsername(forgetPasswordDto.getEmail());
        httpPost.setEntity(new StringEntity(gson.toJson(cognitoForgetPasswordDto), StandardCharsets.UTF_8));
        try(CloseableHttpResponse httpResponse = (HttpClients.createDefault()).execute(httpPost)) {
            StringBuffer responseString = readHttpResponse(httpResponse);
            if(httpResponse.getStatusLine().getStatusCode() == 200) {
                CognitoForgetPasswordResponseDto cognitoResponse =
                        gson.fromJson(responseString.toString(), CognitoForgetPasswordResponseDto.class);
                        forgetPasswordResponseDto.setCognitoForgetPasswordResponseDto(cognitoResponse);
                        forgetPasswordResponseDto.setSuccess(Boolean.TRUE);
                        return forgetPasswordResponseDto;
            }
            forgetPasswordResponseDto.setSuccess(Boolean.FALSE);
            CognitoSignInErrorResponseDto cognitoSignInErrorResponseDto =
                    gson.fromJson(responseString.toString(),CognitoSignInErrorResponseDto.class);
            forgetPasswordResponseDto.setCognitoError(cognitoSignInErrorResponseDto);
            return forgetPasswordResponseDto;
        }catch (java.io.IOException io) {
            throw new AwsServiceException(io.getMessage());
        }
    }

    @Override
    public ConfirmForgetPasswordResponseDto confirmForgetPassword(ConfirmForgetPasswordDto forgetPassword) {
        ConfirmForgetPasswordResponseDto confirmForgetPasswordResponseDto = new ConfirmForgetPasswordResponseDto();
        Gson gson = new Gson();
        Date now = new Date();
        String dateStr = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ").format(now);
        HttpPost httpPost = new HttpPost(this.idpUrl);
        httpPost.setHeader("Content-Type", "application/x-amz-json-1.1");
        httpPost.setHeader("X-Amz-Target", "AWSCognitoIdentityProviderService.ConfirmForgotPassword");
        httpPost.setHeader("X-Amz-Date", dateStr);
        String authString = generateAuthString();
        httpPost.setHeader("Authorization", authString);
        String secretHash = calculateSecretHash(forgetPassword.getEmail());
        CognitoConfirmForgetPassword cognitoConfirmForgetPassword = new CognitoConfirmForgetPassword();
        cognitoConfirmForgetPassword.setClientId(clientId);
        cognitoConfirmForgetPassword.setSecretHash(secretHash);
        cognitoConfirmForgetPassword.setUsername(forgetPassword.getEmail());
        cognitoConfirmForgetPassword.setPassword(forgetPassword.getPassword());
        cognitoConfirmForgetPassword.setConfirmationCode(forgetPassword.getConfirmCode());
        httpPost.setEntity(new StringEntity(gson.toJson(cognitoConfirmForgetPassword), StandardCharsets.UTF_8));
        try(CloseableHttpResponse httpResponse = (HttpClients.createDefault()).execute(httpPost)){
            StringBuffer responseString = readHttpResponse(httpResponse);
            if(httpResponse.getStatusLine().getStatusCode() == 200) {
                LOGGER.error("=========== RESET COMPLETE =========");
                confirmForgetPasswordResponseDto.setSuccess(Boolean.TRUE);
                return confirmForgetPasswordResponseDto;
            }
            confirmForgetPasswordResponseDto.setSuccess(Boolean.FALSE);
            CognitoSignInErrorResponseDto error =
                    gson.fromJson(responseString.toString(),CognitoSignInErrorResponseDto.class);
            LOGGER.error("=======  RESET ERROR =======");
            LOGGER.error(gson.toJson(error));
            return confirmForgetPasswordResponseDto;
        }catch (java.io.IOException io) {
            throw new AwsServiceException(io.getMessage());
        }
    }


    public ResetPasswordResponseDto resetPassword(ResetPasswordDto resetPasswordDto) {
        ResetPasswordResponseDto awsResponse = new ResetPasswordResponseDto();
        Gson gson = new Gson();
        Date now = new Date();
        String dateStr = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ").format(now);
        HttpPost httpPost = new HttpPost(this.idpUrl);
        httpPost.setHeader("Content-Type", "application/x-amz-json-1.1");
        httpPost.setHeader("X-Amz-Target", "AWSCognitoIdentityProviderService.ChangePassword");
        httpPost.setHeader("X-Amz-Date", dateStr);
        httpPost.setEntity(new StringEntity(gson.toJson(resetPasswordDto), StandardCharsets.UTF_8));
        try(CloseableHttpResponse httpResponse = (HttpClients.createDefault()).execute(httpPost)){
            StringBuffer responseString = readHttpResponse(httpResponse);
            if(httpResponse.getStatusLine().getStatusCode() == 200) {
                awsResponse.setSuccess(Boolean.TRUE);
                return awsResponse;
            }
            awsResponse.setSuccess(Boolean.FALSE);
            CognitoSignInErrorResponseDto error = gson.fromJson(
                    responseString.toString(),CognitoSignInErrorResponseDto.class
            );
            awsResponse.setError(error);
            return awsResponse;
        }catch (java.io.IOException io) {
            throw new AwsServiceException(io.getMessage());
        }

    }


    private StringBuffer readHttpResponse(CloseableHttpResponse httpResponse) throws IOException, java.io.IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                httpResponse.getEntity().getContent()));

        String inputBuffer;
        StringBuffer responseString = new StringBuffer();

        while ((inputBuffer = reader.readLine()) != null) {
            responseString.append(inputBuffer);
        }
        return responseString;
    }



    private String generateAuthString() {
        Date now = new Date();
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(now);
        String base = "AWS4-HMAC-SHA256 Credential="
                + this.accessKey+"/"+dateStr+"/"+this.awsRegion+"/execute-api/aws4_request";
        base += ",SignedHeaders=content-length;content-type;host;x-amz-content-sha256;x-amz-date;x-amz-target";
        String sig = generateSignature(dateStr, base);
        base += ",Signature:" +sig;
        return base;
    }

    private String calculateSecretHash( String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
        SecretKeySpec signingKey = new SecretKeySpec(
                this.clientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(this.clientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalArgumentException("Error while calculating ");
        }
    }

    private String generateSignature(String dateStr, String authHeader) {
        String dateKey = hmacsEncoder("AWS"+this.secretKey,dateStr);
        String regionKey = hmacsEncoder(dateKey,this.awsRegion);
        String serviceKey = hmacsEncoder(regionKey,"execute-api");
        String requestKey = hmacsEncoder(serviceKey,"aws4_request");
        return  hmacsEncoder(requestKey,authHeader);
    }

    private String hmacsEncoder(String key, String data) {
        try {
            Mac encoder = Mac.getInstance("HmacSHA256");
            SecretKeySpec keyData = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            encoder.init(keyData);
            return Hex.encodeHexString(encoder.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new AwsServiceException(e.getMessage());
        }
    }




}
