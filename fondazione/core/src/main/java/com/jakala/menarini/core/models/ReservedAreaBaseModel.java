package com.jakala.menarini.core.models;

import com.jakala.menarini.core.dto.RegisteredUserDto;
import com.jakala.menarini.core.dto.RegisteredUserTopicDto;
import com.jakala.menarini.core.dto.awslambda.ImageProfileServiceResponseDto;
import com.jakala.menarini.core.dto.awslambda.LambdaGetFileDto;
import com.jakala.menarini.core.models.interfaces.ReservedAreaBaseModelInterface;
import com.jakala.menarini.core.service.interfaces.EncryptDataServiceInterface;
import com.jakala.menarini.core.service.interfaces.ImageProfileServiceInterface;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;


import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("squid:S2384")
@Model(
        adaptables = {
                SlingHttpServletRequest.class
        },
        adapters = ReservedAreaBaseModelInterface.class
)
public class ReservedAreaBaseModel extends AuthBaseModel implements ReservedAreaBaseModelInterface {


    private long id;
    private Timestamp birthDate;
    private String country;
    private Timestamp createdOn;
    private String email;
    private String firstName;
    private String gender;
    private Timestamp lastUpdatedOn;
    private String lastName;
    private String linkedinProfile;
    private String newsletterSubscription;
    private Timestamp newsletterSubscriptionTs;
    private String occupation;
    private String personalDataProcessingConsent;
    private Timestamp personalDataProcessingConsentTs;
    private String phone;
    private String profilingConsent;
    private Timestamp profilingConsentTs;
    private String taxIdCode;
    private String registrationStatus;
    private String username;
    private List<RegisteredUserTopicDto> topics = new ArrayList<>();
    private String base64Image;
    
    
    @OSGiService
    private ImageProfileServiceInterface imageService;
    @OSGiService
    private EncryptDataServiceInterface encryptService;

    @Override
    @PostConstruct
    protected void init()  {
        super.init();
        if(this.isAuth()) {
            this.setUserData();
        }
        
    }


    private void setUserData() {
        RegisteredUserDto user = this.getUser();
        this.firstName = user.getFirstname();
        this.lastName = user.getLastname();
        this.birthDate = user.getBirthDate() != null ?
               new Timestamp(user.getBirthDate().getTime()) :
        null;
        this.id = user.getId();
        this.country = user.getCountry();
        this.createdOn = user.getCreatedOn();
        this.lastUpdatedOn = user.getLastUpdatedOn();
        this.gender = user.getGender();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.occupation = user.getOccupation();
        this.linkedinProfile = user.getLinkedinProfile();
        this.profilingConsent = user.getProfilingConsent();
        this.profilingConsentTs = user.getProfilingConsentTs();
        this.personalDataProcessingConsent = user.getPersonalDataProcessingConsent();
        this.personalDataProcessingConsentTs = user.getPersonalDataProcessingConsentTs();
        this.newsletterSubscription = user.getNewsletterSubscription();
        this.newsletterSubscriptionTs = user.getNewsletterSubscriptionTs();
        this.topics = user.getRegisteredUserTopics();
        this.base64Image = (String) super.request.getAttribute("userImage");
        if(this.base64Image == null || this.base64Image.isBlank() ) {
            Cookie authCookie = super.request.getCookie("p-idToken");
            String token = encryptService.decrypt(authCookie.getValue());
            LambdaGetFileDto getFileDto = new LambdaGetFileDto();
            getFileDto.setEmail(this.email.replace("@","_"));
            ImageProfileServiceResponseDto imageProfile  = imageService.getImageProfile(getFileDto,token);
            if(!imageProfile.getImageData().isBlank()){
                this.base64Image = imageProfile.getImageData();
                super.request.setAttribute("image",this.base64Image);
            }
        }

    }

    @Override
    public long getId() {
        return id;
    }
    @Override
    public void setId(long id) {
        this.id = id;
    }
    @Override
    public Timestamp getBirthDate() {
        return birthDate;
    }
    @Override
    public void setBirthDate(Timestamp birthDate) {
        this.birthDate = birthDate;
    }
    @Override
    public String getCountry() {
        return country;
    }
    @Override
    public void setCountry(String country) {
        this.country = country;
    }
    @Override
    public Timestamp getCreatedOn() {
        return createdOn;
    }
    @Override
    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }
    @Override
    public String getEmail() {
        return email;
    }
    @Override
    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public String getFirstName() {
        return firstName;
    }
    @Override
    public void setFirstName(String firstname) {
        this.firstName = firstname;
    }
    @Override
    public String getGender() {
        return gender;
    }
    @Override
    public void setGender(String gender) {
        this.gender = gender;
    }
    @Override
    public Timestamp getLastUpdatedOn() {
        return lastUpdatedOn;
    }
    @Override
    public void setLastUpdatedOn(Timestamp lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }
    @Override
    public String getLastName() {
        return lastName;
    }
    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    @Override
    public String getLinkedinProfile() {
        return linkedinProfile;
    }
    @Override
    public void setLinkedinProfile(String linkedinProfile) {
        this.linkedinProfile = linkedinProfile;
    }
    @Override
    public String getNewsletterSubscription() {
        return newsletterSubscription;
    }
    @Override
    public void setNewsletterSubscription(String newsletterSubscription) {
        this.newsletterSubscription = newsletterSubscription;
    }
    @Override
    public Timestamp getNewsletterSubscriptionTs() {
        return newsletterSubscriptionTs;
    }
    @Override
    public void setNewsletterSubscriptionTs(Timestamp newsletterSubscriptionTs) {
        this.newsletterSubscriptionTs = newsletterSubscriptionTs;
    }
    @Override
    public String getOccupation() {
        return occupation;
    }
    @Override
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
    @Override
    public String getPersonalDataProcessingConsent() {
        return personalDataProcessingConsent;
    }
    @Override
    public void setPersonalDataProcessingConsent(String personalDataProcessingConsent) {
        this.personalDataProcessingConsent = personalDataProcessingConsent;
    }
    @Override
    public Timestamp getPersonalDataProcessingConsentTs() {
        return personalDataProcessingConsentTs;
    }
    @Override
    public void setPersonalDataProcessingConsentTs(Timestamp personalDataProcessingConsentTs) {
        this.personalDataProcessingConsentTs = personalDataProcessingConsentTs;
    }
    @Override
    public String getPhone() {
        return phone;
    }
    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }
    @Override
    public String getProfilingConsent() {
        return profilingConsent;
    }
    @Override
    public void setProfilingConsent(String profilingConsent) {
        this.profilingConsent = profilingConsent;
    }
    @Override
    public Timestamp getProfilingConsentTs() {
        return profilingConsentTs;
    }
    @Override
    public void setProfilingConsentTs(Timestamp profilingConsentTs) {
        this.profilingConsentTs = profilingConsentTs;
    }
    @Override
    public String getTaxIdCode() {
        return taxIdCode;
    }
    @Override
    public void setTaxIdCode(String taxIdCode) {
        this.taxIdCode = taxIdCode;
    }
    @Override
    public String getRegistrationStatus() {
        return registrationStatus;
    }
    @Override
    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }
    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public void setUsername(String username) {
        this.username = username;
    }
    @Override
    public List<RegisteredUserTopicDto> getTopics() {
        return topics;
    }
    @Override
    public void setTopics(List<RegisteredUserTopicDto> topics) {
        this.topics = topics;
    }
    @Override
    public ImageProfileServiceInterface getImageService() {
        return imageService;
    }
    @Override
    public void setImageService(ImageProfileServiceInterface imageService) {
        this.imageService = imageService;
    }
    @Override
    public String getBase64Image() {
        return base64Image;
    }
    @Override
    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }
}
