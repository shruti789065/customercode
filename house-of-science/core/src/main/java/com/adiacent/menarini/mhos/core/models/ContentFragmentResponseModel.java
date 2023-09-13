package com.adiacent.menarini.mhos.core.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContentFragmentResponseModel {
    @SerializedName("class")
    private List<String> className;
    private ContentFragmentResponsePropertiesModel properties;

    public List<String> getClassName() {
        return className;
    }

    public void setClassName(List<String> className) {
        this.className = className;
    }

    public ContentFragmentResponsePropertiesModel getProperties() {
        return properties;
    }

    public void setProperties(ContentFragmentResponsePropertiesModel properties) {
        this.properties = properties;
    }
}
