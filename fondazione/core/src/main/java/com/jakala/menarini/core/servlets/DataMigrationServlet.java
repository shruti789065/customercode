package com.jakala.menarini.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.jakala.menarini.core.service.DataMigrationService;
import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;

@Component(
    service = {Servlet.class},
    property = {
        "sling.servlet.paths=/bin/api/startMigration",
        "sling.servlet.methods={GET}",
        "sling.servlet.extensions=json"
    }
)
public class DataMigrationServlet extends SlingSafeMethodsServlet {

    @Reference
    private DataMigrationService migrationService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        
        PrintWriter out = response.getWriter();
        String object = request.getParameter("object");
        try {
            migrationService.migrateData(object);
            out.write("{\"status\":\"success\"}");
        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"status\":\"error\", \"message\": "+e.getMessage() +"}");
            //e.getCause().printStackTrace();
        }
   
    }

}
