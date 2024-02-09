package com.adiacent.menarini.menarinimaster.core.models.rssblog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelModel {

    private String title;
    private String link;

    private String description;

    private RssBlogItemModel item;
    private List<RssBlogItemModel> items = new ArrayList<RssBlogItemModel>();


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

    public RssBlogItemModel getItem() {
        return item;
    }

    public void setItem(RssBlogItemModel item) {
        this.item = item;
        items.add(this.item);
    }

    public List<RssBlogItemModel> getItems() {
        if(items == null)
            return null;
        RssBlogItemModel[] array = items.toArray(new RssBlogItemModel[items.size()]);
        RssBlogItemModel[] clone = array.clone();
        return Arrays.asList(clone);
    }

    public void setItems(List<RssBlogItemModel> items) {

        if(items== null)
            this.items = null;
        else {
            RssBlogItemModel[] array = items.toArray(new RssBlogItemModel[items.size()]);
            RssBlogItemModel[] clone = array.clone();
            this.items = Arrays.asList(clone);
        }
    }
}
