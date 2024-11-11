package com.jakala.menarini.core.dto;

@SuppressWarnings("squid:S2384")
public class EventUserUpdateRequestDto {

    private int enrollmentId;
    private String[] dates;
    private String phone;

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
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
}
