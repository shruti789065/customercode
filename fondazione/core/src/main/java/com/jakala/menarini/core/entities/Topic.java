package com.jakala.menarini.core.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the TOPIC database table.
 * 
 */
@Entity
@Table(name="TOPIC")
@NamedQuery(name="Topic.findAll", query="SELECT t FROM Topic t")
public class Topic implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="created_on")
	private Timestamp createdOn;

	@Column(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	@Column(length=255)
	private String name;

	//bi-directional many-to-one association to EventTopic
	@OneToMany(mappedBy="topic")
	private List<EventTopic> eventTopics;

	//bi-directional many-to-one association to RegisteredUserTopic
	@OneToMany(mappedBy="topic")
	private List<RegisteredUserTopic> registeredUserTopics;

	public Topic() {
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

	public Timestamp getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Timestamp lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EventTopic> getEventTopics() {
		return this.eventTopics;
	}

	public void setEventTopics(List<EventTopic> eventTopics) {
		this.eventTopics = eventTopics;
	}

	public EventTopic addEventTopic(EventTopic eventTopic) {
		getEventTopics().add(eventTopic);
		eventTopic.setTopic(this);

		return eventTopic;
	}

	public EventTopic removeEventTopic(EventTopic eventTopic) {
		getEventTopics().remove(eventTopic);
		eventTopic.setTopic(null);

		return eventTopic;
	}

	public List<RegisteredUserTopic> getRegisteredUserTopics() {
		return this.registeredUserTopics;
	}

	public void setRegisteredUserTopics(List<RegisteredUserTopic> registeredUserTopics) {
		this.registeredUserTopics = registeredUserTopics;
	}

	public RegisteredUserTopic addRegisteredUserTopic(RegisteredUserTopic registeredUserTopic) {
		getRegisteredUserTopics().add(registeredUserTopic);
		registeredUserTopic.setTopic(this);

		return registeredUserTopic;
	}

	public RegisteredUserTopic removeRegisteredUserTopic(RegisteredUserTopic registeredUserTopic) {
		getRegisteredUserTopics().remove(registeredUserTopic);
		registeredUserTopic.setTopic(null);

		return registeredUserTopic;
	}

}