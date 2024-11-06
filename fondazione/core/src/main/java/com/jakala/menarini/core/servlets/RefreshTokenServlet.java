package com.jakala.menarini.core.servlets;


import com.google.gson.Gson;
import com.jakala.menarini.core.dto.cognito.RefreshDto;
import com.jakala.menarini.core.dto.cognito.SignInResponseDto;
import com.jakala.menarini.core.service.interfaces.AwsCognitoServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;

@SuppressWarnings("CQRules:CQBP-75")
@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.paths=/bin/api/awsRefreshToken",
                "sling.servlet.methods={POST}",
                "sling.servlet.extensions=json"

        }
)
public class RefreshTokenServlet  extends SlingAllMethodsServlet {

    @Reference
    private transient AwsCognitoServiceInterface awsCognitoService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        RefreshDto refreshDto = gson.fromJson(request.getReader(), RefreshDto.class);
        SignInResponseDto awsResponse = awsCognitoService.refreshOnCognito(refreshDto);
        String stringResponse = gson.toJson(awsResponse);
        if (awsResponse.getCognitoSignInErrorResponseDto() == null) {
            response.setContentType("application/json");
            response.getWriter().println(stringResponse);
        } else {
            response.setContentType("application/json");
            response.setStatus(400);
            response.getWriter().println(stringResponse);
        }


    }


}
