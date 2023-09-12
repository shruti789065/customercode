package com.adiacent.menarini.mhos.core.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContentFragmentElementValue {
    @SerializedName(":type")
    private String type;
    private List<Object> value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Object> getValue() {
        return value;
    }

    public void setValue(List<Object> value) {
        this.value = value;
    }
}
