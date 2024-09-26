package com.jakala.menarini.core.models;

import java.util.Iterator;

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
}
