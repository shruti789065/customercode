package com.jakala.menarini.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Component(
    service = {Servlet.class},
    property = {
        "sling.servlet.paths=/bin/api/getUsers",
        "sling.servlet.paths=/bin/api/addUser",
        "sling.servlet.methods={GET,POST}",
        "sling.servlet.extensions=json"
    }
)
public class TestDbConnectionBasic  extends SlingAllMethodsServlet {

    @Reference
    private UserRegisteredServiceInterface userService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        List<RegisteredUserDto> users = userService.getUsers();
        
        Gson gson = new Gson();
        String res = gson.toJson(users);
        response.setContentType("application/json");
        response.getWriter().write(res);       
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        RegisteredUserDto newUser = gson.fromJson(request.getReader(), RegisteredUserDto.class);

        boolean success = userService.addUser(newUser);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        if (success) {
            out.write("{\"status\":\"success\"}");
        } else {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"status\":\"error\"}");
        }
    }
}
