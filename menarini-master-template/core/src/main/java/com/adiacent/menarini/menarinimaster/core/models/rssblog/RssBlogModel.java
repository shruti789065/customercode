package com.adiacent.menarini.menarinimaster.core.models.rssblog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("rss")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RssBlogModel {

    private ChannelModel channel;

    public ChannelModel getChannel() {
        return channel;
    }

    public void setChannel(ChannelModel channel) {
        this.channel = channel;
    }
}
