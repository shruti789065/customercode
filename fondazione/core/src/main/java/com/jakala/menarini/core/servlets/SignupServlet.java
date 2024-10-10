package com.jakala.menarini.core.servlets;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.cognitoDto.SignUpDto;
import com.jakala.menarini.core.dto.cognitoDto.SignUpDtoResponse;
import com.jakala.menarini.core.service.interfaces.AwsCognitoServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.paths=/bin/api/awsSignUp",
                "sling.servlet.methods={POST}",
                "sling.servlet.extensions=json"

        }
)
public class SignupServlet extends SlingAllMethodsServlet {

    @Reference
    private transient AwsCognitoServiceInterface awsCognitoService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        SignUpDto signUpDto = gson.fromJson(request.getReader(), SignUpDto.class);
        SignUpDtoResponse awsResponse = awsCognitoService.registerOnCognito(signUpDto);
        if (awsResponse.getCognitoSignUpErrorResponseDto() != null) {
            String successResponse = gson.toJson(awsResponse);
            response.setStatus(400);
            response.setContentType("application/json");
            response.getWriter().println(successResponse);
        } else {
            String errorResponse = gson.toJson(awsResponse);
            response.setContentType("application/json");
            response.getWriter().println(errorResponse);
        }


    }


}
