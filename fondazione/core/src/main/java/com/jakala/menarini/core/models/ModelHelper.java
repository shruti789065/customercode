package com.jakala.menarini.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentVariation;
import com.adobe.cq.dam.cfm.FragmentData;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

public class ModelHelper {
    
    public static String getCurrentPageLanguage(ResourceResolver resourceResolver, Resource currentResource) {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager != null) {
            Page currentPage = pageManager.getContainingPage(currentResource);
            if (currentPage != null) {
                return currentPage.getLanguage(false).getLanguage();
            }
        }
        return "en";
    }

    public static String getLocalizedElementValue(ContentFragment fragment, String language, String field, String value) {
        if (language == null) {
            language = "en";
        }
        String variationId = fragment.getName() + "_" + language;
        final String valKey = field + "_" + language;

        Iterator<ContentElement> elements = fragment.getElements();
        while (elements.hasNext()) {
            ContentElement cfElement = elements.next();
            ContentVariation cv = cfElement.getVariation(variationId);
            FragmentData fragmentData = cv.getValue();
            
            String key = cfElement.getName() + "_" + language;

            if (valKey.equals(key)){
                value = (String)fragmentData.getValue();
                break;
            } 
        }
        if (value == null || value.isEmpty()) {
            value = fragment.getElement(field) == null ? "" : fragment.getElement(field).getContent();
        }
        return value;
    }

    public static String convertPredicatesToDebugFormat(Map<String, String> predicates) {
        StringBuilder queryBuilder = new StringBuilder();
        
        // Ordina le chiavi per una migliore leggibilità
        List<String> keys = new ArrayList<>(predicates.keySet());
        Collections.sort(keys);
        String lineSeparator = System.lineSeparator(); 
        for (String key : keys) {
            queryBuilder.append(key)
                       .append("=")
                       .append(predicates.get(key))
                       .append(lineSeparator);
        }
        
        return queryBuilder.toString();
    }


    public static String nextSequence(ResourceResolver resourceResolver, String path) throws RepositoryException {
        QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
        Session session = resourceResolver.adaptTo(Session.class);
        Map<String, String> predicate = new HashMap<>();

        predicate.put("type", DamConstants.NT_DAM_ASSET);
        predicate.put("path", path);
        predicate.put("orderby", "@jcr:content/data/master/id");
        predicate.put("orderby.sort", "desc");
        predicate.put("p.limit", "1");
        predicate.put("p.offset", "0");
        Query query = queryBuilder.createQuery(PredicateGroup.create(predicate), session);
        SearchResult result = query.getResult();

        for (Hit hit : result.getHits()) {
            ContentFragment fragment = hit.getResource().adaptTo(ContentFragment.class);
            if (fragment != null) {
                String lastSequence = fragment.getElement("id").getContent();
                int sequenceNumber = Integer.parseInt(lastSequence);
                return String.valueOf(sequenceNumber + 1);
            }
        }
        
        return null;
    }

    /**
     * Find the fragment by id field
     */
    public static ContentFragment findFragmentById(ResourceResolver resolver, String id, String path) throws RepositoryException {
        QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);
        Session session = resolver.adaptTo(Session.class);
        Map<String, String> predicate = new HashMap<>();

        predicate.put("type", DamConstants.NT_DAM_ASSET);
        predicate.put("path", path);
        predicate.put("property", "jcr:content/data/master/id");
        predicate.put("property.value", id);
        predicate.put("property.operation", "equals");
        Query query = queryBuilder.createQuery(PredicateGroup.create(predicate), session);
        SearchResult result = query.getResult();

        for (Hit hit : result.getHits()) {
            ContentFragment contentFragment = hit.getResource().adaptTo(ContentFragment.class);
            if (contentFragment != null) {
                return contentFragment;
            }
        }
        
        return null;
    }

    /**
     * Find the resource by id field
     */
    public static Resource findResourceById(ResourceResolver resolver, String id, String path) throws RepositoryException {
        QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);
        Session session = resolver.adaptTo(Session.class);
        Map<String, String> predicate = new HashMap<>();

        predicate.put("type", DamConstants.NT_DAM_ASSET);
        predicate.put("path", path);
        predicate.put("property", "jcr:content/data/master/id");
        predicate.put("property.value", id);
        predicate.put("property.operation", "equals");
        com.day.cq.search.Query query = queryBuilder.createQuery(PredicateGroup.create(predicate), session);
        SearchResult result = query.getResult();

        for (Hit hit : result.getHits()) {
            return  hit.getResource();
        }
        
        return null;
    }

    public static List<Hit> findResourceByIds(ResourceResolver resolver, List<String> ids, String path) {
        QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);
        Session session = resolver.adaptTo(Session.class);
        Map<String, String> predicate = new HashMap<>();

        predicate.put("type", DamConstants.NT_DAM_ASSET);
        predicate.put("path", path);
        if(ids.size() == 1) {
            predicate.put( "property", "jcr:content/data/master/id");
            predicate.put( "property.value", ids.get(0));
            predicate.put("property.operation", "equals");
        } else {
            boolean addOr = false;
            for(int i = 0;  i < ids.size() ; i++ ) {
                predicate.put("group.1_group." +  (i + 1) + "_property", "jcr:content/data/master/id");
                predicate.put( "group.1_group." +  (i + 1) + "_property.value", ids.get(i));
                predicate.put("group.1_group." +  (i + 1) + "_property.operation", "equals");
                if(i == 1) {
                    addOr = true;
                }
            }
            if(addOr) {
                predicate.put("group.1_group.p.or", "true");
            }

        }
        com.day.cq.search.Query query = queryBuilder.createQuery(PredicateGroup.create(predicate), session);
        SearchResult result = query.getResult();
        return result.getHits();
    }



}
