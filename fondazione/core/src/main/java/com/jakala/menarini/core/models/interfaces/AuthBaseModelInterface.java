package com.jakala.menarini.core.models.interfaces;

import com.jakala.menarini.core.dto.RegisteredUserDto;

public interface AuthBaseModelInterface {

    public boolean isAuth();
    public void setAuth(boolean auth);
    public RegisteredUserDto getUser();
    public void setUser(RegisteredUserDto user);
    public boolean isMagazineSubscription();
    public void setMagazineSubscription(boolean magazineSubscription);
    public boolean isMaterialAccess();
    public void setMaterialAccess(boolean materialAccess);
    public boolean isEventSubscription();
    public void setEventSubscription(boolean eventSubscription);


}
