package com.jakala.menarini.core.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jakala.menarini.core.exceptions.JwtServiceException;
import com.jakala.menarini.core.service.interfaces.EncryptDataServiceInterface;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Jwk;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.SignatureException;
import org.apache.sling.auth.core.spi.AuthenticationHandler;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.oltu.oauth2.jwt.ClaimsSet;
import org.apache.oltu.oauth2.jwt.JWT;
import org.apache.oltu.oauth2.jwt.io.JWTReader;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.RoleDto;

@Component(
    service = AuthenticationHandler.class,
    immediate = true,
    property = {
        AuthenticationHandler.PATH_PROPERTY + "=/private/api",
        "service.ranking:Integer=60000", 
        "service.description=Custom Authenticator Handler",
        "service.vendor=Jakala"
    }
    )
    public class JwtAuthenticatorService implements AuthenticationHandler {

        private static final String AUTH_TYPE = "Bearer";
        private static final String AUTH_COOKIE_NAME = "p-idToken";
        private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticatorService.class);

        @Reference
        private EncryptDataServiceInterface encryptDataService;


        protected void activate(Map<String, Object> properties) {
            //
        }

        public Map<String, RoleDto[]> extractCredentialsForComponent(HttpServletRequest request) {
            try {
                String token = this.getToken(request);
                JWTReader reader = new JWTReader();
                JWT jwt = reader.read(token);

                if (token == null || !isValidToken(token)) {
                    return null;
                }

                String userEmail = jwt.getClaimsSet().getCustomField("email", String.class);
                String userRolesStr = jwt.getClaimsSet().getCustomField("aemRoles", String.class);
                Gson gson = new Gson();
                RoleDto[] roles = gson.fromJson(userRolesStr, RoleDto[].class);
                HashMap<String, RoleDto[]> authData = new HashMap<>();
                authData.put(userEmail, roles);
                return authData;
            } catch (Exception e) {
                return null;
            }

        }
    
        @Override
        public AuthenticationInfo extractCredentials(HttpServletRequest request, HttpServletResponse response) {
            try {
                LOGGER.info("Extracting credentials");
                String token = this.getToken(request);
                if (token == null || !isValidToken(token)) {
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
    

            } catch (Exception e) {
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
        
        private String getToken(HttpServletRequest request) throws Exception {
            String authString = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (AUTH_COOKIE_NAME.equals(cookie.getName())) {
                        authString = this.encryptDataService.decrypt(cookie.getValue());
                    }
                }
            }
            if (authString == null) {
                throw new JwtServiceException("Authorization header not found or not in the expected format");
            }
            return authString;
        }
    
        private boolean isValidToken(String token) {

            try {
                JWTReader reader = new JWTReader();
                JWT jwt = reader.read(token);

                ClaimsSet claimsSet = jwt.getClaimsSet();
                Long expirationTime = claimsSet.getExpirationTime();
                if (expirationTime == null || new Date().getTime() > expirationTime * 1000) {
                    LOGGER.error("Token expired");
                    return false; // Il token è scaduto
                }
                String cognitoIss = claimsSet.getIssuer();
                String jwkString = getTokenJwk(cognitoIss+"/.well-known/jwks.json");
                if (jwkString == null) {
                    //jwk not found
                    LOGGER.error("Invalid JWK signature");
                    return false;
                } else {
                    LOGGER.error("JWK signature: {}", jwkString);
                }


                final JsonObject jwkJsonObj = JsonParser.parseString(jwkString).getAsJsonObject();
                String singleKey = jwkJsonObj.get("keys").getAsJsonArray().get(0).toString();

                final Jwk<?> jwk = Jwks.parser().build().parse(singleKey);
                RSAPublicKey rsaKey = (RSAPublicKey)jwk.toKey();

                try {

                    Jwts.parser().verifyWith(rsaKey).build().parse(token);
                } catch (SignatureException e) {
                    LOGGER.error("Invalid JWT signature", e);
                    return false; //wrong signature
                }



                String email = claimsSet.getCustomField("email", String.class);
                if (email == null || email.isEmpty()) {
                    LOGGER.error("Missing email address");
                    return false; // Manca username
                }
                return true;
            } catch (JwtException e) {
                LOGGER.warn("Token validation failed", e);
                return false;
            }
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
        
        private String getTokenJwk(String urlJwk) {
            try(CloseableHttpClient httpClient = HttpClients.createDefault()){
                HttpGet httpGet = new HttpGet(urlJwk);
                try(CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
                    if(httpResponse.getStatusLine().getStatusCode() == 200 ) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                httpResponse.getEntity().getContent()));

                        String inputBuffer;
                        StringBuffer jwkString = new StringBuffer();

                        while ((inputBuffer = reader.readLine()) != null) {
                            jwkString.append(inputBuffer);
                        }
                        return jwkString.toString();
                    }
                    return null;
                } catch(IOException clientEx) {
                    return null;
                }
            }catch (IOException e){
                return null;
            }
        }
    }