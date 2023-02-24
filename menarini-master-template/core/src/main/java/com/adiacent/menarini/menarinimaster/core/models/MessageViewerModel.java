package com.adiacent.menarini.menarinimaster.core.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class })
public class MessageViewerModel {

    @Inject
    private Page currentPage;

    @Inject
    private Node currentNode;

    @Inject
    private String reqattrname;
    private List<String> messageList = null;

    @Self
    private SlingHttpServletRequest request;

    @PostConstruct
    protected void init() throws RepositoryException {

        this.messageList = (List<String>)request.getSession().getAttribute(reqattrname);

    }

    public List<String> getMessageList() {
        return messageList;
    }
}
