package com.adiacent.menarini.menarinimaster.core.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class NewsDateModel {

	@Inject
	private Page currentPage;

	@Inject
	private Node currentNode;

	private String formattedValue = "";

	private Calendar date;

	@Inject

	@PostConstruct
	protected void init() throws RepositoryException {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy - MM - dd");
		if (currentNode.hasProperty("newsDate")) {
			date = currentNode.getProperty("newsDate").getDate();
		} else {
			ValueMap properties = currentPage.getProperties();
			String lastReplicationAction = properties.containsKey(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION) ? properties.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION, String.class) : "";
			if (lastReplicationAction != null && !lastReplicationAction.isEmpty() && lastReplicationAction.equals("Activate")) {
				date = properties.containsKey(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED) ? properties.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED, Calendar.class) : null;
			} else {
				date = properties.containsKey(JcrConstants.JCR_CREATED) ? properties.get(JcrConstants.JCR_CREATED, Calendar.class) : null;
              /* if (date == null) {
                   // Se la data di creazione non è disponibile, imposta la data corrente come data di creazione
                   date = Calendar.getInstance();
               }
               currentNode.setProperty("newsDate", date);
               currentNode.getSession().save();
               date = currentNode.getProperty("newsDate").getDate();*/
			}

		}
		if (date != null) {
			formattedValue = formatter.format(date.getTime());
		}
	}

	public String getFormattedValue() {
		return formattedValue;
	}
}
