package com.jakala.menarini.core.dto.cognitoDto;

import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.dto.TopicDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SignUpDtoResponseTest {

    @Test
    void testGettersAndSetters() {
        SignUpDtoResponse dto = new SignUpDtoResponse();
        CognitoSignInErrorResponseDto cognitoSignUpErrorResponseDto = new CognitoSignInErrorResponseDto();
        ArrayList<RoleDto> roles = new ArrayList<>();
        ArrayList<TopicDto> topics = new ArrayList<>();

        dto.setCognitoSignUpErrorResponseDto(cognitoSignUpErrorResponseDto);
        dto.setUserConfirmed("confirmed");
        dto.setUserSub("userSub");
        dto.setRoles(roles);
        dto.setTopics(topics);

        assertEquals(cognitoSignUpErrorResponseDto, dto.getCognitoSignUpErrorResponseDto());
        assertEquals("confirmed", dto.getUserConfirmed());
        assertEquals("userSub", dto.getUserSub());
        assertEquals(roles, dto.getRoles());
        assertEquals(topics, dto.getTopics());
    }

    @Test
    void testCopyRegistrationData() {
        SignUpDtoResponse dto = new SignUpDtoResponse();
        SignUpDto sourceData = new SignUpDto();
        sourceData.setEmail("email@example.com");
        sourceData.setRegistrationStatus("Active");
        sourceData.setFirstName("First Name");
        sourceData.setLastName("Last Name");
        sourceData.setCountry("Country");
        sourceData.setBirthDate(new Date());
        sourceData.setPhone("1234567890");
        sourceData.setProfession("Profession");
        sourceData.setInterests(new ArrayList<>(Arrays.asList("Interest1", "Interest2")));
        sourceData.setGender("Gender");
        sourceData.setPrivacyConsent(true);
        sourceData.setProfilingConsent(true);
        sourceData.setNewsletterConsent(true);
        sourceData.setRolesNames(new ArrayList<>(Arrays.asList("Role1", "Role2")));

        List<RoleDto> rolesData = new ArrayList<>();
        RoleDto role1 = new RoleDto();
        role1.setName("Role1");
        RoleDto role2 = new RoleDto();
        role2.setName("Role2");
        rolesData.add(role1);
        rolesData.add(role2);

        dto.copyRegistrationData(sourceData, rolesData);

        assertEquals("email@example.com", dto.getEmail());
        assertEquals("Active", dto.getRegistrationStatus());
        assertEquals("First Name", dto.getFirstName());
        assertEquals("Last Name", dto.getLastName());
        assertEquals("Country", dto.getCountry());
        assertEquals(sourceData.getBirthDate(), dto.getBirthDate());
        assertEquals("1234567890", dto.getPhone());
        assertEquals("Profession", dto.getProfession());
        assertEquals(sourceData.getInterests(), dto.getInterests());
        assertEquals("Gender", dto.getGender());
        assertTrue(dto.isPrivacyConsent());
        assertTrue(dto.isProfilingConsent());
        assertTrue(dto.isNewsletterConsent());
        assertEquals(2, dto.getRoles().size());
        assertEquals(2, dto.getTopics().size());
    }

    @Test
    void testGenerateRegisteredUser() {
        SignUpDtoResponse dto = new SignUpDtoResponse();
        dto.setEmail("email@example.com");
        dto.setFirstName("First Name");
        dto.setLastName("Last Name");
        dto.setCountry("Country");
        dto.setBirthDate(new Date());
        dto.setPhone("1234567890");
        dto.setGender("Gender");
        dto.setRegistrationStatus("Active");
        dto.setProfession("Profession");
        dto.setPrivacyConsent(true);
        dto.setProfilingConsent(true);
        dto.setNewsletterConsent(true);

        RegisteredUserDto registeredUser = dto.generateRegisteredUser("username");

        assertEquals("username", registeredUser.getUsername());
        assertEquals("email@example.com", registeredUser.getEmail());
        assertEquals("First Name", registeredUser.getFirstname());
        assertEquals("Last Name", registeredUser.getLastname());
        assertEquals("Country", registeredUser.getCountry());
        assertEquals(dto.getBirthDate(), registeredUser.getBirthDate());
        assertEquals("1234567890", registeredUser.getPhone());
        assertEquals("Gender", registeredUser.getGender());
        assertEquals("Active", registeredUser.getRegistrationStatus());
        assertEquals("Profession", registeredUser.getOccupation());
        assertEquals("1", registeredUser.getProfilingConsent());
        assertEquals("1", registeredUser.getPersonalDataProcessingConsent());
        assertEquals("1", registeredUser.getNewsletterSubscription());
    }
}