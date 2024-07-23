package com.jakala.menarini.core.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the REGISTERED_USER database table.
 * 
 */
@Entity
@Table(name="REGISTERED_USER")
@NamedQuery(name="RegisteredUser.findAll", query="SELECT r FROM RegisteredUser r")
public class RegisteredUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Temporal(TemporalType.DATE)
	@Column(name="birth_date")
	private Date birthDate;

	@Column(length=30)
	private String country;

	@Column(name="created_on")
	private Timestamp createdOn;

	@Column(length=255)
	private String email;

	@Column(length=255)
	private String firstname;

	@Column(length=30)
	private String gender;

	@Column(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	@Column(length=255)
	private String lastname;

	@Column(name="legacy_id")
	private int legacyId;

	@Column(name="linkedin_profile", length=255)
	private String linkedinProfile;

	@Column(name="newsletter_subscription", length=1)
	private String newsletterSubscription;

	@Column(name="newsletter_subscription_ts")
	private Timestamp newsletterSubscriptionTs;

	@Column(length=255)
	private String occupation;

	@Column(name="personal_data_processing_consent", length=1)
	private String personalDataProcessingConsent;

	@Column(name="personal_data_processing_consent_ts")
	private Timestamp personalDataProcessingConsentTs;

	@Column(length=30)
	private String phone;

	@Column(name="profiling_consent", length=1)
	private String profilingConsent;

	@Column(name="profiling_consent_ts")
	private Timestamp profilingConsentTs;

	@Column(name="tax_id_code", length=30)
	private String taxIdCode;

	@Column(length=255)
	private String username;

	//bi-directional many-to-one association to EventEnrollment
	@OneToMany(mappedBy="registeredUser")
	private List<EventEnrollment> eventEnrollments;

	//bi-directional many-to-one association to MagazineSubscription
	@OneToMany(mappedBy="registeredUser")
	private List<MagazineSubscription> magazineSubscriptions;

	//bi-directional many-to-one association to NewsletterSubscription
	@OneToMany(mappedBy="registeredUser")
	private List<NewsletterSubscription> newsletterSubscriptions;

	//bi-directional many-to-one association to RegisteredUserRole
	@OneToMany(mappedBy="registeredUser")
	private List<RegisteredUserRole> registeredUserRoles;

	//bi-directional many-to-one association to RegisteredUserTopic
	@OneToMany(mappedBy="registeredUser")
	private List<RegisteredUserTopic> registeredUserTopics;

	public RegisteredUser() {
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

	public List<MagazineSubscription> getMagazineSubscriptions() {
		return this.magazineSubscriptions;
	}

	public void setMagazineSubscriptions(List<MagazineSubscription> magazineSubscriptions) {
		this.magazineSubscriptions = magazineSubscriptions;
	}

	public MagazineSubscription addMagazineSubscription(MagazineSubscription magazineSubscription) {
		getMagazineSubscriptions().add(magazineSubscription);
		magazineSubscription.setRegisteredUser(this);

		return magazineSubscription;
	}

	public MagazineSubscription removeMagazineSubscription(MagazineSubscription magazineSubscription) {
		getMagazineSubscriptions().remove(magazineSubscription);
		magazineSubscription.setRegisteredUser(null);

		return magazineSubscription;
	}

	public List<NewsletterSubscription> getNewsletterSubscriptions() {
		return this.newsletterSubscriptions;
	}

	public void setNewsletterSubscriptions(List<NewsletterSubscription> newsletterSubscriptions) {
		this.newsletterSubscriptions = newsletterSubscriptions;
	}

	public NewsletterSubscription addNewsletterSubscription(NewsletterSubscription newsletterSubscription) {
		getNewsletterSubscriptions().add(newsletterSubscription);
		newsletterSubscription.setRegisteredUser(this);

		return newsletterSubscription;
	}

	public NewsletterSubscription removeNewsletterSubscription(NewsletterSubscription newsletterSubscription) {
		getNewsletterSubscriptions().remove(newsletterSubscription);
		newsletterSubscription.setRegisteredUser(null);

		return newsletterSubscription;
	}

	public List<RegisteredUserRole> getRegisteredUserRoles() {
		return this.registeredUserRoles;
	}

	public void setRegisteredUserRoles(List<RegisteredUserRole> registeredUserRoles) {
		this.registeredUserRoles = registeredUserRoles;
	}

	public RegisteredUserRole addRegisteredUserRole(RegisteredUserRole registeredUserRole) {
		getRegisteredUserRoles().add(registeredUserRole);
		registeredUserRole.setRegisteredUser(this);

		return registeredUserRole;
	}

	public RegisteredUserRole removeRegisteredUserRole(RegisteredUserRole registeredUserRole) {
		getRegisteredUserRoles().remove(registeredUserRole);
		registeredUserRole.setRegisteredUser(null);

		return registeredUserRole;
	}

	public List<RegisteredUserTopic> getRegisteredUserTopics() {
		return this.registeredUserTopics;
	}

	public void setRegisteredUserTopics(List<RegisteredUserTopic> registeredUserTopics) {
		this.registeredUserTopics = registeredUserTopics;
	}

	public RegisteredUserTopic addRegisteredUserTopic(RegisteredUserTopic registeredUserTopic) {
		getRegisteredUserTopics().add(registeredUserTopic);
		registeredUserTopic.setRegisteredUser(this);

		return registeredUserTopic;
	}

	public RegisteredUserTopic removeRegisteredUserTopic(RegisteredUserTopic registeredUserTopic) {
		getRegisteredUserTopics().remove(registeredUserTopic);
		registeredUserTopic.setRegisteredUser(null);

		return registeredUserTopic;
	}

}