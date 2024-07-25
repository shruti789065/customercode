package com.jakala.menarini.core.service.interfaces;

import java.util.List;

import com.jakala.menarini.core.model.RegisteredUser;

public interface UserRegisteredServiceInterface {
    
    List<RegisteredUser> getUsers();

    boolean addUser(RegisteredUser user);

}
