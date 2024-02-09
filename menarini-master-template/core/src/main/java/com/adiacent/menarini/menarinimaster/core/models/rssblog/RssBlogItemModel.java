package com.adiacent.menarini.menarinimaster.core.models.rssblog;

import com.adiacent.menarini.menarinimaster.core.models.EnclosureModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RssBlogItemModel {
    private String guid;
    @JsonProperty("dc_creator")
    private String creator;
    private String link;
    private String title;
    private EnclosureModel enclosure;
    private String description;
    private String identifier;
    private Date pubDate;

    private String category;

    private List<String> categories = new ArrayList<String>();
    @JsonProperty("content_encoded")
    private String content;

    @JsonProperty("wfw_commentRss")
    private String commentRss;



    private byte[] image;

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
        return (Date) pubDate.clone();
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = (Date) pubDate.clone();
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public byte[] getImage() {
        return image.clone();
    }

    public void setImage(byte[] image) {
        this.image = image.clone();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        this.categories.add(this.category);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentRss() {
        return commentRss;
    }

    public void setCommentRss(String commentRss) {
        this.commentRss = commentRss;
    }

    public List<String> getCategories() {
        if(categories == null)
            return null;
        String[] array = categories.toArray(new String[categories.size()]);
        String[] clone = array.clone();
        return Arrays.asList(clone);
    }

    public void setCategories(List<String> categories) {

        if(categories== null)
            this.categories = null;
        else {
            String[] array = categories.toArray(new String[categories.size()]);
            String[] clone = array.clone();
            this.categories = Arrays.asList(clone);
        }
    }
}
