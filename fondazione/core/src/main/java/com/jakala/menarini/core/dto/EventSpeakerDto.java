package com.jakala.menarini.core.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.tika.config.Field;

@SuppressWarnings("squid:S2384")
public class EventSpeakerDto implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String table = "EVENT_SPEAKER";

	private long id;

	@Field(name="created_on")
	private Timestamp createdOn;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	//bi-directional many-to-one association to Event
	private EventDto event;

	//bi-directional many-to-one association to Speaker
	private SpeakerDto speaker;

	public EventSpeakerDto() {
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

	public EventDto getEvent() {
		return this.event;
	}

	public void setEvent(EventDto event) {
		this.event = event;
	}

	public SpeakerDto getSpeaker() {
		return this.speaker;
	}

	public void setSpeaker(SpeakerDto speaker) {
		this.speaker = speaker;
	}

}