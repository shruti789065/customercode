package com.jakala.menarini.core.dto;

public class RegisteredUserPermissionDto {

    private boolean magazineSubscription;
    private boolean materialAccess;
    private boolean eventSubscription;

    public boolean isMagazineSubscription() {
        return magazineSubscription;
    }

    public void setMagazineSubscription(boolean magazineSubscription) {
        this.magazineSubscription = magazineSubscription;
    }

    public boolean isMaterialAccess() {
        return materialAccess;
    }

    public void setMaterialAccess(boolean materialAccess) {
        this.materialAccess = materialAccess;
    }

    public boolean isEventSubscription() {
        return eventSubscription;
    }

    public void setEventSubscription(boolean eventSubscription) {
        this.eventSubscription = eventSubscription;
    }
}
