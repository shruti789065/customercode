package com.adiacent.menarini.mhos.core.servlets;


import com.adiacent.menarini.mhos.core.business.ContentFragmentApi;
import com.adiacent.menarini.mhos.core.exception.ImportLibraryException;
import com.adiacent.menarini.mhos.core.models.*;
import com.adiacent.menarini.mhos.core.resources.ImportLibraryResource;
import com.adiacent.menarini.mhos.core.services.LibraryImporter;
import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.dam.print.ids.StringConstants;
import com.day.cq.commons.RangeIterator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.mailer.MailService;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagException;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.repoinit.parser.operations.DeleteServiceUser;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.*;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Cell;

import javax.jcr.*;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;
import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



@Component(service = { Servlet.class }, name = "Menarini House of Science - Import Library Servlet", immediate = true)
@SlingServletResourceTypes(
        resourceTypes = NameConstants.NT_PAGE,
        methods= {HttpConstants.METHOD_GET},
        extensions= "json",
        selectors={ImportLibraryServlet.DEFAULT_SELECTOR})
@ServiceDescription("Menarini House of Science - Import Library Servlet")
public class ImportLibraryServlet extends AbstractJsonServlet {

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String DEFAULT_SELECTOR = "importLibrary";
    protected transient ImportLibraryServlet.Response ilr = null;

    @Reference
    protected transient LibraryImporter importerInstance;

    protected boolean running = false; //TRUE: è in corso l'import dei prodotti
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
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response){


        if(running){
            ilr.setResult("running");
            sendResult(ilr, response);
        }
        else{
            Calendar waitingInterval = endImport != null ? (Calendar) endImport.clone() : Calendar.getInstance();
            waitingInterval.add(Calendar.MINUTE, deltaImport);

            if (endImport != null && endImport.before(Calendar.getInstance()) && Calendar.getInstance().before(waitingInterval)) {
                ilr.setResult( OK_RESULT);
                sendResult(ilr, response);
            }

            else{
                String result = OK_RESULT;
                List<String> errors = new ArrayList<>();
                ilr = new Response();
                running = true;
                try{

                    LibraryImporter clonedImporterInstance = (LibraryImporter) importerInstance.clone();
                    clonedImporterInstance.start();
                    errors = clonedImporterInstance.getErrors();
                    if (clonedImporterInstance.getErrors() != null && !clonedImporterInstance.getErrors().isEmpty()) {
                        result = KO_RESULT;
                    }
                } catch (CloneNotSupportedException e) {
                    logger.error("Library Import Servlet Error:" + e.getMessage(), e);
                    result = KO_RESULT;
                }

                ilr.setResult(result);
                ilr.setErrors(errors);
            }


            running = false;
            endImport = Calendar.getInstance();

            sendResult(ilr, response);
            logger.info("******************FINE Import library servlet ************************");
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
