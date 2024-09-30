package com.adiacent.menarini.relife.core.servlets;

import com.adiacent.menarini.relife.core.models.RecaptchaValidationResponse;
import com.adiacent.menarini.relife.core.utils.ModelUtils;
import com.day.cq.mailer.MailService;
import com.day.cq.search.QueryBuilder;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.foundation.forms.FieldDescription;
import com.day.cq.wcm.foundation.forms.FieldHelper;
import com.day.cq.wcm.foundation.forms.FormsHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This mail servlet accepts POSTs to a form begin paragraph
 * but only if the selector "mail" and the extension "html" is used.
 *
 * @scr.component metatype="false"
 * @scr.service interface="javax.servlet.Servlet"
 * @scr.property name="sling.servlet.resourceTypes" value="foundation/components/form/start"
 * @scr.property name="sling.servlet.methods" value="POST"
 * @scr.property name="service.description" value="Form Mail Service"
 */

@Component(service = {Servlet.class}, immediate = true)
@SlingServletResourceTypes(
		resourceTypes = NameConstants.NT_PAGE,
		methods = {HttpConstants.METHOD_POST},
		extensions = {MailServlet.EXTENSION},
		selectors = {MailServlet.SELECTOR})
public class MailServlet extends SlingAllMethodsServlet implements OptingServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger;

	static {
		logger = LoggerFactory.getLogger(MailServlet.class);
	}


	protected static final String EXTENSION = "html";

	/**
	 * @scr.property name="sling.servlet.selectors"
	 */
	protected static final String SELECTOR = "customMailRel";

	protected static final String MAILTO_PROPERTY = "mailto";

	protected static final String OPTMAILTO_PROPERTY = "optMailTo";

	protected static final String CC_PROPERTY = "cc";

	protected static final String BCC_PROPERTY = "bcc";

	protected static final String SUBJECT_PROPERTY = "subject";

	protected static final String FROM_PROPERTY = "from";

	protected static final String CLIENTTEXT_PROPERTY = "mailClientText";

	protected static final String ADMINTEXT_PROPERTY = "mailAdminText";

	protected static final String MAIL_PROPERTY = "email";

	protected static final String CRYPTED_VALUE = "_crypted-value_";

	protected  static final String NT_UNSTRUCTURED = "nt:unstructured";

	protected  static final String PROPERTY = "property";

	private final String[] fileExtensionAllowed = {"doc", "docx", "odt", "sxw", "pdf"}; //estensioni file allegato consentite

	private static final Integer MAX_FILE_SIZE_MB = 3;//in Mbyte
	private static final Integer MAX_FILE_SIZE = MAX_FILE_SIZE_MB * 1024 * 1024;//in byte //max dimensione file allegato

	private final List<String> paramsToExclude = List.of("g-recaptcha-response",CRYPTED_VALUE,"resourceType","resourcePath");
	public static final String ERROR_MESSAGE_ATTRIBUTE_NAME = "contactFormErrorAttr"; //Nome attributo in sessione contenente eventuali messaggi di errore
	//inerenti il fallito invio del form contatti

	/**
	 * @scr.reference policy="dynamic" cardinality="0..1"
	 */
	@Reference
	private transient MailService mailService;

	@Reference
	private transient QueryBuilder qBuilder;

	/**
	 * @see OptingServlet#accepts(SlingHttpServletRequest)
	 */
	public boolean accepts(SlingHttpServletRequest request) {
		return EXTENSION.equals(request.getRequestPathInfo().getExtension());
	}

	/**
	 * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(SlingHttpServletRequest, SlingHttpServletResponse)
	 */
	public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see SlingAllMethodsServlet#doPost(SlingHttpServletRequest, SlingHttpServletResponse)
	 */
	public void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws IOException, RuntimeException {

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
		List<String> errors = new ArrayList<>();
		/*
		 * Adapting the resource resolver to the session object
		 */
		Session session = resourceResolver.adaptTo(Session.class);


		//recupero risorsa di provenienza contenente il componente recaptcha per il recupero della secret key

		final String formStart = request.getParameter("resourcePath");
		if (StringUtils.isBlank(formStart)) {
			logger.debug("It is no possible to verify recaptcha");
			errors.add("It is no possible to verify recaptcha");
		} else {
			//recupero la risorsa recaptcha
            final String recaptchaToken = request.getParameter("g-recaptcha-response");
            if(StringUtils.isBlank(recaptchaToken) || !checkRecaptcha(recaptchaToken,getSecretKey(request))){
                logger.debug("Recaptcha verification failed");
                errors.add("Recaptcha verification failed");
            }
		}

		Map<String, String> predicate = new HashMap<>();
		predicate.put("path", request.getResource().getPath() + "/jcr:content");
		predicate.put("type", NT_UNSTRUCTURED);
		predicate.put(PROPERTY, MAILTO_PROPERTY);
		predicate.put("property.operation", "exists");
		Resource resourceContainer = ModelUtils.findResourceByPredicate(qBuilder, predicate, session, resourceResolver);
		if (resourceContainer != null) {
			final ValueMap values = ResourceUtil.getValueMap(resourceContainer);
			mailTo = values.get(MAILTO_PROPERTY, String[].class);//destinatario admin di default settato a livello di container
			ccRecs = values.get(CC_PROPERTY, String[].class);
			bccRecs = values.get(BCC_PROPERTY, String[].class);
			assert resBundle != null;
			subject = values.get(SUBJECT_PROPERTY, resBundle.getString("Form Mail"));
			fromAddress = values.get(FROM_PROPERTY, ""); //mittente quando email è per il cliente
			clientText = values.get(CLIENTTEXT_PROPERTY, "");
			adminText = values.get(ADMINTEXT_PROPERTY, "");
		}

		final String informationValue = request.getParameter("information");
		// nome campo del form contenente il drop down delle opzioni


		String optMailTo = null;
		if (StringUtils.isNotBlank(informationValue)) {
			//si cerca il nodo child di container il cui nome è information
			predicate = new HashMap<>();
			assert resourceContainer != null;
			predicate.put("path", resourceContainer.getPath());
			predicate.put("type", NT_UNSTRUCTURED);
			predicate.put(PROPERTY, "name");
			predicate.put("property.value", "information");

			Resource optResource = ModelUtils.findResourceByPredicate(qBuilder, predicate, session, resourceResolver);
			if (optResource != null) {
				predicate = new HashMap<>();
				predicate.put("path", optResource.getPath());
				predicate.put("type", NT_UNSTRUCTURED);
				predicate.put(PROPERTY, "value");
				predicate.put("property.value", informationValue);

				Resource optItemResource = ModelUtils.findResourceByPredicate(qBuilder, predicate, session, resourceResolver);
				ValueMap property = optItemResource.adaptTo(ValueMap.class);
				assert property != null;
				optMailTo = property.get(OPTMAILTO_PROPERTY, String.class);
			}
		}


		final String emailValue = request.getParameter(MAIL_PROPERTY); //nome campo del form contenente l'indirizzo email del cliente.E' sia il destinatario della mail per il cliente
		//nonché il mittente della mail per l'admin
		final List<String> contentNamesList = new ArrayList<>();

		final Iterator<String> names = getRequestParamIterators(request);
		while (names.hasNext()) {
			final String name = names.next();
			contentNamesList.add(name);
		}

		final List<String> namesList = new ArrayList<>();
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

		if (StringUtils.isBlank(fromAddress)) {
			logger.debug("No sender specified for client email");
			errors.add("It's not possible to send the email.\nPlease, contact the help desk ");
		}
		if (StringUtils.isBlank(optMailTo) && (mailTo == null || mailTo.length == 0)) {
			logger.debug("No receiver specified for admin email");
			errors.add("It's not possible to send the email.\nPlease, contact the help desk ");
		}

		//controllo errori in file uploaded
		final List<RequestParameter> attachments = new ArrayList<>();
		for (final String name : namesList) {
			final RequestParameter rp = request.getRequestParameter(name);
			if (rp != null && !rp.isFormField() && rp.getSize() > 0) {
				if (!isExtensionValid(rp.getFileName())) {
					logger.debug("File extension for " + rp.getFileName() + " not allowed. File allowed {0}", String.join(",", fileExtensionAllowed));
					errors.add("File extension for " + rp.getFileName() + " not allowed. File allowed " + String.join(",", fileExtensionAllowed));
				}

				if (rp.getSize() > MAX_FILE_SIZE) {
					logger.debug("File size for " + rp.getFileName() + " not allowed. Max size {0} MB", new Object[]{MAX_FILE_SIZE_MB});
					errors.add("File size for " + rp.getFileName() + " not allowed. Max size MAX_FILE_SIZE_MB MB");
				}

			}
		}
		int status = 200;
		if (errors.isEmpty()) {

			//email per cliente
			status = sendEmail(request, clientText, new String[]{emailValue}, fromAddress, null, null, subject, namesList, resBundle,true,null);
			//email per admin
			sendEmail(request, adminText, StringUtils.isNotBlank(optMailTo) ? new String[]{optMailTo} : mailTo, fromAddress, ccRecs, bccRecs, subject, namesList, resBundle,false,new String[]{emailValue});
		} else {
            status = 400;
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
					Collections.singletonMap("stats", new String[]{String.valueOf(status)}));
			return;
		}
		response.setStatus(status);
	}

	public Resource getParentResource(ResourceResolver resourceResolver, String formStart) {
		return resourceResolver.getResource(formStart);
	}


	private int sendEmail(SlingHttpServletRequest request, String mailText, String[] mailTo, String fromAddress, String[] ccRecs, String[] bccRecs, String subject, List<String> namesList, ResourceBundle resBundle, Boolean removeFormLink,String[] replyTo) {
		int status = 200;
		try {
			final StringBuilder builder = new StringBuilder();

			// construct msg
			final StringBuilder buffer = new StringBuilder();

			if(!removeFormLink){
				builder.append(request.getScheme());
				builder.append("://");
				builder.append(request.getServerName());
				if ((request.getScheme().equals("https") && request.getServerPort() != 443)
						|| (request.getScheme().equals("http") && request.getServerPort() != 80)) {
					builder.append(':');
					builder.append(request.getServerPort());
				}
				builder.append(request.getRequestURI());

				String text = resBundle.getString("You've received a new form based mail from {0}.");//testo editoriale
				text = text.replace("{0}", builder.toString());
				buffer.append(text);
				buffer.append("\n\n");
			}

			// construct msg
			if (StringUtils.isNotBlank(mailText)) {
				buffer.append(mailText);
				buffer.append("\n\n");
			}
			/*buffer.append(resBundle.getString("Values"));
			buffer.append(":\n\n");*/


			// now add form fields to message
			// and uploads as attachments
			final List<RequestParameter> attachments = new ArrayList<>();
			for (final String name : namesList) {
				final RequestParameter rp = request.getRequestParameter(name);
				if (rp == null || paramsToExclude.contains(name)) {
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
				}
			}
			// if we have attachments we send a multi part, otherwise a simple email
			final Email email;
			if (!attachments.isEmpty()) {
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
			//reply-to
			if(replyTo != null){
				for (final String rep : replyTo) {
					email.addReplyTo(rep);
				}
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
			if (!fromAddress.isEmpty()) {
				email.setFrom(fromAddress);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Sending form activated mail: fromAddress={}, to={}, subject={}, text={}.",
						new Object[]{fromAddress, mailTo, subject, buffer});
			}

			this.mailService.sendEmail(email);

		} catch (EmailException | IOException e) {
			logger.error("Error sending email: " + e.getMessage(), e);
			status = 500;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return status;
	}

	public boolean isExtensionValid(String filename) {
		String fileExt = FilenameUtils.getExtension(filename);
		return Arrays.stream(fileExtensionAllowed).anyMatch(fileExt::equalsIgnoreCase);
	}

	public Iterator getRequestParamIterators(SlingHttpServletRequest request) {
		return FormsHelper.getContentRequestParameterNames(request);
	}

	public Iterator<Resource> getResourceFormElements(SlingHttpServletRequest request) {
		return FormsHelper.getFormElements(request.getResource());
	}

	public CloseableHttpClient getHttpClient() {
		return HttpClients.createDefault();
	}
    protected String getSecretKey(SlingHttpServletRequest request) {
        try {
            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource currentResource = request.getResource();
            Session session = resourceResolver.adaptTo(Session.class);
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            StringBuilder queryInsidePage = new StringBuilder();

            queryInsidePage.append("SELECT * FROM [nt:unstructured] AS s ");
            queryInsidePage.append("WHERE ISDESCENDANTNODE('" +currentResource.getPath() + "') ");
            queryInsidePage.append(" AND s.[sling:resourceType] = 'relife/components/form/recaptcha' ");

            Query query = queryManager.createQuery(queryInsidePage.toString(), Query.JCR_SQL2);
            QueryResult queryPageResult = query.execute();
            NodeIterator nodes = queryPageResult.getNodes();
            if( nodes.hasNext()){
                Node recaptcha = nodes.nextNode();
                if(recaptcha.hasProperty("secretKey")){
                    return recaptcha.getProperty("secretKey").getString();
                }
            }

        }catch (RepositoryException e){
            logger.error("Missing Recaptcha", e);
        }
        return null;
    }

	public boolean checkRecaptcha(String responseToken, String secretKey) throws RuntimeException {
		if (StringUtils.isBlank(responseToken) || StringUtils.isBlank(secretKey))
			return false;

		CloseableHttpClient httpclient = getHttpClient();
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

					Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) ->

							LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_DATE_TIME)
					).create();
					RecaptchaValidationResponse result = gson.fromJson(resp, RecaptchaValidationResponse.class);
					return (result != null && result.isSuccess());

				}
			}
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
		return false;
	}
}