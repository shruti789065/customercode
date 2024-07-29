package com.jakala.menarini.core.dto;

import java.io.Serializable;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

import org.apache.tika.config.Field;


public class RegisteredUserDto implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String table = "REGISTERED_USER";
	
	private long id;

	@Field(name="birth_date")
	private Date birthDate;

	private String country;

	@Field(name="created_on")
	private Timestamp createdOn;

	private String email;

	private String firstname;

	private String gender;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	private String lastname;

	@Field(name="legacy_id")
	private int legacyId;

	@Field(name="linkedin_profile")
	private String linkedinProfile;

	@Field(name="newsletter_subscription")
	private String newsletterSubscription;

	@Field(name="newsletter_subscription_ts")
	private Timestamp newsletterSubscriptionTs;

	private String occupation;

	@Field(name="personal_data_processing_consent")
	private String personalDataProcessingConsent;

	@Field(name="personal_data_processing_consent_ts")
	private Timestamp personalDataProcessingConsentTs;

	private String phone;

	@Field(name="profiling_consent")
	private String profilingConsent;

	@Field(name="profiling_consent_ts")
	private Timestamp profilingConsentTs;

	@Field(name="tax_id_code")
	private String taxIdCode;

	private String username;

	//bi-directional many-to-one association to EventEnrollment
	private List<EventEnrollment> eventEnrollments;

	//bi-directional many-to-one association to MagazineSubscription
	private List<MagazineSubscriptionDto> magazineSubscriptions;

	//bi-directional many-to-one association to NewsletterSubscription
	private List<NewsletterSubscriptionDto> newsletterSubscriptions;

	//bi-directional many-to-one association to RegisteredUserRole
	private List<RegisteredUserRoleDto> registeredUserRoles;

	//bi-directional many-to-one association to RegisteredUserTopic
	private List<RegisteredUserTopicDto> registeredUserTopics;

	public RegisteredUserDto() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getBirthDate() {
		return this.birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Timestamp getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Timestamp getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Timestamp lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public int getLegacyId() {
		return this.legacyId;
	}

	public void setLegacyId(int legacyId) {
		this.legacyId = legacyId;
	}

	public String getLinkedinProfile() {
		return this.linkedinProfile;
	}

	public void setLinkedinProfile(String linkedinProfile) {
		this.linkedinProfile = linkedinProfile;
	}

	public String getNewsletterSubscription() {
		return this.newsletterSubscription;
	}

	public void setNewsletterSubscription(String newsletterSubscription) {
		this.newsletterSubscription = newsletterSubscription;
	}

	public Timestamp getNewsletterSubscriptionTs() {
		return this.newsletterSubscriptionTs;
	}

	public void setNewsletterSubscriptionTs(Timestamp newsletterSubscriptionTs) {
		this.newsletterSubscriptionTs = newsletterSubscriptionTs;
	}

	public String getOccupation() {
		return this.occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getPersonalDataProcessingConsent() {
		return this.personalDataProcessingConsent;
	}

	public void setPersonalDataProcessingConsent(String personalDataProcessingConsent) {
		this.personalDataProcessingConsent = personalDataProcessingConsent;
	}

	public Timestamp getPersonalDataProcessingConsentTs() {
		return this.personalDataProcessingConsentTs;
	}

	public void setPersonalDataProcessingConsentTs(Timestamp personalDataProcessingConsentTs) {
		this.personalDataProcessingConsentTs = personalDataProcessingConsentTs;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getProfilingConsent() {
		return this.profilingConsent;
	}

	public void setProfilingConsent(String profilingConsent) {
		this.profilingConsent = profilingConsent;
	}

	public Timestamp getProfilingConsentTs() {
		return this.profilingConsentTs;
	}

	public void setProfilingConsentTs(Timestamp profilingConsentTs) {
		this.profilingConsentTs = profilingConsentTs;
	}

	public String getTaxIdCode() {
		return this.taxIdCode;
	}

	public void setTaxIdCode(String taxIdCode) {
		this.taxIdCode = taxIdCode;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<EventEnrollment> getEventEnrollments() {
		return this.eventEnrollments;
	}

	public void setEventEnrollments(List<EventEnrollment> eventEnrollments) {
		this.eventEnrollments = eventEnrollments;
	}

	public EventEnrollment addEventEnrollment(EventEnrollment eventEnrollment) {
		getEventEnrollments().add(eventEnrollment);
		eventEnrollment.setRegisteredUser(this);

		return eventEnrollment;
	}

	public EventEnrollment removeEventEnrollment(EventEnrollment eventEnrollment) {
		getEventEnrollments().remove(eventEnrollment);
		eventEnrollment.setRegisteredUser(null);

		return eventEnrollment;
	}

	public List<MagazineSubscriptionDto> getMagazineSubscriptions() {
		return this.magazineSubscriptions;
	}

	public void setMagazineSubscriptions(List<MagazineSubscriptionDto> magazineSubscriptions) {
		this.magazineSubscriptions = magazineSubscriptions;
	}

	public MagazineSubscriptionDto addMagazineSubscription(MagazineSubscriptionDto magazineSubscription) {
		getMagazineSubscriptions().add(magazineSubscription);
		magazineSubscription.setRegisteredUser(this);

		return magazineSubscription;
	}

	public MagazineSubscriptionDto removeMagazineSubscription(MagazineSubscriptionDto magazineSubscription) {
		getMagazineSubscriptions().remove(magazineSubscription);
		magazineSubscription.setRegisteredUser(null);

		return magazineSubscription;
	}

	public List<NewsletterSubscriptionDto> getNewsletterSubscriptions() {
		return this.newsletterSubscriptions;
	}

	public void setNewsletterSubscriptions(List<NewsletterSubscriptionDto> newsletterSubscriptions) {
		this.newsletterSubscriptions = newsletterSubscriptions;
	}

	public NewsletterSubscriptionDto addNewsletterSubscription(NewsletterSubscriptionDto newsletterSubscription) {
		getNewsletterSubscriptions().add(newsletterSubscription);
		newsletterSubscription.setRegisteredUser(this);

		return newsletterSubscription;
	}

	public NewsletterSubscriptionDto removeNewsletterSubscription(NewsletterSubscriptionDto newsletterSubscription) {
		getNewsletterSubscriptions().remove(newsletterSubscription);
		newsletterSubscription.setRegisteredUser(null);

		return newsletterSubscription;
	}

	public List<RegisteredUserRoleDto> getRegisteredUserRoles() {
		return this.registeredUserRoles;
	}

	public void setRegisteredUserRoles(List<RegisteredUserRoleDto> registeredUserRoles) {
		this.registeredUserRoles = registeredUserRoles;
	}

	public RegisteredUserRoleDto addRegisteredUserRole(RegisteredUserRoleDto registeredUserRole) {
		getRegisteredUserRoles().add(registeredUserRole);
		registeredUserRole.setRegisteredUser(this);

		return registeredUserRole;
	}

	public RegisteredUserRoleDto removeRegisteredUserRole(RegisteredUserRoleDto registeredUserRole) {
		getRegisteredUserRoles().remove(registeredUserRole);
		registeredUserRole.setRegisteredUser(null);

		return registeredUserRole;
	}

	public List<RegisteredUserTopicDto> getRegisteredUserTopics() {
		return this.registeredUserTopics;
	}

	public void setRegisteredUserTopics(List<RegisteredUserTopicDto> registeredUserTopics) {
		this.registeredUserTopics = registeredUserTopics;
	}

	public RegisteredUserTopicDto addRegisteredUserTopic(RegisteredUserTopicDto registeredUserTopic) {
		getRegisteredUserTopics().add(registeredUserTopic);
		registeredUserTopic.setRegisteredUser(this);

		return registeredUserTopic;
	}

	public RegisteredUserTopicDto removeRegisteredUserTopic(RegisteredUserTopicDto registeredUserTopic) {
		getRegisteredUserTopics().remove(registeredUserTopic);
		registeredUserTopic.setRegisteredUser(null);

		return registeredUserTopic;
	}

}