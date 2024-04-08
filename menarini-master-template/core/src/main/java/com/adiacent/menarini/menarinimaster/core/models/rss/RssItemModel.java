package com.adiacent.menarini.menarinimaster.core.models.rss;

import com.adiacent.menarini.menarinimaster.core.models.contentfragments.EnclosureModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
@JsonIgnoreProperties(ignoreUnknown = true)
public class RssItemModel {
    protected String guid;
    @JsonProperty("dc_creator")
    protected String creator;
    protected String link;
    protected String title;
    protected EnclosureModel enclosure;
    protected String description;
    protected String identifier;
    protected Date pubDate;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EnclosureModel getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(EnclosureModel enclosure) {
        this.enclosure = enclosure;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }
}
