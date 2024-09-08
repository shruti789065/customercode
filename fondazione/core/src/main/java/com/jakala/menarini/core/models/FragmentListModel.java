package com.jakala.menarini.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentVariation;
import com.adobe.cq.dam.cfm.FragmentData;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = Resource.class, adapters = FragmentListModel.class, resourceType = "fondazione/components/fragmentlist")
public class FragmentListModel {
    
    @Inject
    private ResourceResolver resourceResolver;

    @SlingObject
    private Resource currentResource;

    private List<Fragment> fragments = new ArrayList<>();

    @PostConstruct
    protected void init() {
        String parentPath = "/content/dam/fondazione/event";
        Resource parentResource = resourceResolver.getResource(parentPath);
        
        String language = getCurrentPageLanguage();

        if (parentResource != null) {
            Iterator<Resource> children = parentResource.listChildren();
            while (children.hasNext()) {
                Resource child = children.next();
                ContentFragment fragment = child.adaptTo(ContentFragment.class);

                if (fragment != null) {
                    String title = getLocalizedElementValue(fragment, language, "titolo", fragment.getTitle());
                    String description = getLocalizedElementValue(fragment, language, "descrizione", fragment.getDescription());
                    
                    fragments.add(new Fragment(title, description, child.getPath()));
                }
            }
        }
    }

    private String getCurrentPageLanguage() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager != null) {
            Page currentPage = pageManager.getContainingPage(currentResource);
            if (currentPage != null) {
                return currentPage.getLanguage(false).getLanguage();
            }
        }
        return "it";
    }

    private String getLocalizedElementValue(ContentFragment fragment, String language, String field, String value) {

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

    public List<Fragment> getFragments() {
        return fragments;
    }

    public static class Fragment {
        private String title;
        private String description;
        private String path;

        public Fragment(String title, String description, String path) {
            this.title = title;
            this.description = description;
            this.path = path;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getPath() {
            return path;
        }
    }

}
