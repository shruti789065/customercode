package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RegisteredUserDtoTest {

    @Test
    void testGettersAndSetters() {
        RegisteredUserDto dto = new RegisteredUserDto();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date birthDate = new Date();

        dto.setId(1L);
        dto.setBirthDate(birthDate);
        dto.setCountry("Country");
        dto.setCreatedOn(timestamp);
        dto.setEmail("email@example.com");
        dto.setFirstname("First Name");
        dto.setGender("Gender");
        dto.setLastUpdatedOn(timestamp);
        dto.setLastname("Last Name");
        dto.setLegacyId(123);
        dto.setLinkedinProfile("LinkedIn Profile");
        dto.setNewsletterSubscription("Subscribed");
        dto.setNewsletterSubscriptionTs(timestamp);
        dto.setOccupation("Occupation");
        dto.setPersonalDataProcessingConsent("Yes");
        dto.setPersonalDataProcessingConsentTs(timestamp);
        dto.setPhone("1234567890");
        dto.setProfilingConsent("Yes");
        dto.setProfilingConsentTs(timestamp);
        dto.setTaxIdCode("Tax ID");
        dto.setRegistrationStatus("Active");
        dto.setUsername("Username");

        assertEquals(1L, dto.getId());
        assertEquals(birthDate, dto.getBirthDate());
        assertEquals("Country", dto.getCountry());
        assertEquals(timestamp, dto.getCreatedOn());
        assertEquals("email@example.com", dto.getEmail());
        assertEquals("First Name", dto.getFirstname());
        assertEquals("Gender", dto.getGender());
        assertEquals(timestamp, dto.getLastUpdatedOn());
        assertEquals("Last Name", dto.getLastname());
        assertEquals(123, dto.getLegacyId());
        assertEquals("LinkedIn Profile", dto.getLinkedinProfile());
        assertEquals("Subscribed", dto.getNewsletterSubscription());
        assertEquals(timestamp, dto.getNewsletterSubscriptionTs());
        assertEquals("Occupation", dto.getOccupation());
        assertEquals("Yes", dto.getPersonalDataProcessingConsent());
        assertEquals(timestamp, dto.getPersonalDataProcessingConsentTs());
        assertEquals("1234567890", dto.getPhone());
        assertEquals("Yes", dto.getProfilingConsent());
        assertEquals(timestamp, dto.getProfilingConsentTs());
        assertEquals("Tax ID", dto.getTaxIdCode());
        assertEquals("Active", dto.getRegistrationStatus());
        assertEquals("Username", dto.getUsername());
    }
}