package com.jakala.menarini.core.servlets;


import com.google.gson.Gson;
import com.jakala.menarini.core.dto.aswLambdaDto.ImageProfileServiceResponseDto;
import com.jakala.menarini.core.dto.aswLambdaDto.LambdaGetFileDto;
import com.jakala.menarini.core.dto.aswLambdaDto.LambdaPutFileDto;
import com.jakala.menarini.core.dto.aswLambdaDto.PutImageDto;
import com.jakala.menarini.core.service.interfaces.ImageProfileServiceInterface;
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
                "sling.servlet.paths=/private/api/profile/image",
                "sling.servlet.methods={GET,POST}",
                "sling.servlet.extensions=json"

        }
)
public class ImageProfileServlet extends SlingAllMethodsServlet {

    @Reference
    private transient ImageProfileServiceInterface imageProfileService;


    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String token = this.getToken(request);
        String username = this.convertEmailToUsername(request.getSession().getAttribute("userEmail").toString());
        LambdaGetFileDto getFileDto = new LambdaGetFileDto();
        getFileDto.setEmail(username);
        ImageProfileServiceResponseDto responseDto = imageProfileService.getImageProfile(getFileDto,token);
        response.setContentType("application/json");
        if(!responseDto.isSuccess()) {
            response.setStatus(400);
        }
        response.getWriter().write(gson.toJson(responseDto));
    }


    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String token = this.getToken(request);
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

    private String getToken(SlingHttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authHeader.substring(7);
    }

    private String convertEmailToUsername(String email) {
        return email.replaceAll("@","_");
    }



}
