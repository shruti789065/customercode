package com.jakala.menarini.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentVariation;
import com.adobe.cq.dam.cfm.FragmentData;
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
}
