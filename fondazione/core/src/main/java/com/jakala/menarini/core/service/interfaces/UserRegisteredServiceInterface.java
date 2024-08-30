package com.jakala.menarini.core.service.interfaces;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.dto.TopicDto;
import com.jakala.menarini.core.security.Acl;

public interface UserRegisteredServiceInterface {
    
    List<RegisteredUserDto> getUsers(Set<Acl> acls) throws AccessDeniedException;

    boolean addUser(RegisteredUserDto user, Set<Acl> acls) throws AccessDeniedException;

    public boolean addUserForSignUp(RegisteredUserDto user, ArrayList<RoleDto> roles, ArrayList<TopicDto> topics);

    public boolean isActiveUser(String username);
    
}
