/*
 * Copyright 1997-2009 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 */
package com.adiacent.menarini.menarinimaster.core.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;


import com.adiacent.menarini.menarinimaster.core.models.RecaptchaValidationResponse;
import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;

import com.day.cq.search.QueryBuilder;

import com.day.cq.wcm.api.NameConstants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MailService;
import com.day.cq.wcm.foundation.forms.FieldDescription;
import com.day.cq.wcm.foundation.forms.FieldHelper;
import com.day.cq.wcm.foundation.forms.FormsHelper;

/**
 * This mail servlet accepts POSTs to a form begin paragraph
 * but only if the selector "mail" and the extension "html" is used.
 *
 * @scr.component metatype="false"
 * @scr.service interface="javax.servlet.Servlet"
 *
 * @scr.property name="sling.servlet.resourceTypes" value="foundation/components/form/start"
 * @scr.property name="sling.servlet.methods" value="POST"
 *
 * @scr.property name="service.description" value="Form Mail Service"
 */

@Component(service = { Servlet.class },  immediate = true)
@SlingServletResourceTypes(
        resourceTypes = NameConstants.NT_PAGE,
        methods= {HttpConstants.METHOD_POST},
        extensions= {MailServlet.EXTENSION},
        selectors={MailServlet.SELECTOR})
public class MailServlet extends SlingAllMethodsServlet implements OptingServlet {

    private static final long serialVersionUID = 1L;
    private static final transient Logger logger = LoggerFactory.getLogger(DropdownServlet.class);


    protected static final String EXTENSION = "html";

    /** @scr.property name="sling.servlet.selectors" */
    protected static final String SELECTOR = "customMail";

    protected static final String MAILTO_PROPERTY = "mailto";

    protected static final String OPTMAILTO_PROPERTY = "optMailTo";

    protected static final String CC_PROPERTY = "cc";

    protected static final String BCC_PROPERTY = "bcc";

    protected static final String SUBJECT_PROPERTY = "subject";

    protected static final String FROM_PROPERTY = "from";

    protected static final String CLIENTTEXT_PROPERTY = "mailClientText";

    protected static final String ADMINTEXT_PROPERTY = "mailAdminText";

    protected static final String MAIL_PROPERTY = "email";

    private String[] fileExtensionAllowed = {"doc","docx","odt","sxw","pdf"}; //estensioni file allegato consentite

    private static final Integer MAX_FILE_SIZE_MB = new Integer(3);//in Mbyte
    private static final Integer MAX_FILE_SIZE = new Integer(MAX_FILE_SIZE_MB *1024*1024);//in byte //max dimensione file allegato

    private List<String> paramsToExclude = Arrays.asList("g-recaptcha-response");
    public static final String ERROR_MESSAGE_ATTRIBUTE_NAME = "contactFormErrorAttr"; //Nome attributo in sessione contenente eventuali messaggi di errore
    //inerenti il fallito invio del form contatti

    /** @scr.reference policy="dynamic" cardinality="0..1" */
    @Reference
    private transient MailService mailService;

    @Reference
    private transient QueryBuilder qBuilder;



    /**
     * @see org.apache.sling.api.servlets.OptingServlet#accepts(org.apache.sling.api.SlingHttpServletRequest)
     */
    public boolean accepts(SlingHttpServletRequest request) {
        return EXTENSION.equals(request.getRequestPathInfo().getExtension());
    }

    /**
     * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
     */
    public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        this.doPost(request, response);
    }

    /**
     * @see org.apache.sling.api.servlets.SlingAllMethodsServlet#doPost(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
     */
    public void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        if (ResourceUtil.isNonExistingResource(request.getResource())) {
            logger.debug("Received fake request!");
            response.setStatus(500);
            return;
        }
        if (this.mailService == null) {
            logger.error("The mail service is currently not available! Unable to send form mail.");
            response.setStatus(500);
            return;
        }
        //Clean attributo in sessione
        request.getSession().removeAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME);

        final ResourceBundle resBundle = request.getResourceBundle(null);
        ResourceResolver resourceResolver = request.getResource().getResourceResolver();

        String[] mailTo = null;
        String[] ccRecs = null;
        String[] bccRecs = null;
        String subject = null;
        String fromAddress = null;
        String clientText = null;
        String adminText = null;
        List<String> errors = new ArrayList<String>();
        /**
         * Adapting the resource resolver to the session object
         */
        Session session = resourceResolver.adaptTo(Session.class);


        //recupero risorsa di provenienza contenente il componente recaptcha per il recupero della secret key
        final String formStart = request.getParameter( "resourcePath");
        if(StringUtils.isBlank(formStart)){
            this.logger.debug("It is no possible to verify recaptcha");
            errors.add("It is no possible to verify recaptcha");
        }
        else{
            //recupero la risorsa recaptcha

            Resource parent =  getParentResource(resourceResolver, formStart);
            if(parent != null){
                Resource recaptchaCmp = parent.getChild("recaptcha");
                if(recaptchaCmp != null && recaptchaCmp.getValueMap().get("secretKey") != null){
                    String secretKey = (String)recaptchaCmp.getValueMap().get("secretKey");
                    //check recaptcha
                    final String recaptchaToken = request.getParameter( "g-recaptcha-response");
                    if(!checkRecaptcha(recaptchaToken, secretKey)){
                        this.logger.debug("Recaptcha verification failed");
                        errors.add("Recaptcha verification failed");
                    }
                }
            }

        }



        /**
         * Configuring the Map for the predicate
         */
        Map<String, String> predicate = new HashMap<>();
        predicate.put("path", request.getResource().getPath()+"/jcr:content");
        predicate.put("type", "nt:unstructured");
        predicate.put("property", MAILTO_PROPERTY);
        predicate.put("property.operation", "exists");
        Resource resourceContainer = ModelUtils.findResourceByPredicate(qBuilder,predicate,session,resourceResolver);
        if(resourceContainer != null){
            final ValueMap values = ResourceUtil.getValueMap(resourceContainer);
            mailTo = values.get(MAILTO_PROPERTY, String[].class);//destinatario admin di default settato a livello di container
            ccRecs = values.get(CC_PROPERTY, String[].class);
            bccRecs = values.get(BCC_PROPERTY, String[].class);
            subject = values.get(SUBJECT_PROPERTY, resBundle.getString("Form Mail"));
            fromAddress = values.get(FROM_PROPERTY, ""); //mittente quando email è per il cliente
            clientText = values.get(CLIENTTEXT_PROPERTY, "");
            adminText = values.get(ADMINTEXT_PROPERTY, "");
        }

        final String informationValue = request.getParameter("information"); // nome campo del form contenente il drop down delle opzioni
        String optMailTo = null;
        if(StringUtils.isNotBlank(informationValue)){
            //si cerca il nodo child di container il cui nome è information
            predicate = new HashMap<>();
            predicate.put("path", resourceContainer.getPath());
            predicate.put("type", "nt:unstructured");
            predicate.put("property", "name");
            predicate.put("property.value", "information");

            Resource optResource = ModelUtils.findResourceByPredicate(qBuilder, predicate, session, resourceResolver);
            if(optResource != null){
                predicate = new HashMap<>();
                predicate.put("path", optResource.getPath());
                predicate.put("type", "nt:unstructured");
                predicate.put("property", "value");
                predicate.put("property.value", informationValue);

                Resource optItemResource =  ModelUtils.findResourceByPredicate(qBuilder, predicate, session, resourceResolver);
                ValueMap property = optItemResource.adaptTo(ValueMap.class);
                optMailTo = property.get("optMailTo", String.class);
            }
            if(session!= null){
                session.logout();
            }
        }


        final String emailValue = request.getParameter(MAIL_PROPERTY); //nome campo del form contenente l'indirizzo email del cliente.E' sia il destinatario della mail per il cliente
        //nonché il mittente della mail per l'admin


        // we sort the names first - we use the order of the form field and
        // append all others at the end (for compatibility)

        // let's get all parameters first and sort them alphabetically!
        final List<String> contentNamesList = new ArrayList<String>();

        final Iterator<String> names = getRequestParamIterators(request);
        while (names.hasNext()) {
            final String name = names.next();
            contentNamesList.add(name);
        }
        Collections.sort(contentNamesList);

        final List<String> namesList = new ArrayList<String>();
        final Iterator<Resource> fields = getResourceFormElements(request);
        while (fields.hasNext()) {
            final Resource field = fields.next();
            final FieldDescription[] descs = FieldHelper.getFieldDescriptions(request, field);
            for (final FieldDescription desc : descs) {
                // remove from content names list
                contentNamesList.remove(desc.getName());
                if (!desc.isPrivate()) {
                    namesList.add(desc.getName());
                }
            }
        }
        namesList.addAll(contentNamesList);

        if(StringUtils.isBlank(fromAddress)){
            this.logger.debug("No sender specified for client email");
            errors.add("It's not possible to send the email.\nPlease, contact the help desk ");
        }
        if(   StringUtils.isBlank(optMailTo) && ( mailTo == null || mailTo.length == 0 ) )  {
            this.logger.debug("No receiver specified for admin email");
            errors.add("It's not possible to send the email.\nPlease, contact the help desk ");
        }

        //controllo errori in file uploaded
        final List<RequestParameter> attachments = new ArrayList<RequestParameter>();
        for (final String name : namesList) {
            final RequestParameter rp = request.getRequestParameter(name);
            if (rp != null &&  !rp.isFormField() && rp.getSize() > 0) {
                if(!isExtensionValid(rp.getFileName())){
                    this.logger.debug("File extension for " + rp.getFileName() +" not allowed. File allowed {0}", String.join(",", fileExtensionAllowed));
                    errors.add("File extension for " + rp.getFileName() +" not allowed. File allowed " + String.join(",", fileExtensionAllowed));
                }
                //int size = ea.getInputStream().available();
                if(rp.getSize() > MAX_FILE_SIZE.intValue()) {
                    this.logger.debug("File size for " + rp.getFileName() +" not allowed. Max size {0} MB", new Object[] { MAX_FILE_SIZE_MB });
                    errors.add("File size for " + rp.getFileName() +" not allowed. Max size MAX_FILE_SIZE_MB MB");
                }

            }
        }
        int status = 200;
        if(errors == null || errors.size() == 0) {

            //email per cliente
            status = sendEmail(request, clientText, new String[]{emailValue}, fromAddress, null, null, subject, namesList, resBundle);
            //email per admin
            sendEmail(request, adminText, StringUtils.isNotBlank(optMailTo) ? new String[]{optMailTo} : mailTo, emailValue, ccRecs, bccRecs, subject, namesList, resBundle);
        }


        // check for redirect
        String redirectTo = request.getParameter(":redirect");
        if (redirectTo != null) {
            int pos = redirectTo.indexOf('?');
            redirectTo = redirectTo + (pos == -1 ? '?' : '&') + "status=" + status;
            request.getSession().setAttribute(ERROR_MESSAGE_ATTRIBUTE_NAME, errors);
            response.sendRedirect(redirectTo);
            return;
        }
        if (FormsHelper.isRedirectToReferrer(request)) {
            FormsHelper.redirectToReferrer(request, response,
                    Collections.singletonMap("stats", new String[] { String.valueOf(status) }));
            return;
        }
        response.setStatus(status);
    }

    public Resource getParentResource(ResourceResolver resourceResolver, String formStart) {
        return resourceResolver.getResource(formStart);
    }


    private int sendEmail(SlingHttpServletRequest request, String mailText, String[] mailTo, String fromAddress, String[] ccRecs, String[] bccRecs, String subject, List<String> namesList, ResourceBundle resBundle ){
        int status = 200;
        try {
            final StringBuilder builder = new StringBuilder();
            builder.append(request.getScheme());
            builder.append("://");
            builder.append(request.getServerName());
            if ((request.getScheme().equals("https") && request.getServerPort() != 443)
                    || (request.getScheme().equals("http") && request.getServerPort() != 80)) {
                builder.append(':');
                builder.append(request.getServerPort());
            }
            builder.append(request.getRequestURI());

            // construct msg
            final StringBuilder buffer = new StringBuilder();
            String text = resBundle.getString("You've received a new form based mail from {0}.");//testo editoriale
            text = text.replace("{0}", builder.toString());
            buffer.append(text);
            buffer.append("\n\n");
            if(StringUtils.isNotBlank(mailText)) {
                buffer.append(mailText);
                buffer.append("\n\n");
            }
            buffer.append(resBundle.getString("Values"));
            buffer.append(":\n\n");


            // now add form fields to message
            // and uploads as attachments
            final List<RequestParameter> attachments = new ArrayList<RequestParameter>();
            for (final String name : namesList) {
                final RequestParameter rp = request.getRequestParameter(name);
                if (rp == null || paramsToExclude.contains(name)) {
                    //see Bug https://bugs.day.com/bugzilla/show_bug.cgi?id=35744
                    logger.debug("skipping form element {} from mail content because it's not in the request or is not allowed in the mail body",
                            name);
                } else if (rp.isFormField()) {
                    buffer.append(name);
                    buffer.append(" : \n");
                    final String[] pValues = request.getParameterValues(name);
                    for (final String v : pValues) {
                        buffer.append(v);
                        buffer.append("\n");
                    }
                    buffer.append("\n");
                } else if (rp.getSize() > 0) {
                    attachments.add(rp);
                } else {
                    //ignore
                }
            }
            // if we have attachments we send a multi part, otherwise a simple email
            final Email email;
            if (attachments.size() > 0) {
                buffer.append("\n");
                buffer.append(resBundle.getString("Attachments"));
                buffer.append(":\n");
                final MultiPartEmail mpEmail = new MultiPartEmail();
                email = mpEmail;
                for (final RequestParameter rp : attachments) {
                    final ByteArrayDataSource ea = new ByteArrayDataSource(rp.getInputStream(),
                            rp.getContentType());

                    mpEmail.attach(ea, rp.getFileName(), rp.getFileName());

                    buffer.append("- ");
                    buffer.append(rp.getFileName());
                    buffer.append("\n");
                }
            } else {
                email = new SimpleEmail();
            }

            email.setMsg(buffer.toString());

            // mailto
            for (final String rec : mailTo) {
                email.addTo(rec);
            }
            // cc
            if (ccRecs != null) {
                for (final String rec : ccRecs) {
                    email.addCc(rec);
                }
            }
            // bcc
            if (bccRecs != null) {
                for (final String rec : bccRecs) {
                    email.addBcc(rec);
                }
            }
            // subject and from address
            email.setSubject(subject);
            if (fromAddress.length() > 0) {
                email.setFrom(fromAddress);
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Sending form activated mail: fromAddress={}, to={}, subject={}, text={}.",
                        new Object[] { fromAddress, mailTo, subject, buffer });
            }

            this.mailService.sendEmail(email);

        } catch (EmailException | IOException e) {
            logger.error("Error sending email: " + e.getMessage(), e);
            status = 500;
        }
        return status;
    }
    public boolean isExtensionValid(String filename) {

        String fileExt = FilenameUtils.getExtension(filename);
        return Arrays.stream(fileExtensionAllowed).anyMatch(fileExt::equalsIgnoreCase);
    }

    public Iterator getRequestParamIterators(SlingHttpServletRequest request){
        return FormsHelper.getContentRequestParameterNames(request);
    }

    public Iterator<Resource> getResourceFormElements(SlingHttpServletRequest request) {
        return FormsHelper.getFormElements(request.getResource());
    }

    public CloseableHttpClient getHttpClient(){
        return HttpClients.createDefault();
    }
    public boolean checkRecaptcha(String responseToken, String secretKey){
        if(StringUtils.isBlank(responseToken) || StringUtils.isBlank(secretKey))
            return false;
        //CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpClient  httpclient = getHttpClient();
        try {
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost("www.google.com")
                    .setPath("/recaptcha/api/siteverify")
                    .setParameter("secret", secretKey)
                    .setParameter("response", responseToken)

                    .build();

            HttpPost httpPost = new HttpPost(uri);
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                try (InputStream instream = entity.getContent()) {
                    String resp = new String(instream.readAllBytes(), StandardCharsets.UTF_8);
                    //Gson gson =  new GsonBuilder().create();
                    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) ->

                            LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_DATE_TIME)
                    ).create();
                    RecaptchaValidationResponse result = gson.fromJson(resp, RecaptchaValidationResponse.class);
                    return (result != null && result.isSuccess());

                }
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //HttpPost httpPost = new HttpPost("https://www.google.com/recaptcha/api/siteverify");
        return false;
    }

}