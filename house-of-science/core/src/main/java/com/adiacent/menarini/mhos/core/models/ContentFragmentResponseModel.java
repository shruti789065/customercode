package com.adiacent.menarini.mhos.core.models;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class ContentFragmentResponseModel {
    @SerializedName("class")
    private List<String> className;
    private ContentFragmentResponsePropertiesModel properties;

    public List<String> getClassName() {
        if(className == null)
            return null;
        String[] array = className.toArray(new String[className.size()]);
        String[] clone = array.clone();
        return Arrays.asList(clone);
    }
    public void setClassName(List<String> value) {
        if(value== null)
            this.className = null;
        else {
            String[] array =value.toArray(new String[value.size()]);
            String[] clone = array.clone();
            this.className = Arrays.asList(clone);
        }
    }

    public ContentFragmentResponsePropertiesModel getProperties() {
        return properties;
    }

    public void setProperties(ContentFragmentResponsePropertiesModel properties) {
        this.properties = properties;
    }
}
