package com.jakala.menarini.core.security;

public class Acl {
    private String permission;

    public Acl(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
