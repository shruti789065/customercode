package com.adiacent.menarini.menarinimaster.core.models.contentfragments;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
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
        if(value == null)
            return null;
        Object[] array = value.toArray(new Object[value.size()]);
        Object[] clone = array.clone();
        return Arrays.asList(clone);
    }
    public void setValue(List<Object> value) {
        if(value== null)
            this.value = null;
        else {
            Object[] array =value.toArray(new Object[value.size()]);
            Object[] clone = array.clone();
            this.value = Arrays.asList(clone);
        }
    }
}
