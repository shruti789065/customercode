package com.jakala.menarini.core.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the NEWSLETTER_SUBSCRIPTION database table.
 * 
 */
@Entity
@Table(name="NEWSLETTER_SUBSCRIPTION")
@NamedQuery(name="NewsletterSubscription.findAll", query="SELECT n FROM NewsletterSubscription n")
public class NewsletterSubscription implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="created_on")
	private Timestamp createdOn;

	@Column(length=255)
	private String email;

	@Column(length=255)
	private String firstname;

	@Column(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	@Column(length=255)
	private String lastname;

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

	//bi-directional many-to-one association to RegisteredUser
	@ManyToOne
	@JoinColumn(name="registered_user_id")
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