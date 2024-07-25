package com.jakala.menarini.core.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.apache.tika.config.Field;


/**
 * The persistent class for the TOPIC database table.
 * 
 */
public class Topic implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String table = "TOPIC";

	private long id;

	@Field(name="created_on")
	private Timestamp createdOn;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	private String name;

	//bi-directional many-to-one association to EventTopic
	private List<EventTopic> eventTopics;

	//bi-directional many-to-one association to RegisteredUserTopic
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