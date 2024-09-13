package com.jakala.menarini.core.servlets;


import com.google.gson.Gson;
import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RegisteredUserServletDto;
import com.jakala.menarini.core.dto.RegisteredUseServletResponseDto;
import com.jakala.menarini.core.security.Acl;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.Set;

@Component(
    service = {Servlet.class},
    property = {
            "sling.servlet.paths=/private/api/user",
            "sling.servlet.methods={GET,POST}",
            "sling.servlet.extensions=json"
    }
)
public class UserRegisteredServlet extends  BaseRestServlet {

        @Reference
        private UserRegisteredServiceInterface userService;


        @Override
        protected void doGet(SlingHttpServletRequest request,  SlingHttpServletResponse response) throws ServletException, IOException {
                Gson gson = new Gson();
                final Set<Acl> acls = getAcls(request);
                String userMail = (String)request.getSession().getAttribute("userEmail");
                RegisteredUserServletDto getData = gson.fromJson(request.getReader(), RegisteredUserServletDto.class);
                try {
                        RegisteredUseServletResponseDto responseDto = new RegisteredUseServletResponseDto();
                        final RegisteredUserDto user = userService.getUserByEmail(userMail, acls);
                        if (user == null) {
                             response.setStatus(404);
                             response.setContentType("application/json");
                             responseDto.setSuccess(false);
                             responseDto.setErrorMessage("Email not found");
                        } else {
                            response.setStatus(200);
                            response.setContentType("application/json");
                            responseDto.setSuccess(true);
                            responseDto.setUpdatedUser(user);
                        }
                        response.getWriter().write(gson.toJson(responseDto));

                }catch (AccessDeniedException  e) {
                        response.setContentType("application/json");
                        response.setStatus(403);
                        response.getWriter().println("");
                }catch (SQLException sqlEx) {
                        response.setStatus(500);
                        response.setContentType("application/json");
                        RegisteredUseServletResponseDto responseDto = new RegisteredUseServletResponseDto();
                        responseDto.setSuccess(false);
                        responseDto.setErrorMessage(sqlEx.getMessage());
                        response.getWriter().write(gson.toJson(responseDto));
                }
        }

        @Override
        protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
                Gson gson = new Gson();
                final Set<Acl> acls = getAcls(request);
                String userMail = (String)request.getSession().getAttribute("userEmail");
                RegisteredUserServletDto updateData = gson.fromJson(request.getReader(), RegisteredUserServletDto.class);
                try {
                        final RegisteredUseServletResponseDto result = userService.updateUserData(
                                userMail,
                                updateData.generateRegisteredUserForUpdate(),
                                updateData.getInterests(),
                                acls);
                        if (result.isSuccess()) {
                                response.setStatus(200);
                                response.setContentType("application/json");
                                response.getWriter().write(gson.toJson(result));
                        } else {
                                response.setStatus(500);
                                response.setContentType("application/json");
                                response.getWriter().write(gson.toJson(result));
                        }
                } catch (AccessDeniedException e) {
                        response.setContentType("application/json");
                        response.setStatus(403);
                        response.getWriter().println("");
                } catch (SQLException sqlEx) {
                        response.setStatus(500);
                        response.setContentType("application/json");
                        RegisteredUseServletResponseDto responseDto = new RegisteredUseServletResponseDto();
                        responseDto.setSuccess(false);
                        responseDto.setErrorMessage(sqlEx.getMessage());
                        response.getWriter().write(gson.toJson(responseDto));
                }
        }
}
