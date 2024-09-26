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
public class NationListingModel {

    private static final Logger LOG = LoggerFactory.getLogger(CityListingModel.class);

    private static final String NATIONS_PATH = "/content/dam/fondazione/nations/";

    @SlingObject
    private ResourceResolver resourceResolver;

    @SlingObject
    private Resource currentResource;

    private List<Nation> nations;

    public List<Nation> getNations() {
        return nations;
    }

    @PostConstruct
    protected void init() {
        this.nations = new ArrayList<Nation>();
        try {
            Resource parentResource = resourceResolver.getResource(NATIONS_PATH);
            if (parentResource != null) {
                String language = ModelHelper.getCurrentPageLanguage(resourceResolver, currentResource);
                Iterator<Resource> children = parentResource.listChildren();
                while (children.hasNext()) {
                    Resource child = children.next();
                    ContentFragment fragmentNation = child.adaptTo(ContentFragment.class);
                    if (fragmentNation != null) {
                        Nation nation = new Nation(
                                fragmentNation.getElement("id").getContent(),
                                ModelHelper.getLocalizedElementValue(
                                        fragmentNation, language, "name",
                                        fragmentNation.getElement("name").getContent()),
                                NATIONS_PATH + fragmentNation.getName()
                        );
                        nations.add(nation);
                    }
                }
            }
            nations.sort((nation1,nation2) ->nation1.getName().compareToIgnoreCase(nation2.getName()));
        } catch (Exception e) {
            LOG.error("Error retrieving nation content fragments", e);
        }

    }

    public void setNations(List<Nation> nations) {
        this.nations = nations;
    }

    public static class Nation {
        private String id;
        private String name;
        private String path;

        public Nation(String id, String name, String path) {
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
