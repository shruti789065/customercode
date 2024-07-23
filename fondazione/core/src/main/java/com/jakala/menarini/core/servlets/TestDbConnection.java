package com.jakala.menarini.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.UserDto;
import com.jakala.menarini.core.entities.RegisteredUser;
import com.jakala.menarini.core.service.interfaces.MysqlConnectionInterface;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component(
    service = {Servlet.class},
    property = {
        "sling.servlet.paths=/bin/getUsers",
        "sling.servlet.methods=get",
        "sling.servlet.extensions=json"
    }
)
public class TestDbConnection  extends SlingAllMethodsServlet {

    @Reference
    private UserRegisteredServiceInterface userService;


    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        /*boolean isAccess = userService.isAccessible();
        boolean isAcc = false;
        if(isAccess) {
        isAcc = userService.isAccessible();
        }*/
        List<RegisteredUser> users = userService.getUsers();
        
        Gson gson = new Gson();
        String res = gson.toJson(users);
        response.setContentType("application/json");
        response.getWriter().write(res);
    
       
    }
}
