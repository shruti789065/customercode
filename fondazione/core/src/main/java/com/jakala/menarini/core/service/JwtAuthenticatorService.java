package com.jakala.menarini.core.service;

import java.io.IOException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonSyntaxException;
import com.jakala.menarini.core.exceptions.JwtServiceException;
import com.jakala.menarini.core.service.interfaces.EncryptDataServiceInterface;
import com.jakala.menarini.core.service.utils.JwtUtils;
import io.jsonwebtoken.JwtException;

import org.apache.sling.auth.core.spi.AuthenticationHandler;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.oltu.oauth2.jwt.JWT;
import org.apache.oltu.oauth2.jwt.io.JWTReader;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.RoleDto;

@Component(
    service = AuthenticationHandler.class,
    property = {
        AuthenticationHandler.PATH_PROPERTY + "=/private/api",
        "service.ranking:Integer=60000", 
        "service.description=Custom Authenticator Handler",
        "service.vendor=Jakala"
    }
    )
    public class JwtAuthenticatorService implements AuthenticationHandler {

        private static final String AUTH_TYPE = "Bearer";
        private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticatorService.class);

        @Reference
        private EncryptDataServiceInterface encryptDataService;


        protected void activate(Map<String, Object> properties) {
            //
        }


    
        @Override
        public AuthenticationInfo extractCredentials(HttpServletRequest request, HttpServletResponse response) {
            try {
                LOGGER.info("Extracting credentials");
                String token = JwtUtils.getToken(request,encryptDataService);
                if (token == null || !JwtUtils.isValidToken(token)) {
                    LOGGER.warn("Invalid or missing token");
                    sendUnauthorizedResponse(response);
                    return AuthenticationInfo.DOING_AUTH;
                } 
                
                JWTReader reader = new JWTReader();
                JWT jwt = reader.read(token);
                
                String userEmail = jwt.getClaimsSet().getCustomField("email", String.class);
                String userRolesStr = jwt.getClaimsSet().getCustomField("aemRoles", String.class);
                
                LOGGER.info("User email: {}", userEmail);
                LOGGER.info("User roles string: {}", userRolesStr);
                
                Gson gson = new Gson();
                RoleDto[] roles = gson.fromJson(userRolesStr, RoleDto[].class);
                
                AuthenticationInfo authData = new AuthenticationInfo(AUTH_TYPE, userEmail);
                for (RoleDto roleDto : roles) {
                    authData.put("role_" + roleDto.getId(), roleDto.getName());
                }

                request.getSession().setAttribute("userEmail", userEmail);
                request.getSession().setAttribute("roles", roles);

                return authData;
    

            } catch (JwtException | JwtServiceException | IOException | JsonSyntaxException e) {
                LOGGER.error("Failed to extract credentials", e);
                try {
                    sendUnauthorizedResponse(response);
                } catch (IOException e1) {
                    LOGGER.error("Failed to send unauthorized response", e);
                }
            } 
            return AuthenticationInfo.DOING_AUTH;
        }

        private void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
            response.setHeader("WWW-Authenticate", AUTH_TYPE);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing token");
        }



        @Override
        public void dropCredentials(HttpServletRequest request, HttpServletResponse response) throws IOException {
            request.getSession().removeAttribute("userEmail");
            request.getSession().removeAttribute("roles");
        }
    
        @Override
        public boolean requestCredentials(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.setHeader("WWW-Authenticate", AUTH_TYPE);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

    }