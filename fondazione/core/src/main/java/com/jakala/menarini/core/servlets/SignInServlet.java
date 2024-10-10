package com.jakala.menarini.core.servlets;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.cognitoDto.SignInDto;
import com.jakala.menarini.core.dto.cognitoDto.SignInResponseDto;
import com.jakala.menarini.core.dto.cognitoDto.SignUpDtoResponse;
import com.jakala.menarini.core.service.CookieService;
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
    private AwsCognitoServiceInterface awsCognitoService;
    @Reference
    private CookieServiceInterface cookieService;
    @Reference
    private EncryptDataServiceInterface encryptDataService;

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
            String refreshToken = encryptDataService.encrypt(awsResponse.getCognitoAuthResultDto().getAccessToken());
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

