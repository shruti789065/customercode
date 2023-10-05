package com.adiacent.menarini.menarinimaster.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class })
public class PipelineItemModel {
    @Inject
    Node currentNode;

    private String compoundValue;

    @PostConstruct
    protected void init() throws RepositoryException {
        if(currentNode != null && currentNode.hasProperty("compoundValue")){
            String path = currentNode.getProperty("compoundValue").getString();
            compoundValue = path.substring(path.lastIndexOf('/')).split("/")[1];
        }
    }

    public String getCompoundValue() {
        return compoundValue;
    }
}
