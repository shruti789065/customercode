package com.adiacent.menarini.menarinimaster.core.utils;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ModelUtils {

	public static String getNodeName(String label){
		if(StringUtils.isBlank(label))
			return null;
		label = label.trim().toLowerCase().replaceAll("[\\(\\)\\[\\]\\']","");
		label = label.replaceAll("[\\s:]","-");
		label = label.replaceAll("[\\?]","-");
		label = label.replaceAll("[\\%]","-");
		label = label.replaceAll("[\\®]","");
		label = label.replaceAll("[\\/]","-");
		label = label.replaceAll("[\\:]","-");
		label = label.replaceAll("[\\']","-");
		label = label.replaceAll("[\\’]","-");
		label = label.replaceAll("[\\“]","-");
		label = label.replaceAll("[\\”]","-");
		label = label.replaceAll("[\\,]","-");
		label = label.replaceAll("[\\?]","-");
		label = label.replaceAll("[\\‘]","-");
		label = label.replaceAll("[\\’]","-");
		label = label.replaceAll("[\\™]","-");
		label = label.replaceAll("[\\.]","-");
		label = label.replaceAll("[\\\"]","-");
		label = label.replaceAll("[\\!]","-");
		label = label.replaceAll("[\\à]","a");
		label = label.replaceAll("[\\è]","e");
		label = label.replaceAll("[\\é]","e");
		label = label.replaceAll("[\\ì]","i");
		label = label.replaceAll("[\\ò]","o");
		label = label.replaceAll("[\\ù]","u");
		label = label.replaceAll("[\\…]","-");
		label = label.replaceAll("[\\–]","-");
		label = label.replaceAll("[\\<]","-");
		label = label.replaceAll("[\\>]","-");
		label = label.replaceAll("[\\+]","-");
		return JcrUtil.escapeIllegalJcrChars(label);
	}

	public static Page createPage(ResourceResolver resolver, Session session, String prefixPath, String name, String title, String template, String resourceType ) throws WCMException, RepositoryException {
		if(resolver == null || session == null || StringUtils.isBlank(prefixPath) || StringUtils.isBlank(name) || StringUtils.isBlank(title) ||
				StringUtils.isBlank(template))
			return null;

		PageManager pageManager = resolver.adaptTo(PageManager.class);
		Page page = pageManager.create(prefixPath, name, template, title);


		Node pageNode = page.adaptTo(Node.class);
		Node jcrNode = null;

		if (page.hasContent()) {
			jcrNode = page.getContentResource().adaptTo(Node.class);
		} else {
			jcrNode = pageNode.addNode("jcr:content", "cq:PageContent");

		}
		if(StringUtils.isNotBlank(resourceType))
			jcrNode.setProperty("sling:resourceType", resourceType);

		return page;
	}

	public static Page findPageByParentTemplate(Page currentPage, String templateName) {

		if (currentPage == null)
			return null;

		if (currentPage.getParent() == null)
			return null;

		String currentParentTemplate = null;
		Node n = currentPage.getParent().getContentResource().adaptTo(Node.class);
		try {
			if (!n.hasProperty(Constants.TEMPLATE_PROPERTY))
				return null;
			currentParentTemplate = n.getProperty(Constants.TEMPLATE_PROPERTY).getString();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

        /* if(currentPage.getParent().getTemplate() == null ||
            StringUtils.isBlank(currentPage.getParent().getTemplate().getTitle())
        )
            return null;

        String currentParentTemplate  = currentPage.getParent().getTemplate().getTitle(); */

		if (templateName.equals(currentParentTemplate))
			return currentPage;

		return findPageByParentTemplate(currentPage.getParent(), templateName);
	}

	public static Resource findChildComponentByResourceType(Resource resource, String resourceType) {

		if (resource == null)
			return null;

		Resource p = null;
		Node node = resource.adaptTo(Node.class);
		try {
			if (StringUtils.isNotBlank(node.getProperty(Constants.RESOURCE_TYPE_PROPERTY).getString()) &&
					resourceType.equals(node.getProperty(Constants.RESOURCE_TYPE_PROPERTY).getString()))
				return resource;
			if (node.hasNodes()) {

				Iterator<Resource> iter = resource.listChildren();
				while (iter.hasNext()) {
					p = findChildComponentByResourceType((Resource) iter.next(), resourceType);
					if (p != null)
						break;
				}
			}

		} catch (RepositoryException re) {
			re.printStackTrace();
		}
		return p;
	}

	public static Locale getResourceLocale(Page currentPage) throws RepositoryException {
		if (currentPage != null) {
			return currentPage.getLanguage(false);
		}
		return null;
	}

	public static String getFormat(Locale local) throws RepositoryException {
		if (local != null) {
			if (local.getCountry().equals("us")) {
				return Constants.FORMAT_ANNO_MESE_GIORNO;
			} else {
				return Constants.FORMAT_ANNO_MESE_GIORNO;
			}
		} else {
			return Constants.FORMAT_ANNO_MESE_GIORNO;
		}
	}

	public static String getModifiedLink(String link) {
		if (StringUtils.isNotEmpty(link) && !link.equals("#") && !(link.startsWith("/") && link.contains(".pdf")) && !link.contains("@")) {
			link = link.startsWith("/") && link.contains(".html") ? link : link.startsWith("/") && !link.contains(".html") ? link + ".html" : link.contains("https://") ?
					link : link.contains("http://") ? link :  "http://" + link;
		}
		return link;
	}

	public static Page getHomePage(ResourceResolver resourceResolver, String currentPage) {
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		if(pageManager == null)
			return null;
		pageManager.getPage(currentPage).getDepth();
		int pathIndex = 3;
		String cqTemplate = pageManager.getPage(currentPage).getAbsoluteParent(pathIndex).getProperties().get("cq:template",String.class);
		if(!cqTemplate.toLowerCase().contains("homepage")){
			pathIndex = 2;
		}
		Page homepage = pageManager.getPage(currentPage).getAbsoluteParent(pathIndex);
		return homepage;
	}


    public static void initializeInternalMenuComponent(Page page, ResourceResolver resourceResolver, boolean isPublishModeEnabled) throws RepositoryException {

		//si controlla se la pagina ha uno dei template per i quali occorre eseguire la logica dell'evento
		if(page != null) {
			ValueMap properties = page.getProperties();
			String template = properties.get("cq:template", String.class);

			Pattern p = Pattern.compile(Constants.INTERNAL_MENU_TEMPLATE_REGEXP); // Create a regex
			Matcher m = p.matcher(template);  // Our string for matching
			if (m.matches()) {
				Page homepage = ModelUtils.getHomePage(resourceResolver, page.getPath());
				if(homepage!=null) {
					properties = homepage.getProperties();
					template = properties.containsKey("cq:template") ? properties.get("cq:template", String.class) : "";

					Page parentPage = ModelUtils.findPageByParentTemplate(page, template);
					Page navigationRoot = parentPage;
					//recupero internalmenu component se non siamo in publish mode
					if (!isPublishModeEnabled) {
						Resource internalMenu = ModelUtils.findChildComponentByResourceType(page.getContentResource(), "menarinimaster/components/internalmenu");
						if (internalMenu != null) {
							Node node = internalMenu.adaptTo(Node.class);
							if(navigationRoot!=null) {
								node.setProperty("navigationRoot", navigationRoot.getPath());
								node.setProperty("structureStart", 0);
								node.getSession().save();
							}
						}
					}
				}
			}
		}

    }
	public static Resource findResourceByPredicate(QueryBuilder qBuilder, Map<String, String> predicate, Session session, ResourceResolver resourceResolver){
		if(qBuilder == null || predicate == null || session == null || resourceResolver == null)
			return null;

		/**
		 * Creating the Query instance
		 */
		Query query = qBuilder.createQuery(PredicateGroup.create(predicate), session);
		/**
		 * Getting the search results
		 */
		SearchResult searchResult = query.getResult();
		Resource resource = null;
		for(Hit hit : searchResult.getHits()) {
			String path = null;
			try {
				path = hit.getPath();
			} catch (RepositoryException e) {
				throw new RuntimeException(e);
			}
			resource = resourceResolver.getResource(path);
		}
		return resource;
	}

	public static int extractIntFromString(String input) {
		int extractedInt = 0;
		try {
			extractedInt = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			System.out.println("Impossibile estrarre un intero dalla stringa.");
		}
		return extractedInt;
	}
	public static String extractIntAsString(String input) {
		StringBuilder result = new StringBuilder();

		for (char c : input.toCharArray()) {
			if (Character.isDigit(c)) {
				result.append(c);
			}
		}
		return result.toString();
	}



	public static Map<String, Object> convertJsonObjectToMap(JsonObject jsonObject) {
		Map<String, Object> properties = new HashMap<>();
		for (Map.Entry<String, com.google.gson.JsonElement> entry : jsonObject.entrySet()) {
			properties.put(entry.getKey(), entry.getValue().getAsString());
		}
		return properties;
	}
	public static String encrypt(String key, String iv, String value, String TRANSFORMATION) throws Exception {
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

		byte[] encrypted = cipher.doFinal(value.getBytes());
		return Base64.getEncoder().encodeToString(encrypted);
	}

	public static String decrypt(String key, String iv, String encryptedValue, String TRANSFORMATION) throws Exception {
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

		byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
		return new String(decrypted);
	}

	public static boolean isValidEmailFormat(String email) {
		// Implementa la tua logica di validazione dell'indirizzo email
		// Ad esempio, puoi utilizzare espressioni regolari
		// Qui viene fornito solo un esempio di base
		return email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
	}

	public static Node createNode(String path, String type, Session session) throws RepositoryException {
		if(StringUtils.isNotBlank(path) && StringUtils.isNotBlank(type))
				return JcrUtil.createPath(path, type, session);
		return null;
	}
}
