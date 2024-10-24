package com.jakala.menarini.core.entities.records;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RegisteredUserRecordTest {

    private RegisteredUserRecord record;

    @BeforeEach
    void setUp() {
        record = new RegisteredUserRecord();
    }

    @Test
    void testSettersAndGetters() {
        Long id = 1L;
        String lastname = "Doe";
        String firstname = "John";
        String email = "john.doe@example.com";
        String username = "johndoe";
        String occupation = "Developer";
        String gender = "Male";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        String phone = "1234567890";
        String taxIdCode = "TAX123";
        String linkedinProfile = "john.doe";
        String country = "USA";
        String personalDataProcessingConsent = "Yes";
        LocalDateTime personalDataProcessingConsentTs = LocalDateTime.now();
        String profilingConsent = "Yes";
        LocalDateTime profilingConsentTs = LocalDateTime.now();
        String newsletterSubscription = "Yes";
        LocalDateTime newsletterSubscriptionTs = LocalDateTime.now();
        Integer legacyId = 100;
        String registrationStatus = "Active";
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime lastUpdatedOn = LocalDateTime.now();

        record.setId(id);
        record.setLastname(lastname);
        record.setFirstname(firstname);
        record.setEmail(email);
        record.setUsername(username);
        record.setOccupation(occupation);
        record.setGender(gender);
        record.setBirthDate(birthDate);
        record.setPhone(phone);
        record.setTaxIdCode(taxIdCode);
        record.setLinkedinProfile(linkedinProfile);
        record.setCountry(country);
        record.setPersonalDataProcessingConsent(personalDataProcessingConsent);
        record.setPersonalDataProcessingConsentTs(personalDataProcessingConsentTs);
        record.setProfilingConsent(profilingConsent);
        record.setProfilingConsentTs(profilingConsentTs);
        record.setNewsletterSubscription(newsletterSubscription);
        record.setNewsletterSubscriptionTs(newsletterSubscriptionTs);
        record.setLegacyId(legacyId);
        record.setRegistrationStatus(registrationStatus);
        record.setCreatedOn(createdOn);
        record.setLastUpdatedOn(lastUpdatedOn);

        assertEquals(id, record.getId());
        assertEquals(lastname, record.getLastname());
        assertEquals(firstname, record.getFirstname());
        assertEquals(email, record.getEmail());
        assertEquals(username, record.getUsername());
        assertEquals(occupation, record.getOccupation());
        assertEquals(gender, record.getGender());
        assertEquals(birthDate, record.getBirthDate());
        assertEquals(phone, record.getPhone());
        assertEquals(taxIdCode, record.getTaxIdCode());
        assertEquals(linkedinProfile, record.getLinkedinProfile());
        assertEquals(country, record.getCountry());
        assertEquals(personalDataProcessingConsent, record.getPersonalDataProcessingConsent());
        assertEquals(personalDataProcessingConsentTs, record.getPersonalDataProcessingConsentTs());
        assertEquals(profilingConsent, record.getProfilingConsent());
        assertEquals(profilingConsentTs, record.getProfilingConsentTs());
        assertEquals(newsletterSubscription, record.getNewsletterSubscription());
        assertEquals(newsletterSubscriptionTs, record.getNewsletterSubscriptionTs());
        assertEquals(legacyId, record.getLegacyId());
        assertEquals(registrationStatus, record.getRegistrationStatus());
        assertEquals(createdOn, record.getCreatedOn());
        assertEquals(lastUpdatedOn, record.getLastUpdatedOn());
    }

    @Test
    void testConstructor() {
        Long id = 1L;
        String lastname = "Doe";
        String firstname = "John";
        String email = "john.doe@example.com";
        String username = "johndoe";
        String occupation = "Developer";
        String gender = "Male";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        String phone = "1234567890";
        String taxIdCode = "TAX123";
        String linkedinProfile = "john.doe";
        String country = "USA";
        String personalDataProcessingConsent = "Yes";
        LocalDateTime personalDataProcessingConsentTs = LocalDateTime.now();
        String profilingConsent = "Yes";
        LocalDateTime profilingConsentTs = LocalDateTime.now();
        String newsletterSubscription = "Yes";
        LocalDateTime newsletterSubscriptionTs = LocalDateTime.now();
        Integer legacyId = 100;
        String registrationStatus = "Active";
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime lastUpdatedOn = LocalDateTime.now();

        RegisteredUserRecord record = new RegisteredUserRecord(id, lastname, firstname, email, username, occupation, gender, birthDate, phone, taxIdCode, linkedinProfile, country, personalDataProcessingConsent, personalDataProcessingConsentTs, profilingConsent, profilingConsentTs, newsletterSubscription, newsletterSubscriptionTs, legacyId, registrationStatus, createdOn, lastUpdatedOn);

        assertEquals(id, record.getId());
        assertEquals(lastname, record.getLastname());
        assertEquals(firstname, record.getFirstname());
        assertEquals(email, record.getEmail());
        assertEquals(username, record.getUsername());
        assertEquals(occupation, record.getOccupation());
        assertEquals(gender, record.getGender());
        assertEquals(birthDate, record.getBirthDate());
        assertEquals(phone, record.getPhone());
        assertEquals(taxIdCode, record.getTaxIdCode());
        assertEquals(linkedinProfile, record.getLinkedinProfile());
        assertEquals(country, record.getCountry());
        assertEquals(personalDataProcessingConsent, record.getPersonalDataProcessingConsent());
        assertEquals(personalDataProcessingConsentTs, record.getPersonalDataProcessingConsentTs());
        assertEquals(profilingConsent, record.getProfilingConsent());
        assertEquals(profilingConsentTs, record.getProfilingConsentTs());
        assertEquals(newsletterSubscription, record.getNewsletterSubscription());
        assertEquals(newsletterSubscriptionTs, record.getNewsletterSubscriptionTs());
        assertEquals(legacyId, record.getLegacyId());
        assertEquals(registrationStatus, record.getRegistrationStatus());
        assertEquals(createdOn, record.getCreatedOn());
        assertEquals(lastUpdatedOn, record.getLastUpdatedOn());
    }

    @Test
    void testValuesMethod() {
        Long id = 1L;
        String lastname = "Doe";
        String firstname = "John";
        String email = "john.doe@example.com";
        String username = "johndoe";
        String occupation = "Developer";
        String gender = "Male";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        String phone = "1234567890";
        String taxIdCode = "TAX123";
        String linkedinProfile = "john.doe";
        String country = "USA";
        String personalDataProcessingConsent = "Yes";
        LocalDateTime personalDataProcessingConsentTs = LocalDateTime.now();
        String profilingConsent = "Yes";
        LocalDateTime profilingConsentTs = LocalDateTime.now();
        String newsletterSubscription = "Yes";
        LocalDateTime newsletterSubscriptionTs = LocalDateTime.now();
        Integer legacyId = 100;
        String registrationStatus = "Active";
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime lastUpdatedOn = LocalDateTime.now();

        record.values(id, lastname, firstname, email, username, occupation, gender, birthDate, phone, taxIdCode, linkedinProfile, country, personalDataProcessingConsent, personalDataProcessingConsentTs, profilingConsent, profilingConsentTs, newsletterSubscription, newsletterSubscriptionTs, legacyId, registrationStatus, createdOn, lastUpdatedOn);

        assertEquals(id, record.getId());
        assertEquals(lastname, record.getLastname());
        assertEquals(firstname, record.getFirstname());
        assertEquals(email, record.getEmail());
        assertEquals(username, record.getUsername());
        assertEquals(occupation, record.getOccupation());
        assertEquals(gender, record.getGender());
        assertEquals(birthDate, record.getBirthDate());
        assertEquals(phone, record.getPhone());
        assertEquals(taxIdCode, record.getTaxIdCode());
        assertEquals(linkedinProfile, record.getLinkedinProfile());
        assertEquals(country, record.getCountry());
        assertEquals(personalDataProcessingConsent, record.getPersonalDataProcessingConsent());
        assertEquals(personalDataProcessingConsentTs, record.getPersonalDataProcessingConsentTs());
        assertEquals(profilingConsent, record.getProfilingConsent());
        assertEquals(profilingConsentTs, record.getProfilingConsentTs());
        assertEquals(newsletterSubscription, record.getNewsletterSubscription());
        assertEquals(newsletterSubscriptionTs, record.getNewsletterSubscriptionTs());
        assertEquals(legacyId, record.getLegacyId());
        assertEquals(registrationStatus, record.getRegistrationStatus());
        assertEquals(createdOn, record.getCreatedOn());
        assertEquals(lastUpdatedOn, record.getLastUpdatedOn());
    }
}