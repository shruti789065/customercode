package com.jakala.menarini.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import com.google.gson.Gson;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.security.Acl;
import com.jakala.menarini.core.security.AclRolePermissions;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;

public abstract class BaseRestServlet extends SlingAllMethodsServlet {

    protected Set<Acl> getAcls(SlingHttpServletRequest request) {
        RoleDto[] roles = (RoleDto[]) request.getSession().getAttribute("roles");
        return roles != null ? AclRolePermissions.transformRolesToAcl(Arrays.asList(roles)) : null;
    }

    protected void sendJsonResponse(SlingHttpServletResponse response, String status, int statusCode, Object data) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        StringBuilder jsonResponse = new StringBuilder("{\"status\":\"").append(status).append("\"");
        if (data != null) {
            Gson gson = new Gson();
            String jsonData = gson.toJson(data);
            jsonResponse.append(", \"data\":").append(jsonData);
        }
        jsonResponse.append("}");
        out.write(jsonResponse.toString());
    }
}
