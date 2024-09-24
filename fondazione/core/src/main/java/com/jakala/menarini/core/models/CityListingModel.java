package com.jakala.menarini.core.models;

import com.adobe.cq.dam.cfm.ContentFragment;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Model(adaptables = Resource.class)
public class CityListingModel {

    private static final Logger LOG = LoggerFactory.getLogger(CityListingModel.class);

    private static final String CITIES_PATH = "/content/dam/fondazione/cities/";

    @SlingObject
    private ResourceResolver resourceResolver;

    @SlingObject
    private Resource currentResource;

    private List<City> cities = new ArrayList<>();

    public List<City> getCities() {
        return cities;
    }

    @PostConstruct
    protected void init() {
        cities = new ArrayList<>();

        try {
            Resource parentResource = resourceResolver.getResource(CITIES_PATH);
        
            String language = ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource);

            if (parentResource != null) {
                Iterator<Resource> children = parentResource.listChildren();
                while (children.hasNext()) {
                    Resource child = children.next();
                    ContentFragment fragment = child.adaptTo(ContentFragment.class);

                    if (fragment != null) {
                        String id = fragment.getElement("id").getContent();
                        String nome = ModelHelper.getLocalizedElementValue(fragment, language, "name", fragment.getElement("name").getContent());
                        String path = fragment.getName();

                        cities.add(new City(id, nome, CITIES_PATH + path));
                    }
                }
            }

            cities.sort((city1, city2) -> city1.getName().compareToIgnoreCase(city2.getName()));
        } catch (Exception e) {
            LOG.error("Error retrieving topic content fragments", e);
        }
    }

    // Inner class to represent each city
    public static class City {
        private String id;
        private String name;
        private String path;

        public City(String id, String name, String path) {
            this.id = id;
            this.name = name;
            this.path = path;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}