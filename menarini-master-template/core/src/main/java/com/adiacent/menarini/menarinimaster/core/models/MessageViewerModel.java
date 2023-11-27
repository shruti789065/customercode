package com.adiacent.menarini.menarinimaster.core.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class })
public class MessageViewerModel {

    @Inject
    @Optional
    private String sessionattrname;
    private List<String> messageList = null;

    @Self
    private SlingHttpServletRequest request;

    @PostConstruct
    protected void init() throws RepositoryException {

        this.messageList = null;//reset
        if(StringUtils.isNotBlank(sessionattrname))
            this.messageList = (List<String>)request.getSession().getAttribute(sessionattrname);

    }

    public List<String> getMessageList() {
        if(messageList != null) {
            return new ArrayList<>(messageList);
        }else return null;
    }
}
