package com.adiacent.menarini.menarinimaster.core.servlets;

import com.adiacent.menarini.menarinimaster.core.services.RssBlogImporter;
import com.adiacent.menarini.menarinimaster.core.services.RssNewsImporter;
import com.day.cq.wcm.api.NameConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Component(service = { Servlet.class }, name = "Menarini Master template - Import RSS Blog Servlet", immediate = true)
@SlingServletResourceTypes(
        resourceTypes = NameConstants.NT_PAGE,
        methods= {HttpConstants.METHOD_GET},
        extensions= "json",
        selectors={RssBlogImporterServlet.DEFAULT_SELECTOR})
@ServiceDescription("Menarini Master template - Import RSS Blog Servlet")
public class RssBlogImporterServlet extends BaseJsonServlet{
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String DEFAULT_SELECTOR = "rssBlogImporter";
    protected transient RssBlogImporterServlet.Response ilr = null;

    @Reference
    protected transient RssBlogImporter importerInstance;

    protected boolean running = false; //TRUE: è in corso l'import
    private Calendar endImport = null; //data fine i-esima importazione
    private Integer deltaImport = 0; //tempo in minuti. Lasso di tempo che trascorre dopo l'i-esima importazione prima che la servlet possa essere rieseguita.
    //Si sfrutta questo lasso di tempo per comunicare al browser ( client ) invocante che un'importazione è appena terminata e ci sono dati
    // disponibili da mostrare all'utente


    @Activate
    protected void activate() {
        logger.info("*** ACTIVATING {}",this.getClass().getName());
    }
    @Deactivate
    protected void deactivate() {
        logger.info("*** DEACTIVATING {}",this.getClass().getName());
        importerInstance = null;
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {


        if (running) {
            ilr.setResult("running");
            sendResult(ilr, response);
        } else {
            Calendar waitingInterval = endImport != null ? (Calendar) endImport.clone() : Calendar.getInstance();
            waitingInterval.add(Calendar.MINUTE, deltaImport);

            if (endImport != null && endImport.before(Calendar.getInstance()) && Calendar.getInstance().before(waitingInterval)) {
                ilr.setResult(OK_RESULT);
                sendResult(ilr, response);
            } else {
                String result = OK_RESULT;
                List<String> errors = new ArrayList<>();
                ilr = new RssBlogImporterServlet.Response();
                running = true;
                try {

                    RssBlogImporter clonedImporterInstance = (RssBlogImporter) importerInstance.clone();
                    clonedImporterInstance.start();
                    errors = clonedImporterInstance.getErrors();
                    if (clonedImporterInstance.getErrors() != null && !clonedImporterInstance.getErrors().isEmpty()) {
                        result = KO_RESULT;
                    }
                } catch (CloneNotSupportedException e) {
                    logger.error("RSS Blog Import Servlet Error:" + e.getMessage(), e);
                    result = KO_RESULT;
                }

                ilr.setResult(result);
                ilr.setErrors(errors);
            }


            running = false;
            endImport = Calendar.getInstance();

            sendResult(ilr, response);
            logger.info("******************FINE Rss Blog Import servlet ************************");
        }
    }

    public class Response{
        private String result;
        private List<String> errors;

        public Response() {
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public List<String> getErrors() {
            if(errors == null)
                return null;
            String[] array = errors.toArray(new String[errors.size()]);
            String[] clone = array.clone();
            return Arrays.asList(clone);
        }
        public void setErrors(List<String> errors) {
            if(errors== null)
                this.errors = null;
            else {
                String[] array = errors.toArray(new String[errors.size()]);
                String[] clone = array.clone();
                this.errors = Arrays.asList(clone);
            }
        }

    }
}
