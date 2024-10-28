package com.jakala.menarini.core.models.interfaces;

import com.jakala.menarini.core.dto.RegisteredUserTopicDto;
import com.jakala.menarini.core.service.interfaces.ImageProfileServiceInterface;

import java.sql.Timestamp;
import java.util.List;

public interface ReservedAreaBaseModelInterface {

    public long getId();

    public void setId(long id);

    public Timestamp getBirthDate();

    public void setBirthDate(Timestamp birthDate);

    public String getCountry();

    public void setCountry(String country);

    public Timestamp getCreatedOn();

    public void setCreatedOn(Timestamp createdOn);

    public String getEmail();

    public void setEmail(String email);

    public String getFirstName();

    public void setFirstName(String firstname);

    public String getGender();

    public void setGender(String gender);

    public Timestamp getLastUpdatedOn();

    public void setLastUpdatedOn(Timestamp lastUpdatedOn);

    public String getLastName();

    public void setLastName(String lastname);

    public String getLinkedinProfile();

    public void setLinkedinProfile(String linkedinProfile);

    public String getNewsletterSubscription();

    public void setNewsletterSubscription(String newsletterSubscription);

    public Timestamp getNewsletterSubscriptionTs();

    public void setNewsletterSubscriptionTs(Timestamp newsletterSubscriptionTs);

    public String getOccupation();

    public void setOccupation(String occupation);

    public String getPersonalDataProcessingConsent() ;

    public void setPersonalDataProcessingConsent(String personalDataProcessingConsent);

    public Timestamp getPersonalDataProcessingConsentTs();

    public void setPersonalDataProcessingConsentTs(Timestamp personalDataProcessingConsentTs);

    public String getPhone();

    public void setPhone(String phone);

    public String getProfilingConsent();

    public void setProfilingConsent(String profilingConsent);

    public Timestamp getProfilingConsentTs();

    public void setProfilingConsentTs(Timestamp profilingConsentTs);

    public String getTaxIdCode();

    public void setTaxIdCode(String taxIdCode);

    public String getRegistrationStatus();

    public void setRegistrationStatus(String registrationStatus);

    public String getUsername();

    public void setUsername(String username);

    public List<RegisteredUserTopicDto> getTopics();

    public void setTopics(List<RegisteredUserTopicDto> topics);

    public ImageProfileServiceInterface getImageService();

    public void setImageService(ImageProfileServiceInterface imageService);

    public String getBase64Image();

    public void setBase64Image(String base64Image);

}
