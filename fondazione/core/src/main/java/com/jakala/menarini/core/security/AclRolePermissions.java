package com.jakala.menarini.core.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jakala.menarini.core.dto.RoleDto;

public class AclRolePermissions {

    public static final Map<String, List<Acl>> rolePermissions = new HashMap<>();

    public static final String VIEW_REGISTERED_USERS = "view_registered_users";
    public static final String UPDATE_REGISTERED_USERS = "update_registered_users";
    public static final String ADD_REGISTERED_USERS = "add_registered_users"; //MOVE to public
    public static final String VIEW_ART_AND_SCIENCE = "view_art_and_science";
    public static final String VIEW_VIDEO_AND_SLIDES = "view_video_and_slides";
    public static final String REGISTER_EVENTS = "register_events";
    public static final String VIEW_STREAMING = "view_streaming";
    public static final String SUBSCRIBE_MAGAZINE = "subscribe_magazine";

    static {

        // rolePermissions.put("guest", Arrays.asList(
        //     new Acl(VIEW_BASE),
        //     new Acl(ADD_REGISTERED_USERS)
        // ));

        // utente registrato
        rolePermissions.put("User", Arrays.asList(
            new Acl(VIEW_ART_AND_SCIENCE),
                new Acl(VIEW_REGISTERED_USERS),
                new Acl(UPDATE_REGISTERED_USERS)
        ));

        // biologo, farmacista, studente, etc.
        rolePermissions.put("UserHealthCare", Arrays.asList(
            new Acl(VIEW_REGISTERED_USERS),
                new Acl(UPDATE_REGISTERED_USERS),
            new Acl(VIEW_ART_AND_SCIENCE),
            new Acl(VIEW_VIDEO_AND_SLIDES),
            new Acl(REGISTER_EVENTS),
            new Acl(VIEW_STREAMING)
        ));

        // dottore, infermiere
        rolePermissions.put("UserDoctor", Arrays.asList(
            new Acl(VIEW_REGISTERED_USERS), new Acl(UPDATE_REGISTERED_USERS),
            new Acl(VIEW_ART_AND_SCIENCE),
            new Acl(VIEW_VIDEO_AND_SLIDES),
            new Acl(REGISTER_EVENTS),
            new Acl(VIEW_STREAMING),
            new Acl(VIEW_STREAMING),
            new Acl(SUBSCRIBE_MAGAZINE)
        ));

        rolePermissions.put("Admin", Arrays.asList(
                new Acl(VIEW_REGISTERED_USERS),
                new Acl(UPDATE_REGISTERED_USERS),
                new Acl(VIEW_ART_AND_SCIENCE),
                new Acl(VIEW_VIDEO_AND_SLIDES),
                new Acl(REGISTER_EVENTS),
                new Acl(VIEW_STREAMING),
                new Acl(SUBSCRIBE_MAGAZINE)
        ));

    }

    public static List<Acl> getPermissionsForRole(String role) {
        return rolePermissions.get(role);
    }

    public static Set<Acl> transformRolesToAcl(List<RoleDto> roles) {
        Set<Acl> acls = new HashSet<>();
        for (RoleDto role : roles) {
            List<Acl> permissions = getPermissionsForRole(role.getName());
            if (permissions != null) {
                acls.addAll(permissions);
            }
        }
        return acls;
    }
}
