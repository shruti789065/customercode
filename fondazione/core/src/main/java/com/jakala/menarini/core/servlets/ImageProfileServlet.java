package com.jakala.menarini.core.servlets;


import com.google.gson.Gson;
import com.jakala.menarini.core.dto.awslambda.ImageProfileServiceResponseDto;
import com.jakala.menarini.core.dto.awslambda.LambdaGetFileDto;
import com.jakala.menarini.core.dto.awslambda.LambdaPutFileDto;
import com.jakala.menarini.core.dto.awslambda.PutImageDto;
import com.jakala.menarini.core.service.interfaces.EncryptDataServiceInterface;
import com.jakala.menarini.core.service.interfaces.ImageProfileServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.http.Cookie;
import java.io.IOException;

@SuppressWarnings("CQRules:CQBP-75")
@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.paths=/private/api/profile/image",
                "sling.servlet.methods={GET,POST}",
                "sling.servlet.extensions=json"

        }
)
public class ImageProfileServlet extends SlingAllMethodsServlet {

    @Reference
    private transient ImageProfileServiceInterface imageProfileService;
    @Reference
    private transient EncryptDataServiceInterface encryptDataService;


    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String token = this.getToken(request);
        if(token == null) {
            response.setContentType("application/json");
            response.setStatus(401);
            return;
        }
        String username = this.convertEmailToUsername(request.getSession().getAttribute("userEmail").toString());
        LambdaGetFileDto getFileDto = new LambdaGetFileDto();
        getFileDto.setEmail(username);
        ImageProfileServiceResponseDto responseDto = imageProfileService.getImageProfile(getFileDto,token);
        response.setContentType("application/json");
        if(!responseDto.isSuccess()) {
            response.setStatus(400);
        }
        if(responseDto.getImageData().isBlank() ) {
            response.setStatus(404);
        }
        response.getWriter().write(gson.toJson(responseDto));
    }


    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String token = this.getToken(request);
        if(token == null) {
            response.setContentType("application/json");
            response.setStatus(401);
            return;
        }
        String username = this.convertEmailToUsername(request.getSession().getAttribute("userEmail").toString());
        PutImageDto putImageDto = gson.fromJson(request.getReader(), PutImageDto.class);
        LambdaPutFileDto putFileDto = new LambdaPutFileDto();
        putFileDto.setUsername(username);
        putFileDto.setImageData(putImageDto.getImageData());
        ImageProfileServiceResponseDto responseDto = imageProfileService.saveImageProfile(putFileDto,token);
        response.setContentType("application/json");
        if(!responseDto.isSuccess()) {
            response.setStatus(400);
        }
        response.getWriter().write(gson.toJson(responseDto));
    }

    private String getToken(SlingHttpServletRequest request)  {
        String authString = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("p-idToken".equals(cookie.getName())) {
                    authString = this.encryptDataService.decrypt(cookie.getValue());
                }
            }
        }
        return authString;
    }

    private String convertEmailToUsername(String email) {
        return email.replaceAll("@","_");
    }



}
