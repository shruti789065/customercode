package com.adiacent.menarini.relife.core.utils;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Map;


public class ModelUtils {

	public static Page getHomePage(ResourceResolver resourceResolver, String currentPage) {
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		if(pageManager == null)
			return null;
		pageManager.getPage(currentPage).getDepth();
		int pathIndex = 3;
		Page homepage = pageManager.getPage(currentPage).getAbsoluteParent(pathIndex);
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
