package com.jakala.menarini.core.dto;

@SuppressWarnings("squid:S2384")
public class EventUserRequestDto {

    private String eventId;
    private String[] dates;
    private String phone;
    private String lang;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String[] getDates() {
        return dates;
    }

    public void setDates(String[] dates) {
        this.dates = dates;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
