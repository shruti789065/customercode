package com.jakala.menarini.core.service;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.exceptions.JwtServiceException;
import com.jakala.menarini.core.service.interfaces.AuthServiceForModelsInterface;
import com.jakala.menarini.core.service.interfaces.EncryptDataServiceInterface;
import com.jakala.menarini.core.service.utils.JwtUtils;

import org.apache.oltu.oauth2.jwt.JWT;
import org.apache.oltu.oauth2.jwt.io.JWTReader;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Component(
        service = AuthServiceForModelsInterface.class
)
public class AuthServiceForModels implements  AuthServiceForModelsInterface {


    @Reference
    private EncryptDataServiceInterface encryptDataService;

    protected static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceForModels.class);

    @Override
    public Map<String, RoleDto[]> extractCredentialsForComponent(HttpServletRequest request) {
        try {
            String token = JwtUtils.getToken(request,this.encryptDataService);
            JWTReader reader = new JWTReader();
            JWT jwt = reader.read(token);

            if (token == null || !JwtUtils.isValidToken(token)) {
                return null;
            }

            String userEmail = jwt.getClaimsSet().getCustomField("email", String.class);
            String userRolesStr = jwt.getClaimsSet().getCustomField("aemRoles", String.class);
            Gson gson = new Gson();
            RoleDto[] roles = gson.fromJson(userRolesStr, RoleDto[].class);
            HashMap<String, RoleDto[]> authData = new HashMap<>();
            authData.put(userEmail, roles);
            return authData;
        } catch (JwtServiceException e) {
            LOGGER.error("Failed to extract credentials", e);
            return null;
        }

    }




}
