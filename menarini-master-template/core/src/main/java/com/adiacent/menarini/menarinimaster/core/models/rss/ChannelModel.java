package com.adiacent.menarini.menarinimaster.core.models.rss;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelModel<T> {
    private String title;
    private String link;

    private String description;
    private T item;
    private List<T> items = new ArrayList<T>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
        items.add(this.item);
    }

    public List<T> getItems() {
        if(items == null)
            return null;
        T[] array = (T[]) this.items.toArray();
        T[] clone = array.clone();
        return Arrays.asList(clone);
    }

    public void setItems(List<T> items) {

        if(items == null)
            this.items = null;
        else {
            T[] array = (T[]) items.toArray();
            T[] clone = array.clone();
            this.items = Arrays.asList(clone);
        }
    }

    /*public static <T> T[] toArray(List<T> list) {


         T[] toR = (T[]) java.lang.reflect.Array.newInstance(list.get(0)
                    .getClass(), list.size());
            for (int i = 0; i < list.size(); i++) {
                toR[i] = list.get(i);
            }
            return toR;

    }*/
}
