package com.jakala.menarini.core.servlets;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.cognito.ConfirmForgetPasswordDto;
import com.jakala.menarini.core.dto.cognito.ConfirmForgetPasswordResponseDto;
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
                "sling.servlet.paths=/bin/api/confirmForgetPassword",
                "sling.servlet.methods={POST}",
                "sling.servlet.extensions=json"

        }
)
public class ConfirmForgetPassword extends SlingAllMethodsServlet {

    @Reference
    private transient AwsCognitoServiceInterface awsCognitoService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        ConfirmForgetPasswordDto confirmForgetPasswordDto =
                gson.fromJson(request.getReader(), ConfirmForgetPasswordDto.class);
        ConfirmForgetPasswordResponseDto responseData =
                awsCognitoService.confirmForgetPassword(confirmForgetPasswordDto);
        if(!responseData.isSuccess()){
            response.setStatus(400);
            response.setContentType("application/json");
            response.getWriter().println(gson.toJson(responseData));
        }
    }

}
