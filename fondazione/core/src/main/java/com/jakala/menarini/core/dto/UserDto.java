package com.jakala.menarini.core.dto;

import java.sql.Timestamp;
import java.util.Date;

public class UserDto {
    
    private long id;
    private String lastname;
    private String firstname;
    private String email;
    private String username;
    private String occupation;
    private String gender;
    private Date birthDate;
    private String phone;
    private String taxIdCode;
    private String linkedinProfile;
    private String country;
    private char personalDataProcessingConsent;
    private Timestamp personalDataProcessingConsentTs;
    private char profilingConsent;
    private Timestamp profilingConsentTs;
    private char newsletterSubscription;
    private Timestamp newsletterSubscriptionTs;
    private int legacyId;
    private Timestamp createdOn;
    private Timestamp lastUpdatedOn;


    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOccupation() {
        return this.occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTaxIdCode() {
        return this.taxIdCode;
    }

    public void setTaxIdCode(String taxIdCode) {
        this.taxIdCode = taxIdCode;
    }

    public String getLinkedinProfile() {
        return this.linkedinProfile;
    }

    public void setLinkedinProfile(String linkedinProfile) {
        this.linkedinProfile = linkedinProfile;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public char getPersonalDataProcessingConsent() {
        return this.personalDataProcessingConsent;
    }

    public void setPersonalDataProcessingConsent(char personalDataProcessingConsent) {
        this.personalDataProcessingConsent = personalDataProcessingConsent;
    }

    public Timestamp getPersonalDataProcessingConsentTs() {
        return this.personalDataProcessingConsentTs;
    }

    public void setPersonalDataProcessingConsentTs(Timestamp personalDataProcessingConsentTs) {
        this.personalDataProcessingConsentTs = personalDataProcessingConsentTs;
    }

    public char getProfilingConsent() {
        return this.profilingConsent;
    }

    public void setProfilingConsent(char profilingConsent) {
        this.profilingConsent = profilingConsent;
    }

    public Timestamp getProfilingConsentTs() {
        return this.profilingConsentTs;
    }

    public void setProfilingConsentTs(Timestamp profilingConsentTs) {
        this.profilingConsentTs = profilingConsentTs;
    }

    public char getNewsletterSubscription() {
        return this.newsletterSubscription;
    }

    public void setNewsletterSubscription(char newsletterSubscription) {
        this.newsletterSubscription = newsletterSubscription;
    }

    public Timestamp getNewsletterSubscriptionTs() {
        return this.newsletterSubscriptionTs;
    }

    public void setNewsletterSubscriptionTs(Timestamp newsletterSubscriptionTs) {
        this.newsletterSubscriptionTs = newsletterSubscriptionTs;
    }

    public int getLegacyId() {
        return this.legacyId;
    }

    public void setLegacyId(int legacyId) {
        this.legacyId = legacyId;
    }

    public Timestamp getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getLastUpdatedOn() {
        return this.lastUpdatedOn;
    }

    public void setLastUpdatedOn(Timestamp lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    
}
