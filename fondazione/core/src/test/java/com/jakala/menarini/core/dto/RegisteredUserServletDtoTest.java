package com.jakala.menarini.core.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RegisteredUserServletDtoTest {

    @Test
    void testGettersAndSetters() {
        RegisteredUserServletDto dto = new RegisteredUserServletDto();
        Date birthDate = new Date();

        dto.setFirstName("First Name");
        dto.setLastName("Last Name");
        dto.setBirthDate(birthDate);
        dto.setProfession("Profession");
        dto.setPhone("1234567890");
        dto.setCountry("Country");
        dto.setGender("Gender");
        dto.setLinkedinProfile("LinkedIn Profile");
        dto.setPrivacyConsent(true);
        dto.setProfilingConsent(true);
        dto.setNewsletterConsent(true);
        ArrayList<String> rolesNames = new ArrayList<>();
        rolesNames.add("Role1");
        dto.setRolesNames(rolesNames);
        ArrayList<String> interests = new ArrayList<>();
        interests.add("Interest1");
        dto.setInterests(interests);

        assertEquals("First Name", dto.getFirstName());
        assertEquals("Last Name", dto.getLastName());
        assertEquals(birthDate, dto.getBirthDate());
        assertEquals("Profession", dto.getProfession());
        assertEquals("1234567890", dto.getPhone());
        assertEquals("Country", dto.getCountry());
        assertEquals("Gender", dto.getGender());
        assertEquals("LinkedIn Profile", dto.getLinkedinProfile());
        assertTrue(dto.isPrivacyConsent());
        assertTrue(dto.isProfilingConsent());
        assertTrue(dto.isNewsletterConsent());
        assertEquals(rolesNames, dto.getRolesNames());
        assertEquals(interests, dto.getInterests());
    }

    @Test
    void testGenerateRegisteredUserForUpdate() {
        RegisteredUserServletDto dto = new RegisteredUserServletDto();
        dto.setFirstName("First Name");
        dto.setLastName("Last Name");
        dto.setCountry("Country");
        dto.setBirthDate(new Date());
        dto.setPhone("1234567890");
        dto.setGender("Gender");
        dto.setLinkedinProfile("LinkedIn Profile");
        dto.setProfession("Profession");
        dto.setPrivacyConsent(true);
        dto.setProfilingConsent(true);
        dto.setNewsletterConsent(true);

        RegisteredUserDto registeredUser = dto.generateRegisteredUserForUpdate();

        assertEquals(dto.getFirstName(), registeredUser.getFirstname());
        assertEquals(dto.getLastName(), registeredUser.getLastname());
        assertEquals(dto.getCountry(), registeredUser.getCountry());
        assertEquals(dto.getBirthDate(), registeredUser.getBirthDate());
        assertEquals(dto.getPhone(), registeredUser.getPhone());
        assertEquals(dto.getGender(), registeredUser.getGender());
        assertEquals(dto.getLinkedinProfile(), registeredUser.getLinkedinProfile());
        assertEquals(dto.getProfession(), registeredUser.getOccupation());
        assertEquals("1", registeredUser.getPersonalDataProcessingConsent());
        assertEquals("1", registeredUser.getProfilingConsent());
        assertEquals("1", registeredUser.getNewsletterSubscription());
    }
}