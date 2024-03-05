package com.adiacent.menarini.menarinimaster.core.models.rss;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlogItemModel extends RssItemModel{
    private String category;

    private List<String> categories = new ArrayList<String>();
    @JsonProperty("content_encoded")
    private String content;

    @JsonProperty("wfw_commentRss")
    private String commentRss;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        this.categories.add(this.category);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentRss() {
        return commentRss;
    }

    public void setCommentRss(String commentRss) {
        this.commentRss = commentRss;
    }

    public List<String> getCategories() {
        if(categories == null)
            return null;
        String[] array = categories.toArray(new String[categories.size()]);
        String[] clone = array.clone();
        return Arrays.asList(clone);
    }

    public void setCategories(List<String> categories) {

        if(categories== null)
            this.categories = null;
        else {
            String[] array = categories.toArray(new String[categories.size()]);
            String[] clone = array.clone();
            this.categories = Arrays.asList(clone);
        }
    }
}
