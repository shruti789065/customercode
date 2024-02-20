package com.adiacent.menarini.menarinimaster.core.models.rss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsItemModel extends RssItemModel{
    private byte[] image;

    public byte[] getImage() {
        return image.clone();
    }

    public void setImage(byte[] image) {
        this.image = image.clone();
    }
}
