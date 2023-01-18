package com.adiacent.menarini.menarinimaster.core.models;

import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;

import java.util.Calendar;
public class CustomTabsListItem implements ListItem {

	private final com.adobe.cq.wcm.core.components.models.ListItem wrappedListItem;

	public CustomTabsListItem( final com.adobe.cq.wcm.core.components.models.ListItem listItem) {

		this.wrappedListItem = listItem;
	}


	@Override
	public String getTitle() {
		return wrappedListItem.getTitle();}

	@Override
	public String getDescription() {
		return wrappedListItem.getDescription();
	}

	@Override
	public String getURL() {
		return wrappedListItem.getURL();
	}
 	@Override
	public String getId() {
		return "megamenu-"+wrappedListItem.getId();
	}

	@Override
	public Link getLink() {
		return wrappedListItem.getLink();
	}

	@Override
	public Calendar getLastModified() {
		return wrappedListItem.getLastModified();
	}

	@Override
	public String getPath() {
		return wrappedListItem.getPath();
	}

	@Override
	public String getName() {
		return wrappedListItem.getName();
	}

	@Override
	public Resource getTeaserResource() {
		return wrappedListItem.getTeaserResource();
	}

	@Override
	public ComponentData getData() {
		return wrappedListItem.getData();
	}
}