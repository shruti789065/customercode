package com.adiacent.menarini.mhos.core.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContentFragmentElements {

    private ContentFragmentElementSingleValue linkLabel;
    private ContentFragmentElementValue typology;
    private ContentFragmentElementValue author;
    private ContentFragmentElementValue topic;
    private ContentFragmentElementSingleValue description;
    private ContentFragmentElementValue source;
    private ContentFragmentElementValue tags;
    private ContentFragmentElementSingleValue link;
    private ContentFragmentElementSingleValue articleData;
    private ContentFragmentElementSingleValue linkTarget;



    public ContentFragmentElementValue getTypology() {
        return typology;
    }

    public void setTypology(ContentFragmentElementValue typology) {
        this.typology = typology;
    }

    public ContentFragmentElementValue getAuthor() {
        return author;
    }

    public void setAuthor(ContentFragmentElementValue author) {
        this.author = author;
    }

    public ContentFragmentElementValue getTopic() {
        return topic;
    }

    public void setTopic(ContentFragmentElementValue topic) {
        this.topic = topic;
    }



    public ContentFragmentElementValue getSource() {
        return source;
    }

    public void setSource(ContentFragmentElementValue source) {
        this.source = source;
    }

    public ContentFragmentElementValue getTags() {
        return tags;
    }

    public void setTags(ContentFragmentElementValue tags) {
        this.tags = tags;
    }

    public ContentFragmentElementSingleValue getLink() {
        return link;
    }

    public void setLink(ContentFragmentElementSingleValue link) {
        this.link = link;
    }

    public ContentFragmentElementSingleValue getDescription() {
        return description;
    }

    public void setDescription(ContentFragmentElementSingleValue description) {
        this.description = description;
    }

    public ContentFragmentElementSingleValue getArticleData() {
        return articleData;
    }

    public void setArticleData(ContentFragmentElementSingleValue articleData) {
        this.articleData = articleData;
    }

    public ContentFragmentElementSingleValue getLinkLabel() {
        return linkLabel;
    }

    public void setLinkLabel(ContentFragmentElementSingleValue linkLabel) {
        this.linkLabel = linkLabel;
    }

    public ContentFragmentElementSingleValue getLinkTarget() {
        return linkTarget;
    }

    public void setLinkTarget(ContentFragmentElementSingleValue linkTarget) {
        this.linkTarget = linkTarget;
    }
}
