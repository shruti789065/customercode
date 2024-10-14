package com.jakala.menarini.core.servlets;


import com.google.gson.Gson;
import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RegisteredUserServletDto;
import com.jakala.menarini.core.dto.RegisteredUseServletResponseDto;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.dto.cognitoDto.RefreshDto;
import com.jakala.menarini.core.dto.cognitoDto.SignInResponseDto;
import com.jakala.menarini.core.security.Acl;
import com.jakala.menarini.core.service.interfaces.AwsCognitoServiceInterface;
import com.jakala.menarini.core.service.interfaces.CookieServiceInterface;
import com.jakala.menarini.core.service.interfaces.EncryptDataServiceInterface;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
        private transient UserRegisteredServiceInterface userService;

        @Reference
        private transient CookieServiceInterface cookieService;

        @Reference
        private transient AwsCognitoServiceInterface awsCognitoService;

        @Reference
        private transient EncryptDataServiceInterface encryptDataService;

        private static final String APP_CONTENT = "application/json";


        @Override
        protected void doGet(SlingHttpServletRequest request,  SlingHttpServletResponse response) throws ServletException, IOException {
                Gson gson = new Gson();
                final Set<Acl> acls = getAcls(request);
                RoleDto[] roles = (RoleDto[]) request.getSession().getAttribute("roles");
                String userMail = (String)request.getSession().getAttribute("userEmail");
                try {
                        RegisteredUseServletResponseDto responseDto = new RegisteredUseServletResponseDto();
                        final RegisteredUserDto user = userService.getUserByEmail(userMail, acls, Arrays.asList(roles));
                        if (user == null) {
                             response.setStatus(404);
                             response.setContentType(APP_CONTENT);
                             responseDto.setSuccess(false);
                             responseDto.setErrorMessage("Email not found");
                        } else {
                            response.setStatus(200);
                            response.setContentType(APP_CONTENT);
                            responseDto.setSuccess(true);
                            responseDto.setUpdatedUser(user);
                        }
                        response.getWriter().write(gson.toJson(responseDto));

                }catch (AccessDeniedException  e) {
                        response.setContentType(APP_CONTENT);
                        response.setStatus(403);
                        response.getWriter().println("");
                }catch (SQLException sqlEx) {
                        response.setStatus(500);
                        response.setContentType(APP_CONTENT);
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
                RoleDto[] roles = (RoleDto[]) request.getSession().getAttribute("roles");
                RegisteredUserServletDto updateData = gson.fromJson(request.getReader(), RegisteredUserServletDto.class);
                try {
                        final RegisteredUseServletResponseDto result = userService.updateUserData(
                                userMail,
                                updateData.generateRegisteredUserForUpdate(),
                                updateData.getInterests(),
                                acls,
                                Arrays.asList(roles));
                        if (result.isSuccess()) {
                                if(Boolean.TRUE.equals(result.getIslogOut())) {
                                        Cookie[] cookies = request.getCookies();
                                        ArrayList<String> toDelete = new ArrayList<>();
                                        if (cookies != null) {
                                                for (Cookie cookie : cookies) {
                                                        toDelete.add(cookie.getName());
                                                }
                                        }
                                        cookieService.removeCookie(response,toDelete);
                                        this.relog(cookies,userMail,response);
                                }
                                response.setStatus(200);
                                response.setContentType(APP_CONTENT);
                                response.getWriter().write(gson.toJson(result));
                        } else {
                                response.setStatus(500);
                                response.setContentType(APP_CONTENT);
                                response.getWriter().write(gson.toJson(result));
                        }
                } catch (AccessDeniedException e) {
                        response.setContentType(APP_CONTENT);
                        response.setStatus(403);
                        response.getWriter().println("");
                } catch (SQLException sqlEx) {
                        response.setStatus(500);
                        response.setContentType(APP_CONTENT);
                        RegisteredUseServletResponseDto responseDto = new RegisteredUseServletResponseDto();
                        responseDto.setSuccess(false);
                        responseDto.setErrorMessage(sqlEx.getMessage());
                        response.getWriter().write(gson.toJson(responseDto));
                }
        }

        private void relog(Cookie[] cookies, String email, SlingHttpServletResponse response) {
                for(Cookie cookie : cookies) {
                        if(cookie.getName().equals("p-rToken")) {
                                String token = encryptDataService.decrypt(cookie.getValue());
                                RefreshDto refreshDto = new RefreshDto();
                                refreshDto.setRefreshToken(token);
                                refreshDto.setEmail(email);
                                final SignInResponseDto signInResponseDto =
                                        awsCognitoService.refreshOnCognito(refreshDto);
                                if(signInResponseDto.getCognitoSignInErrorResponseDto() == null) {
                                        HashMap<String,String> mapCookie = new HashMap<>();
                                        mapCookie.put("p-idToken",
                                                encryptDataService.encrypt(signInResponseDto.getCognitoAuthResultDto().getIdToken()));
                                        mapCookie.put("p-aToken",
                                                encryptDataService.encrypt(signInResponseDto.getCognitoAuthResultDto().getAccessToken()));
                                        mapCookie.put("p-rToken",
                                                encryptDataService.encrypt(signInResponseDto.getCognitoAuthResultDto().getRefreshToken()));
                                        cookieService.setCookie(response,mapCookie,true);
                                }
                                break;

                        }

                }
        }
}
