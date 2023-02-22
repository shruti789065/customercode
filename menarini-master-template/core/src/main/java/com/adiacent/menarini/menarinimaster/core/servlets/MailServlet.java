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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.adiacent.menarini.menarinimaster.core.utils.Constants;
import com.day.cq.wcm.api.NameConstants;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
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

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected static final String EXTENSION = "html";

    /** @scr.property name="sling.servlet.selectors" */
    protected static final String SELECTOR = "customMail";

    protected static final String MAILTO_PROPERTY = "mailto";

    protected static final String CC_PROPERTY = "cc";

    protected static final String BCC_PROPERTY = "bcc";

    protected static final String SUBJECT_PROPERTY = "subject";

    protected static final String FROM_PROPERTY = "from";

    /** @scr.reference policy="dynamic" cardinality="0..1" */
    protected MailService mailService;

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
        final MailService localService = this.mailService;
        if (ResourceUtil.isNonExistingResource(request.getResource())) {
            logger.debug("Received fake request!");
            response.setStatus(500);
            return;
        }
        final ResourceBundle resBundle = request.getResourceBundle(null);

        final ValueMap values = ResourceUtil.getValueMap(request.getResource());
        final String[] mailTo = values.get(MAILTO_PROPERTY, String[].class);
        int status = 200;
        if (mailTo == null || mailTo.length == 0 || mailTo[0].length() == 0) {
            // this is a sanity check
            logger.error(
                    "The mailto configuration is missing in the form begin at " + request.getResource().getPath());

            status = 500;
        } else if (localService == null) {
            logger.error("The mail service is currently not available! Unable to send form mail.");

            status = 500;
        } else {
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
                String text = resBundle.getString("You've received a new form based mail from {0}.");
                text = text.replace("{0}", builder.toString());
                buffer.append(text);
                buffer.append("\n\n");
                buffer.append(resBundle.getString("Values"));
                buffer.append(":\n\n");
                // we sort the names first - we use the order of the form field and
                // append all others at the end (for compatibility)

                // let's get all parameters first and sort them alphabetically!
                final List<String> contentNamesList = new ArrayList<String>();
                final Iterator<String> names = FormsHelper.getContentRequestParameterNames(request);
                while (names.hasNext()) {
                    final String name = names.next();
                    contentNamesList.add(name);
                }
                Collections.sort(contentNamesList);

                final List<String> namesList = new ArrayList<String>();
                final Iterator<Resource> fields = FormsHelper.getFormElements(request.getResource());
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

                // now add form fields to message
                // and uploads as attachments
                final List<RequestParameter> attachments = new ArrayList<RequestParameter>();
                for (final String name : namesList) {
                    final RequestParameter rp = request.getRequestParameter(name);
                    if (rp == null) {
                        //see Bug https://bugs.day.com/bugzilla/show_bug.cgi?id=35744
                        logger.debug("skipping form element {} from mail content because it's not in the request",
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
                final String[] ccRecs = values.get(CC_PROPERTY, String[].class);
                if (ccRecs != null) {
                    for (final String rec : ccRecs) {
                        email.addCc(rec);
                    }
                }
                // bcc
                final String[] bccRecs = values.get(BCC_PROPERTY, String[].class);
                if (bccRecs != null) {
                    for (final String rec : bccRecs) {
                        email.addBcc(rec);
                    }
                }

                // subject and from address
                final String subject = values.get(SUBJECT_PROPERTY, resBundle.getString("Form Mail"));
                email.setSubject(subject);
                final String fromAddress = values.get(FROM_PROPERTY, "");
                if (fromAddress.length() > 0) {
                    email.setFrom(fromAddress);
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Sending form activated mail: fromAddress={}, to={}, subject={}, text={}.",
                            new Object[] { fromAddress, mailTo, subject, buffer });
                }
                localService.sendEmail(email);

            } catch (EmailException e) {
                logger.error("Error sending email: " + e.getMessage(), e);
                status = 500;
            }
        }
        // check for redirect
        String redirectTo = request.getParameter(":redirect");
        if (redirectTo != null) {
            int pos = redirectTo.indexOf('?');
            redirectTo = redirectTo + (pos == -1 ? '?' : '&') + "status=" + status;
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

}