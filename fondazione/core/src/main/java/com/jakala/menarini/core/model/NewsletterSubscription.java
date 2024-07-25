package com.jakala.menarini.core.model;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.tika.config.Field;


/**
 * The persistent class for the NEWSLETTER_SUBSCRIPTION database table.
 * 
 */
public class NewsletterSubscription implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String table = "NEWSLETTER_SUBSCRIPTION";

	private long id;

	@Field(name="created_on")
	private Timestamp createdOn;

	private String email;

	private String firstname;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	private String lastname;

	@Field(name="newsletter_subscription")
	private String newsletterSubscription;

	@Field(name="newsletter_subscription_ts")
	private Timestamp newsletterSubscriptionTs;

	private String occupation;

	@Field(name="personal_data_processing_consent")
	private String personalDataProcessingConsent;

	@Field(name="personal_data_processing_consent_ts")
	private Timestamp personalDataProcessingConsentTs;

	//bi-directional many-to-one association to RegisteredUser
	private RegisteredUser registeredUser;

	public NewsletterSubscription() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
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

	public RegisteredUser getRegisteredUser() {
		return this.registeredUser;
	}

	public void setRegisteredUser(RegisteredUser registeredUser) {
		this.registeredUser = registeredUser;
	}

}