package com.jakala.menarini.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.ContentFragmentException;
import com.jakala.menarini.core.exceptions.RowProcessException;
import com.jakala.menarini.core.service.DataMigrationService;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@SuppressWarnings("CQRules:CQBP-75")
@Component(
    service = {Servlet.class},
    property = {
        "sling.servlet.paths=/bin/api/startMigration",
        "sling.servlet.methods={GET}",
        "sling.servlet.extensions=json"
    }
)
public class DataMigrationServlet extends SlingSafeMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataMigrationServlet.class);

    @Reference
    private transient DataMigrationService migrationService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        
        PrintWriter out = response.getWriter();
        String object = request.getParameter("object");
        String exclusions = request.getParameter("exclusions");
        String delete = request.getParameter("delete");

        try {
            migrationService.migrateData(object, exclusions, delete);
        } catch (InterruptedException ie) {
            // Log the interruption instead of calling interrupt directly
            LOGGER.error("Thread was interrupted", ie);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"status\":\"error\", \"message\": "+ie.getMessage() +"}");
        } catch (LoginException | RepositoryException | IOException | ContentFragmentException | RowProcessException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"status\":\"error\", \"message\": "+e.getMessage() +"}");
        }

        out.write("{\"status\":\"success\"}");
   
    }

}
