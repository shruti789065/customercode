package com.adiacent.menarini.menarinimaster.core.models;

import com.google.gson.annotations.SerializedName;

public class ContentFragmentElementSingleValue {
    @SerializedName(":type")
    private String type;
    private Object value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
