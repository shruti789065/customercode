package com.jakala.menarini.core.security;

import org.junit.jupiter.api.Test;
import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class AclValidatorTest {

    @Test
    void testIsAccessAllowedWithValidPermission() {
        Set<Acl> acls = new HashSet<>();
        Acl acl = new Acl("read");
        acls.add(acl);

        assertDoesNotThrow(() -> AclValidator.isAccessAllowed("read", acls));
    }

    @Test
    void testIsAccessAllowedWithInvalidPermission() {
        Set<Acl> acls = new HashSet<>();
        Acl acl = new Acl("read");
        acls.add(acl);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> AclValidator.isAccessAllowed("write", acls));
        assertEquals("Access denied for permission: write", exception.getMessage());
    }

    @Test
    void testIsAccessAllowedWithNullAcls() {
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> AclValidator.isAccessAllowed("read", null));
        assertEquals("No ACLs available, access denied.", exception.getMessage());
    }
}