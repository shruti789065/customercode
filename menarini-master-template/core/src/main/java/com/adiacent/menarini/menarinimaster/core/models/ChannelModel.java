package com.adiacent.menarini.menarinimaster.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelModel {

    private String title;
    private String link;

    private String description;

    private  RssItemModel  item;
    private List<RssItemModel> items = new ArrayList<RssItemModel>();


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RssItemModel getItem() {
        return item;
    }

    public void setItem(RssItemModel item) {
        this.item = item;
        items.add(this.item);
    }

    public List<RssItemModel> getItems() {
        return items;
    }

    public void setItems(List<RssItemModel> items) {
        this.items = items;
    }
}
