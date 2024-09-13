package com.jakala.menarini.core.service.interfaces;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.dto.TopicDto;
import com.jakala.menarini.core.dto.RegisteredUseServletResponseDto;
import com.jakala.menarini.core.security.Acl;

public interface UserRegisteredServiceInterface {

    public RegisteredUserDto getUserByEmail(String email, Set<Acl> acls) throws AccessDeniedException, SQLException;

    public List<RegisteredUserDto> getUsers(Set<Acl> acls) throws AccessDeniedException;

    public boolean addUser(RegisteredUserDto user, Set<Acl> acls) throws AccessDeniedException;

    public boolean addUserForSignUp(RegisteredUserDto user, ArrayList<RoleDto> roles, ArrayList<TopicDto> topics);

    public boolean isActiveUser(String username);

    public RegisteredUseServletResponseDto updateUserData(String email, RegisteredUserDto user, List<String> updateTopics, Set<Acl> acls) throws AccessDeniedException, SQLException;
    
}
