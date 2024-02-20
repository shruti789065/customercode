package com.adiacent.menarini.menarinimaster.core.models.contentfragments;

import com.google.gson.annotations.SerializedName;

public class ContentFragmentPropertiesM<T> {

    @SerializedName("cq:model")
    private Object cqModel;
    private String title;
    private String description;

    private T elements;

    public Object getCqModel() {
        return cqModel;
    }

    public void setCqModel(Object cqModel) {
        this.cqModel = cqModel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getElements() {
        return elements;
    }

    public void setElements(T elements) {
        this.elements = elements;
    }

    public static class CQModel
    {
        String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
