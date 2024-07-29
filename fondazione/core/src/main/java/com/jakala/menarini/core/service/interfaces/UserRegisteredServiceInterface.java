package com.jakala.menarini.core.service.interfaces;

import java.util.List;

import com.jakala.menarini.core.dto.RegisteredUserDto;

public interface UserRegisteredServiceInterface {
    
    List<RegisteredUserDto> getUsers();

    boolean addUser(RegisteredUserDto user);
    
}
