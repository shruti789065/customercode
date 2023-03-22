package com.adiacent.menarini.menarinimaster.core.utils;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class ModelUtils {

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
			link = link.startsWith("/") ? link + ".html" : link.contains("https://") ?
					link : link.contains("http://") ? link : "http://" + link;
		}
		return link;
	}

	public static Page getHomePage(ResourceResolver resourceResolver, String currentPage) {
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		if(pageManager == null)
			return null;
		Page homepage = pageManager.getPage(currentPage).getAbsoluteParent(3);
		return homepage;
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

}
