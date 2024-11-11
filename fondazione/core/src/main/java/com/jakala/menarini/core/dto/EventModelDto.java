package com.jakala.menarini.core.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventModelDto {

    private String id;
    private String title;
    private String description;
    private String path;
    private Date startDate;
    private Date endDate;
    private String startDateText;
    private String endDateText;
    private String topics;
    private String eventType;
    private String location;
    private String presentationImage;
    private String subscription;

    public EventModelDto(
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
        this.id = id;
        this.title = title;
        this.description = description;
        this.path = path;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat stringFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            this.startDate = (startDateStr != null && !startDateStr.isEmpty()) ? dateFormat.parse(startDateStr) : null;
            this.endDate = (endDateStr != null && !endDateStr.isEmpty()) ? dateFormat.parse(endDateStr) : null;
            this.startDateText = (startDate != null) ? stringFormat.format(startDate) : null;
            this.endDateText = (endDate != null) ? stringFormat.format(endDate) : null;
        } catch (ParseException e) {
            this.startDate = null;
            this.endDate = null;
        }
        this.topics = topics;
        this.eventType = eventType;
        this.location = location;
        this.presentationImage = presentationImage;
        this.subscription = subscription;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPath() {
        return path;
    }

    public Date getStartDate() {
        return startDate != null ? new Date(startDate.getTime()) : null;
    }

    public Date getEndDate() {
        return endDate != null ? new Date(endDate.getTime()) : null;
    }

    public String getStartDateText() {
        return startDateText;
    }

    public String getEndDateText() {
        return endDateText;
    }

    public String getTopics() {
        return topics;
    }

    public String getEventType() {
        return eventType;
    }

    public String getLocation() {
        return location;
    }

    public String getPresentationImage() {
        return presentationImage;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
