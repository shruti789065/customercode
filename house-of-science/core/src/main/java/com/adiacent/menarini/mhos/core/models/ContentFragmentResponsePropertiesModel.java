package com.adiacent.menarini.mhos.core.models;

import com.google.gson.annotations.SerializedName;

public class ContentFragmentResponsePropertiesModel {

    private String path;
    private String location;
    private String parentLocation;
    @SerializedName("status.code")
    private Integer statusCode;
    @SerializedName("status.message")
    private String statusMessage;
    private String referer;
    private Boolean isCreated;
    private String title;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getParentLocation() {
        return parentLocation;
    }

    public void setParentLocation(String parentLocation) {
        this.parentLocation = parentLocation;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public Boolean getCreated() {
        return isCreated;
    }

    public void setCreated(Boolean created) {
        isCreated = created;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
