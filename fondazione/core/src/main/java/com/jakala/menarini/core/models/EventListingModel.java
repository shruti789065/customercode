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

import com.adobe.cq.dam.cfm.ContentFragment;

@Model(adaptables = Resource.class, adapters = EventListingModel.class, resourceType = "fondazione/components/fragmentlist")
public class EventListingModel {
    
    @Inject
    private ResourceResolver resourceResolver;

    @SlingObject
    private Resource currentResource;

    private List<Fragment> fragments = new ArrayList<>();
    
    private static final String EVENT_PATH = "/content/dam/fondazione/events";

    @PostConstruct
    protected void init() {
        
        Resource parentResource = resourceResolver.getResource(EVENT_PATH);
        
        String language = ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource);

        if (parentResource != null) {
            Iterator<Resource> children = parentResource.listChildren();
            while (children.hasNext()) {
                Resource child = children.next();
                ContentFragment fragment = child.adaptTo(ContentFragment.class);

                if (fragment != null) {
                    String title = ModelHelper.getLocalizedElementValue(fragment, language, "titolo", fragment.getTitle());
                    String description = ModelHelper.getLocalizedElementValue(fragment, language, "descrizione", fragment.getDescription());
                    
                    fragments.add(new Fragment(title, description, child.getPath()));
                }
            }
        }
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
