package com.jakala.menarini.core.models.utils;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.google.gson.JsonObject;
import com.jakala.menarini.core.exceptions.CreateTagException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ModelUtils {

	// Private constructor to hide the implicit public one
	private ModelUtils() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	private static Logger LOGGER = LoggerFactory.getLogger(ModelUtils.class);

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
		label = label.replaceAll("[\\ñ]","n");
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
			jcrNode = pageNode.addNode(JcrConstants.JCR_CONTENT,  "cq:PageContent");

		}
		if(StringUtils.isNotBlank(resourceType))
			jcrNode.setProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, resourceType);

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
			LOGGER.error(currentParentTemplate + " not found", e);
		}

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
					p = findChildComponentByResourceType(iter.next(), resourceType);
					if (p != null)
						break;
				}
			}

		} catch (RepositoryException re) {
			LOGGER.error(resourceType + " not found", re);
		}
		return p;
	}

	public static Locale getResourceLocale(Page currentPage) {
		if (currentPage != null) {
			return currentPage.getLanguage(false);
		}
		return null;
	}

	public static String getFormat(Locale local) {
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
		if (StringUtils.isEmpty(link) || link.equals("#") || link.contains("@")) {
			return link;
		}

		if (link.startsWith("/") && link.contains(".pdf")) {
			return link;
		}

		if (link.startsWith("/") && !link.contains(".html")) {
			return link + ".html";
		}

		if (!link.contains("https://") && !link.contains("http://")) {
			return "http://" + link;
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
	public static Resource findResourceByPredicate(QueryBuilder qBuilder, Map<String, String> predicate, Session session, ResourceResolver resourceResolver) throws RepositoryException{
		if(qBuilder == null || predicate == null || session == null || resourceResolver == null) {
			return null;
		}

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

			path = hit.getPath();

			resource = resourceResolver.getResource(path);
		}
		return resource;
	}

	public static int extractIntFromString(String input) {
		int extractedInt = 0;
		try {
			extractedInt = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			LOGGER.error("Error in extracting int from string", e);
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

	public static String encrypt(String key, String iv, String value, String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		if (iv == null) {
			throw new InvalidAlgorithmParameterException("IV parameter cannot be null");
		}
		Cipher cipher = Cipher.getInstance(transformation);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

		byte[] encrypted = cipher.doFinal(value.getBytes());
		return Base64.getEncoder().encodeToString(encrypted);
	}

	public static String decrypt(String key, String iv, String encryptedValue, String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException  {
		if (iv == null) {
			throw new InvalidAlgorithmParameterException("IV parameter cannot be null");
		}
		Cipher cipher = Cipher.getInstance(transformation);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
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
		if(StringUtils.isNotBlank(path) && StringUtils.isNotBlank(type)) {
			return JcrUtil.createPath(path, type, session);
		}
		return null;
	}

    public static List<String> getPageTags(ResourceResolver resourceResolver, String pagePath) {
        List<String> tags = new ArrayList<>();
        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        Resource pageContentResource = resourceResolver.getResource(pagePath + "/jcr:content");

        if (tagManager != null && pageContentResource != null) {
            Tag[] pageTags = tagManager.getTags(pageContentResource);
            for (Tag tag : pageTags) {
                tags.add(tag.getTagID());
            }
        }
        return tags;
    }

	public static Tag findTag(String namespace, String nestedTagPath, String tagName, ResourceResolver resolver) {
		Tag tag = null;
		if(StringUtils.isNotBlank(tagName)) {
			TagManager tagManager =  resolver.adaptTo(TagManager.class);
			StringBuilder tagPathBuilder = new StringBuilder();
			if (StringUtils.isNotBlank(namespace)) {
				tagPathBuilder.append(namespace);
			}
			if (StringUtils.isNotBlank(nestedTagPath)) {
				tagPathBuilder.append(nestedTagPath).append("/");
			}
			tagPathBuilder.append(tagName);
			tag = tagManager.resolve(tagPathBuilder.toString());
		}
		return tag;
	}

	public static Tag createTag(String namespace, String nestedTagPath, String title, HashMap properties, Session session, ResourceResolver resolver) throws CreateTagException {

		if(StringUtils.isNotBlank(title)){
			String tagName = ModelUtils.getNodeName(title);
			Tag tag = ModelUtils.findTag(namespace, nestedTagPath, tagName, resolver);
			TagManager tagManager =  resolver.adaptTo(TagManager.class);
			if(tag == null){
				try{
					tag = tagManager.createTag(namespace+(StringUtils.isNotBlank(nestedTagPath) ? nestedTagPath+"/":"")+tagName, title, null,true);
					if(tag == null){
						throw new CreateTagException("Error in creating tag " + title);
					} else {
						session.save();
					}
				} catch (InvalidTagFormatException | RepositoryException e) {
					LOGGER.error("Error in creating tag " + title, e);
					throw new CreateTagException("Error in creating tag " + tag);
				}
			}
			return tag;
		}
		return null;
	}


}
