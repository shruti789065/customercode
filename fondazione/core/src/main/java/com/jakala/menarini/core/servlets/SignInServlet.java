package com.jakala.menarini.core.servlets;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.cognito.SignInDto;
import com.jakala.menarini.core.dto.cognito.SignInResponseDto;
import com.jakala.menarini.core.service.interfaces.AwsCognitoServiceInterface;
import com.jakala.menarini.core.service.interfaces.CookieServiceInterface;
import com.jakala.menarini.core.service.interfaces.EncryptDataServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;

@SuppressWarnings("CQRules:CQBP-75")
@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.paths=/bin/api/awsSignIn",
                "sling.servlet.methods={POST}",
                "sling.servlet.extensions=json"

        }
)
public class SignInServlet extends SlingAllMethodsServlet {

    @Reference
    private transient AwsCognitoServiceInterface awsCognitoService;
    @Reference
    private transient CookieServiceInterface cookieService;
    @Reference
    private transient EncryptDataServiceInterface encryptDataService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        SignInDto signInDto = gson.fromJson(request.getReader(), SignInDto.class);
        SignInResponseDto awsResponse = awsCognitoService.loginOnCognito(signInDto);
        String stringResponse = gson.toJson(awsResponse);
        if (awsResponse.getCognitoSignInErrorResponseDto() == null) {
            response.setContentType("application/json");
            String idToken =  encryptDataService.encrypt(awsResponse.getCognitoAuthResultDto().getIdToken());
            String accessToken = encryptDataService.encrypt(awsResponse.getCognitoAuthResultDto().getAccessToken());
            String refreshToken = encryptDataService.encrypt(awsResponse.getCognitoAuthResultDto().getRefreshToken());
            HashMap<String,String> mapCookie = new HashMap<>();
            mapCookie.put("p-idToken", idToken);
            mapCookie.put("p-aToken", accessToken);
            mapCookie.put("p-rToken", refreshToken);
            cookieService.setCookie(response,mapCookie, signInDto.getRememberMe());
            response.getWriter().println(stringResponse);
        } else {
            response.setContentType("application/json");
            response.setStatus(400);
            response.getWriter().println(stringResponse);
        }


    }


}

