package com.jakala.menarini.core.dto.cognito;

import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings("squid:S2384")
public class SignUpDto {

    private String firstName;
    private String lastName;
    private Date birthDate;
    private String password;
    private String email;
    private String profession;
    private String phone;
    private String country;
    private String gender;
    private String registrationStatus;
    private boolean privacyConsent;
    private boolean profilingConsent;
    private boolean newsletterConsent;
    private ArrayList<String> rolesNames;
    private ArrayList<String> interests;


    public SignUpDto() {
        super();
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getRegistrationStatus() {
        return registrationStatus;
    }
    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    public ArrayList<String> getRolesNames() {
        return rolesNames;
    }

    public void setRolesNames(ArrayList<String> rolesNames) {
        this.rolesNames = rolesNames;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


}
