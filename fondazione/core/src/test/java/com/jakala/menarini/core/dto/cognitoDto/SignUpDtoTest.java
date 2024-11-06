package com.jakala.menarini.core.dto.cognitoDto;

import org.junit.jupiter.api.Test;

import com.jakala.menarini.core.dto.cognito.SignUpDto;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class SignUpDtoTest {

    @Test
    void testGettersAndSetters() {
        SignUpDto dto = new SignUpDto();
        Date birthDate = new Date();
        ArrayList<String> rolesNames = new ArrayList<>();
        ArrayList<String> interests = new ArrayList<>();

        dto.setFirstName("First Name");
        dto.setLastName("Last Name");
        dto.setBirthDate(birthDate);
        dto.setPassword("password");
        dto.setEmail("email@example.com");
        dto.setProfession("Profession");
        dto.setPhone("1234567890");
        dto.setCountry("Country");
        dto.setGender("Gender");
        dto.setRegistrationStatus("Active");
        dto.setPrivacyConsent(true);
        dto.setProfilingConsent(true);
        dto.setNewsletterConsent(true);
        dto.setRolesNames(rolesNames);
        dto.setInterests(interests);

        assertEquals("First Name", dto.getFirstName());
        assertEquals("Last Name", dto.getLastName());
        assertEquals(birthDate, dto.getBirthDate());
        assertEquals("password", dto.getPassword());
        assertEquals("email@example.com", dto.getEmail());
        assertEquals("Profession", dto.getProfession());
        assertEquals("1234567890", dto.getPhone());
        assertEquals("Country", dto.getCountry());
        assertEquals("Gender", dto.getGender());
        assertEquals("Active", dto.getRegistrationStatus());
        assertTrue(dto.isPrivacyConsent());
        assertTrue(dto.isProfilingConsent());
        assertTrue(dto.isNewsletterConsent());
        assertEquals(rolesNames, dto.getRolesNames());
        assertEquals(interests, dto.getInterests());
    }
}