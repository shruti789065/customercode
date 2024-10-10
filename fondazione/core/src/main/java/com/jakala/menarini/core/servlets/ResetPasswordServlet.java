package com.jakala.menarini.core.servlets;


import com.google.gson.Gson;
import com.jakala.menarini.core.dto.cognitoDto.CognitoSignInErrorResponseDto;
import com.jakala.menarini.core.dto.cognitoDto.ResetPasswordDto;
import com.jakala.menarini.core.dto.cognitoDto.ResetPasswordResponseDto;
import com.jakala.menarini.core.service.interfaces.AwsCognitoServiceInterface;
import com.jakala.menarini.core.service.interfaces.EncryptDataServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.http.Cookie;
import java.io.IOException;

@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.paths=/private/api/resetPassword",
                "sling.servlet.methods={POST}",
                "sling.servlet.extensions=json"

        }
)
public class ResetPasswordServlet extends SlingAllMethodsServlet {


    @Reference
    private transient AwsCognitoServiceInterface awsCognitoService;
    @Reference
    private transient EncryptDataServiceInterface encryptDataService;

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        ResetPasswordDto resetPasswordDto = gson.fromJson(request.getReader(), ResetPasswordDto.class);
        Cookie[] cookies = request.getCookies();
        boolean tokenIsPresent = false;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("p-aToken")) {
                    String decodedToken = encryptDataService.decrypt(cookie.getValue());
                    resetPasswordDto.setAccessToken(decodedToken);
                    tokenIsPresent = true;
                }
            }
        }
        if (!tokenIsPresent) {
            response.setStatus(400);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            ResetPasswordResponseDto errorResponseDto = new ResetPasswordResponseDto();
            errorResponseDto.setSuccess(Boolean.FALSE);
            CognitoSignInErrorResponseDto cognitoSignInErrorResponseDto = new CognitoSignInErrorResponseDto();
            cognitoSignInErrorResponseDto.set__type("BadRequest");
            cognitoSignInErrorResponseDto.setMessage("missing access token");
            errorResponseDto.setError(cognitoSignInErrorResponseDto);
            response.getWriter().print(gson.toJson(errorResponseDto));
            return;
        }

        ResetPasswordResponseDto awsResponse = awsCognitoService.resetPassword(resetPasswordDto);
        if(!awsResponse.isSuccess()) {
            response.setStatus(400);
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(awsResponse));
    }



}
