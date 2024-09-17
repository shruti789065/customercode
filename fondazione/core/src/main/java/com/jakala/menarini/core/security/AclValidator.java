package com.jakala.menarini.core.security;

import java.nio.file.AccessDeniedException;
import java.util.Set;

public class AclValidator {
    
    public static void isAccessAllowed(String permission, Set<Acl> acls) throws AccessDeniedException {
        if (acls == null) {
            throw new AccessDeniedException("No ACLs available, access denied.");
        }

        for (Acl acl : acls) {
            if (acl.getPermission().equals(permission)) {
                return;
            }
        }
        throw new AccessDeniedException("Access denied for permission: " + permission);
    }

}