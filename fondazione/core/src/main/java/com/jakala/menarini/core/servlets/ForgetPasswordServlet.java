package com.jakala.menarini.core.servlets;


import com.google.gson.Gson;
import com.jakala.menarini.core.dto.cognito.ForgetPasswordDto;
import com.jakala.menarini.core.dto.cognito.ForgetPasswordResponseDto;
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
                "sling.servlet.paths=/bin/api/forgetPassword",
                "sling.servlet.methods={POST}",
                "sling.servlet.extensions=json"

        }
)
public class ForgetPasswordServlet extends SlingAllMethodsServlet {

    @Reference
    private transient AwsCognitoServiceInterface awsCognitoService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        ForgetPasswordDto forgetPasswordDto = gson.fromJson(request.getReader(), ForgetPasswordDto.class);
        ForgetPasswordResponseDto responseData = awsCognitoService.forgetPassword(forgetPasswordDto);
        if(!responseData.isSuccess()) {
            response.setStatus(400);
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(responseData));
    }


}
