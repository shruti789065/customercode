package com.jakala.menarini.core.dto.cognitoDto;

import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RoleDto;
import com.jakala.menarini.core.dto.TopicDto;
import java.sql.Timestamp;
import java.util.*;

public class SignUpDtoResponse  extends SignUpDto {

    private CognitoSignInErrorResponseDto cognitoSignUpErrorResponseDto;

    private String UserConfirmed;
    private String UserSub;
    
    private ArrayList<RoleDto> roles;
    private ArrayList<TopicDto> topics;
    
    public SignUpDtoResponse() {
        super();
        this.roles = new ArrayList<>();
        this.topics = new ArrayList<>();
    }


    public ArrayList<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<RoleDto> roles) {
        this.roles = roles;
    }

    public CognitoSignInErrorResponseDto getCognitoSignUpErrorResponseDto() {
        return cognitoSignUpErrorResponseDto;
    }

    public void setCognitoSignUpErrorResponseDto(CognitoSignInErrorResponseDto cognitoSignUpErrorResponseDto) {
        this.cognitoSignUpErrorResponseDto = cognitoSignUpErrorResponseDto;
    }

    public String getUserConfirmed() {
        return UserConfirmed;
    }

    public void setUserConfirmed(String userConfirmed) {
        UserConfirmed = userConfirmed;
    }

    public String getUserSub() {
        return UserSub;
    }

    public void setUserSub(String userSub) {
        UserSub = userSub;
    }

    public ArrayList<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<TopicDto> topics) {
        this.topics = topics;
    }

    public void copyRegistrationData(SignUpDto sourceData, List<RoleDto> rolesData) {
        setEmail(sourceData.getEmail());
        setRegistrationStatus(sourceData.getRegistrationStatus());
        setFirstName(sourceData.getFirstName());
        setLastName(sourceData.getLastName());
        setCountry(sourceData.getCountry());
        setBirthDate(sourceData.getBirthDate());
        setPhone(sourceData.getPhone());
        setProfession(sourceData.getProfession());
        setInterests(sourceData.getInterests());
        setGender(sourceData.getGender());
        setPrivacyConsent(sourceData.isPrivacyConsent());
        setProfilingConsent(sourceData.isProfilingConsent());
        setNewsletterConsent(sourceData.isNewsletterConsent());
        if(sourceData.getRolesNames() != null && !sourceData.getRolesNames().isEmpty()) {
            sourceData.getRolesNames().forEach(roleName -> {
                for(RoleDto roleDto : rolesData) {
                    if(roleDto.getName().equals(roleName)) {
                        roles.add(roleDto);
                    }
                }
            });
        }
        if(sourceData.getInterests() != null && !sourceData.getInterests().isEmpty()) {
            sourceData.getInterests().forEach(interest -> {
                TopicDto topic = new TopicDto();
                topic.setName(interest);
                topic.setId(interest);
                topics.add(topic);
            });
        }
    }

    public RegisteredUserDto generateRegisteredUser(String username) {
        RegisteredUserDto registeredUser = new RegisteredUserDto();
        registeredUser.setUsername(username);
        registeredUser.setEmail(this.getEmail());
        registeredUser.setFirstname(this.getFirstName());
        registeredUser.setLastname(this.getLastName());
        registeredUser.setCountry(this.getCountry());
        registeredUser.setBirthDate(this.getBirthDate());
        registeredUser.setPhone(this.getPhone());
        registeredUser.setGender(this.getGender());
        registeredUser.setCreatedOn((new Timestamp(System.currentTimeMillis())));
        registeredUser.setLastUpdatedOn((new Timestamp(System.currentTimeMillis())));
        registeredUser.setRegistrationStatus(this.getRegistrationStatus());
        registeredUser.setOccupation(this.getProfession());
        registeredUser.setProfilingConsent(this.convertBool(this.isProfilingConsent()));
        registeredUser.setProfilingConsentTs((new Timestamp(System.currentTimeMillis())));
        registeredUser.setPersonalDataProcessingConsent(this.convertBool(this.isPrivacyConsent()));
        registeredUser.setPersonalDataProcessingConsentTs((new Timestamp(System.currentTimeMillis())));
        registeredUser.setNewsletterSubscription(this.convertBool(this.isNewsletterConsent()));
        registeredUser.setNewsletterSubscriptionTs((new Timestamp(System.currentTimeMillis())));
        return registeredUser;
    }

    private String convertBool(boolean bool) {
        return bool ? "1" : "0";
    }

}
