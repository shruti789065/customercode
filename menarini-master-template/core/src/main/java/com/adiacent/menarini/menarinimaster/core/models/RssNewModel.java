package com.adiacent.menarini.menarinimaster.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("rss")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RssNewModel {

    private ChannelModel channel;

    public ChannelModel getChannel() {
        return channel;
    }

    public void setChannel(ChannelModel channel) {
        this.channel = channel;
    }
}
