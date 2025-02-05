package com.jakala.menarini.core.dto;

@SuppressWarnings("squid:S2384")
public class EventDetailDto extends EventModelDto{

    public EventDetailDto(
             String id,
             String title,
             String description,
             String path,
             String startDateStr,
             String endDateStr,
             String topics,
             String eventType,
             String location,
             String presentationImage,
             String subscription
    ) {
        super(id, title, description, path, startDateStr, endDateStr, topics, eventType, location, presentationImage, subscription);
    }
    
    private String presentationDescription;
    private String nation;
    private String [] speakers;
    private String [] topicList;

    public String getPresentationDescription() {
        return presentationDescription;
    }

    public void setPresentationDescription(String presentationDescription) {
        this.presentationDescription = presentationDescription;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public void setTopicList(String[] topicList) {
        this.topicList = topicList;
    }

    public String[] getTopicList() {
        return topicList;
    }

    public String[] getSpeakers() {
        return speakers;
    }

    public void setSpeakers(String[] speakers) {
        this.speakers = speakers;
    }

    
}
