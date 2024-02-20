package com.adiacent.menarini.menarinimaster.core.models.rss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("rss")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RssModel<T> {
    private ChannelModel<T> channel;

    public ChannelModel<T> getChannel() {
        return channel;
    }

    public void setChannel(ChannelModel<T> channel) {
        this.channel = channel;
    }
}

