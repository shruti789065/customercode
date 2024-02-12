package com.adiacent.menarini.menarinimaster.core.models.rssnews;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelModel {

    private String title;
    private String link;

    private String description;

    private RssItemModel item;
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
        if(items == null)
            return null;
        RssItemModel[] array = items.toArray(new RssItemModel[items.size()]);
        RssItemModel[] clone = array.clone();
        return Arrays.asList(clone);
    }

    public void setItems(List<RssItemModel> items) {

        if(items== null)
            this.items = null;
        else {
            RssItemModel[] array = items.toArray(new RssItemModel[items.size()]);
            RssItemModel[] clone = array.clone();
            this.items = Arrays.asList(clone);
        }
    }
}
