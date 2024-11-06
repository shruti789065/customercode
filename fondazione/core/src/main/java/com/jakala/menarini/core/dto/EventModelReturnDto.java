package com.jakala.menarini.core.dto;

import java.util.List;

@SuppressWarnings("squid:S2384")
public class EventModelReturnDto {

    private List<EventModelDto> events;
    private int totalMatches;

    public List<EventModelDto> getEvents() {
        return events;
    }

    public void setEvents(List<EventModelDto> events) {
        this.events = events;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
    }
}
