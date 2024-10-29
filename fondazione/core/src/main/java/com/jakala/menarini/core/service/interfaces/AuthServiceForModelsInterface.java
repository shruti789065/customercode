package com.jakala.menarini.core.service.interfaces;

import com.jakala.menarini.core.dto.RoleDto;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface AuthServiceForModelsInterface {

    public Map<String, RoleDto[]> extractCredentialsForComponent(HttpServletRequest request);

}
