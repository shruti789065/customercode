package com.jakala.menarini.core.service.interfaces;

import com.jakala.menarini.core.dto.RoleDto;

import java.util.List;

public interface RoleServiceInterface {

    List<RoleDto> getRoles();
    public List<RoleDto> getRolesUser(long id);

}
