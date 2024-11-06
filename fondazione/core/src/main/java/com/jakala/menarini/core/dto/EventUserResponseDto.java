package com.jakala.menarini.core.dto;

import java.sql.Timestamp;
import java.util.List;

@SuppressWarnings("squid:S2384")
public class EventUserResponseDto {

    private long id;
    private long eventId;
    private boolean isResidential;
    private Timestamp registerOn;
    private List<Timestamp> dates;
    private String phone;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public Timestamp getRegisterOn() {
        return registerOn;
    }

    public void setRegisterOn(Timestamp registerOn) {
        this.registerOn = registerOn;
    }

    public boolean isResidential() {
        return isResidential;
    }

    public void setResidential(boolean residential) {
        isResidential = residential;
    }

    public List<Timestamp> getDates() {
        return dates;
    }

    public void setDates(List<Timestamp> dates) {
        this.dates = dates;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
