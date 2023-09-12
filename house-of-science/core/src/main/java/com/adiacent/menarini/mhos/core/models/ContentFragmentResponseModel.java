package com.adiacent.menarini.mhos.core.models;

import com.google.gson.annotations.SerializedName;

public class ContentFragmentResponseModel {
    @SerializedName("class")
    private String className;
    private ContentFragmentResponsePropertiesModel properties;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ContentFragmentResponsePropertiesModel getProperties() {
        return properties;
    }

    public void setProperties(ContentFragmentResponsePropertiesModel properties) {
        this.properties = properties;
    }
}
