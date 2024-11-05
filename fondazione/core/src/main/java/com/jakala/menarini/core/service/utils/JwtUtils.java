package com.jakala.menarini.core.service.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jakala.menarini.core.exceptions.JwtServiceException;
import com.jakala.menarini.core.service.AuthServiceForModels;
import com.jakala.menarini.core.service.interfaces.EncryptDataServiceInterface;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Jwk;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.SignatureException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.oltu.oauth2.jwt.ClaimsSet;
import org.apache.oltu.oauth2.jwt.JWT;
import org.apache.oltu.oauth2.jwt.io.JWTReader;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

public class JwtUtils {

    @Reference
    private EncryptDataServiceInterface encryptDataService;
    private static final String AUTH_COOKIE_NAME = "p-idToken";

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

    public static boolean isValidToken(String token) {

        try {
            JWTReader reader = new JWTReader();
            JWT jwt = reader.read(token);

            ClaimsSet claimsSet = jwt.getClaimsSet();
            Long expirationTime = claimsSet.getExpirationTime();
            if (new Date().getTime() > expirationTime * 1000) {
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

    public static String getToken(HttpServletRequest request, EncryptDataServiceInterface encryptDataService) throws JwtServiceException {
        String authString = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (AUTH_COOKIE_NAME.equals(cookie.getName())) {
                    authString = encryptDataService.decrypt(cookie.getValue());
                }
            }
        }
        if (authString == null) {
            throw new JwtServiceException("Authorization header not found or not in the expected format");
        }
        return authString;
    }

    public static String getTokenJwk(String urlJwk) {
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
