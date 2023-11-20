package com.adiacent.menarini.mhos.core.servlets;
import com.day.cq.mailer.MailService;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.PrintWriter;


public abstract class AbstractJsonServlet extends SlingAllMethodsServlet {

    public static final String OK_RESULT = "OK";
    public static final String KO_RESULT = "KO";

    private static final Logger logger = LoggerFactory.getLogger(AbstractJsonServlet.class);

    private transient Gson gson = null;


    /**
     * Flush the response of the servlet, a JSON representation of the
     * resultObject passed.
     *
     * @param resultObject Result object to return as response.
     * @param response Servlet response
     */
    protected void sendResult(Object resultObject, SlingHttpServletResponse response) {
        sendResult(resultObject, response, null);
    }

    protected void sendResult(Object resultObject, SlingHttpServletResponse response, String errorMessage) {
        try {
            initGson();
            String result = gson.toJson(resultObject);

            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(result);
            out.flush();


        } catch (IOException e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error("Error on sending servlet JSON result from " + this.getClass().getName(), e);
        }
    }



    private void initGson() {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();
            gson = builder.create();
        }
    }

}
