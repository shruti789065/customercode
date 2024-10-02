package com.jakala.menarini.core.servlets;


import com.google.gson.Gson;
import com.jakala.menarini.core.dto.cognitoDto.ResetPasswordDto;
import com.jakala.menarini.core.dto.cognitoDto.ResetPasswordResponseDto;
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
                "sling.servlet.paths=/private/api/resetPassword",
                "sling.servlet.methods={POST}",
                "sling.servlet.extensions=json"

        }
)
public class ResetPasswordServlet extends SlingAllMethodsServlet {


    @Reference
    private AwsCognitoServiceInterface awsCognitoService;

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        ResetPasswordDto resetPasswordDto = gson.fromJson(request.getReader(), ResetPasswordDto.class);
        ResetPasswordResponseDto awsResponse = awsCognitoService.resetPassword(resetPasswordDto);
        if(!awsResponse.isSuccess()) {
            response.setStatus(400);
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(awsResponse));
    }



}
