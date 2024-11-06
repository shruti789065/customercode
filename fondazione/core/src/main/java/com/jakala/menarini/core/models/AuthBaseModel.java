package com.jakala.menarini.core.models;



import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RegisteredUserPermissionDto;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.models.interfaces.AuthBaseModelInterface;
import com.jakala.menarini.core.models.utils.RequestDataConstants;
import com.jakala.menarini.core.security.Acl;
import com.jakala.menarini.core.security.AclRolePermissions;
import com.jakala.menarini.core.service.interfaces.AuthServiceForModelsInterface;
import com.jakala.menarini.core.service.interfaces.UserRegisteredServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.*;

@Model(
        adaptables = {
                SlingHttpServletRequest.class
        },
        adapters = AuthBaseModelInterface.class
)
public class AuthBaseModel implements AuthBaseModelInterface {

    @OSGiService
    private AuthServiceForModelsInterface jwt;

    @OSGiService
    private UserRegisteredServiceInterface userService;


    @Self
    protected SlingHttpServletRequest request;

    private boolean isAuth;

    private boolean magazineSubscription;

    private boolean materialAccess;

    private boolean eventSubscription;

    private RegisteredUserDto user;

    protected RoleDto[] rolesData;


    @PostConstruct
    protected void init()  {
      HashMap<String,RoleDto[]> authData = (HashMap<String, RoleDto[]>) jwt.extractCredentialsForComponent(request);
      if(authData != null) {
          final Optional<String> email = authData.keySet().stream().findFirst();
          if(email.isPresent()) {
              RoleDto[] roles = authData.get(email.get());
              this.rolesData = roles;
              final Set<Acl> acls = AclRolePermissions.transformRolesToAcl(Arrays.asList(roles));
              try {
                  RegisteredUserDto userData = userService.getUserByEmail(email.get(),acls,Arrays.asList(roles));
                  this.isAuth = true;
                  this.user = userData;
                  RegisteredUserPermissionDto permissions =
                          userService.generateUserPermission(roles[0],this.user.getCountry());
                  this.eventSubscription = permissions.isEventSubscription();
                  this.magazineSubscription = permissions.isMagazineSubscription();
                  this.materialAccess = permissions.isMaterialAccess();
                  request.setAttribute(RequestDataConstants.KEY_USER,this.user);
              } catch (AccessDeniedException | SQLException e) {
                  this.isAuth = false;
                  request.setAttribute(RequestDataConstants.KEY_IS_AUTH,this.isAuth);
              }
          }
      }
      request.setAttribute(RequestDataConstants.KEY_IS_AUTH,this.isAuth);

    }

    @Override
    public boolean isAuth() {
        return isAuth;
    }

    @Override
    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    @Override
    public RegisteredUserDto getUser() {
        return user;
    }

    @Override
    public void setUser(RegisteredUserDto user) {
        this.user = user;
    }

    @Override
    public boolean isMagazineSubscription() {
        return magazineSubscription;
    }

    @Override
    public void setMagazineSubscription(boolean magazineSubscription) {
        this.magazineSubscription = magazineSubscription;
    }

    @Override
    public boolean isMaterialAccess() {
        return materialAccess;
    }

    @Override
    public void setMaterialAccess(boolean materialAccess) {
        this.materialAccess = materialAccess;
    }

    @Override
    public boolean isEventSubscription() {
        return eventSubscription;
    }

    @Override
    public void setEventSubscription(boolean eventSubscription) {
        this.eventSubscription = eventSubscription;
    }
}
