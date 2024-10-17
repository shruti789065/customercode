package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class NewsletterSubscriptionDtoTest {

    @Test
    void testGettersAndSetters() {
        NewsletterSubscriptionDto dto = new NewsletterSubscriptionDto();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dto.setId(1L);
        dto.setCreatedOn(timestamp);
        dto.setEmail("email@example.com");
        dto.setFirstname("First Name");
        dto.setLastUpdatedOn(timestamp);
        dto.setLastname("Last Name");
        dto.setNewsletterSubscription("Subscribed");
        dto.setNewsletterSubscriptionTs(timestamp);
        dto.setOccupation("Occupation");
        dto.setPersonalDataProcessingConsent("Yes");
        dto.setPersonalDataProcessingConsentTs(timestamp);
        RegisteredUserDto registeredUser = new RegisteredUserDto();
        dto.setRegisteredUser(registeredUser);

        assertEquals(1L, dto.getId());
        assertEquals(timestamp, dto.getCreatedOn());
        assertEquals("email@example.com", dto.getEmail());
        assertEquals("First Name", dto.getFirstname());
        assertEquals(timestamp, dto.getLastUpdatedOn());
        assertEquals("Last Name", dto.getLastname());
        assertEquals("Subscribed", dto.getNewsletterSubscription());
        assertEquals(timestamp, dto.getNewsletterSubscriptionTs());
        assertEquals("Occupation", dto.getOccupation());
        assertEquals("Yes", dto.getPersonalDataProcessingConsent());
        assertEquals(timestamp, dto.getPersonalDataProcessingConsentTs());
        assertEquals(registeredUser, dto.getRegisteredUser());
    }
}