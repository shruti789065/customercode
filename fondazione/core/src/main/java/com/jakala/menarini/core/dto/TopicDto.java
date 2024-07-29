package com.jakala.menarini.core.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.apache.tika.config.Field;


public class TopicDto implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String table = "TOPIC";

	private long id;

	@Field(name="created_on")
	private Timestamp createdOn;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	private String name;

	//bi-directional many-to-one association to EventTopic
	private List<EventTopicDto> eventTopics;

	//bi-directional many-to-one association to RegisteredUserTopic
	private List<RegisteredUserTopicDto> registeredUserTopics;

	public TopicDto() {
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

	public List<EventTopicDto> getEventTopics() {
		return this.eventTopics;
	}

	public void setEventTopics(List<EventTopicDto> eventTopics) {
		this.eventTopics = eventTopics;
	}

	public EventTopicDto addEventTopic(EventTopicDto eventTopic) {
		getEventTopics().add(eventTopic);
		eventTopic.setTopic(this);

		return eventTopic;
	}

	public EventTopicDto removeEventTopic(EventTopicDto eventTopic) {
		getEventTopics().remove(eventTopic);
		eventTopic.setTopic(null);

		return eventTopic;
	}

	public List<RegisteredUserTopicDto> getRegisteredUserTopics() {
		return this.registeredUserTopics;
	}

	public void setRegisteredUserTopics(List<RegisteredUserTopicDto> registeredUserTopics) {
		this.registeredUserTopics = registeredUserTopics;
	}

	public RegisteredUserTopicDto addRegisteredUserTopic(RegisteredUserTopicDto registeredUserTopic) {
		getRegisteredUserTopics().add(registeredUserTopic);
		registeredUserTopic.setTopic(this);

		return registeredUserTopic;
	}

	public RegisteredUserTopicDto removeRegisteredUserTopic(RegisteredUserTopicDto registeredUserTopic) {
		getRegisteredUserTopics().remove(registeredUserTopic);
		registeredUserTopic.setTopic(null);

		return registeredUserTopic;
	}

}