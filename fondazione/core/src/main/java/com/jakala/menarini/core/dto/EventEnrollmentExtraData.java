package com.jakala.menarini.core.dto;

import java.sql.Timestamp;
import java.util.List;

public class EventEnrollmentExtraData {

    private List<Timestamp> enrollmentDates;
    private String phone;

    public List<Timestamp> getEnrollmentDates() {
        return enrollmentDates;
    }

    public void setEnrollmentDates(List<Timestamp> enrollmentDates) {
        this.enrollmentDates = enrollmentDates;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
