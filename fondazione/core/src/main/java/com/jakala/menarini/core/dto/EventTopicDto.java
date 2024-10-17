package com.jakala.menarini.core.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.tika.config.Field;

@SuppressWarnings("squid:S2384")
public class EventTopicDto implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String table = "EVENT_TOPIC";

	private long id;

	@Field(name="created_on")
	private Timestamp createdOn;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	private int priority;

	//bi-directional many-to-one association to Event
	private EventDto event;

	//bi-directional many-to-one association to Topic
	private TopicDto topic;

	public EventTopicDto() {
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

	public int getPriority() {
		return this.priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public EventDto getEvent() {
		return this.event;
	}

	public void setEvent(EventDto event) {
		this.event = event;
	}

	public TopicDto getTopic() {
		return this.topic;
	}

	public void setTopic(TopicDto topic) {
		this.topic = topic;
	}

}