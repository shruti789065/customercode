package com.jakala.menarini.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.security.Acl;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;

import jakarta.servlet.http.HttpServletResponse;

import javax.servlet.Servlet;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;

@Component(
    service = {Servlet.class},
    property = {
        "sling.servlet.paths=/private/api/getUsers",
        "sling.servlet.paths=/private/api/addUser",
        "sling.servlet.methods={GET,POST}",
        "sling.servlet.extensions=json"
    }
)
public class TestDbConnectionPrivate extends BaseRestServlet {

    @Reference
    private UserRegisteredServiceInterface userService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Set<Acl> acls = getAcls(request);

        try {
            List<RegisteredUserDto> users = userService.getUsers(acls);

            sendJsonResponse(response, "success", HttpServletResponse.SC_OK, users);
        } catch (AccessDeniedException e) {
            sendJsonResponse(response, "error", HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            sendJsonResponse(response, "error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
   
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Set<Acl> acls = getAcls(request);

        try {
            Gson gson = new Gson();
            RegisteredUserDto newUser = gson.fromJson(request.getReader(), RegisteredUserDto.class);

            boolean success = userService.addUser(newUser, acls);
            if (success) {
                sendJsonResponse(response, "success", HttpServletResponse.SC_OK, null);
            } else {
                sendJsonResponse(response, "error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
            }
        } catch (AccessDeniedException e) {
            sendJsonResponse(response, "error", HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            sendJsonResponse(response, "error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }

}
