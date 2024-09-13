package com.jakala.menarini.core.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class RegisteredUserServletDto {

    private String firstName;
    private String lastName;
    private Date birthDate;
    private String profession;
    private String phone;
    private String country;
    private String gender;
    private String linkedinProfile;
    private boolean privacyConsent;
    private boolean profilingConsent;
    private boolean newsletterConsent;
    private ArrayList<String> rolesNames;
    private ArrayList<String> interests;


    public RegisteredUserServletDto() {
        this.rolesNames = new ArrayList<>();
        this.interests = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLinkedinProfile() {
        return linkedinProfile;
    }

    public void setLinkedinProfile(String linkedinProfile) {
        this.linkedinProfile = linkedinProfile;
    }

    public boolean isPrivacyConsent() {
        return privacyConsent;
    }

    public void setPrivacyConsent(boolean privacyConsent) {
        this.privacyConsent = privacyConsent;
    }

    public boolean isProfilingConsent() {
        return profilingConsent;
    }

    public void setProfilingConsent(boolean profilingConsent) {
        this.profilingConsent = profilingConsent;
    }

    public boolean isNewsletterConsent() {
        return newsletterConsent;
    }

    public void setNewsletterConsent(boolean newsletterConsent) {
        this.newsletterConsent = newsletterConsent;
    }

    public ArrayList<String> getRolesNames() {
        return rolesNames;
    }

    public void setRolesNames(ArrayList<String> rolesNames) {
        this.rolesNames = rolesNames;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    public RegisteredUserDto generateRegisteredUserForUpdate() {
        RegisteredUserDto registeredUser = new RegisteredUserDto();
        registeredUser.setFirstname(this.getFirstName());
        registeredUser.setLastname(this.getLastName());
        registeredUser.setCountry(this.getCountry());
        registeredUser.setBirthDate(this.getBirthDate());
        registeredUser.setPhone(this.getPhone());
        registeredUser.setGender(this.getGender());
        registeredUser.setLinkedinProfile(this.getLinkedinProfile());
        registeredUser.setOccupation(this.getProfession());
        registeredUser.setProfilingConsent(this.convertBool(this.isProfilingConsent()));
        registeredUser.setPersonalDataProcessingConsent(this.convertBool(this.isPrivacyConsent()));
        registeredUser.setNewsletterSubscription(this.convertBool(this.isNewsletterConsent()));
        return registeredUser;
    }

    private String convertBool(boolean bool) {
        return bool ? "1" : "0";
    }
}
