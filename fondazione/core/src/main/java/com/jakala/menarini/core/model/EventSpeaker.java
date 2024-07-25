package com.jakala.menarini.core.model;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.tika.config.Field;


/**
 * The persistent class for the EVENT_SPEAKER database table.
 * 
 */
public class EventSpeaker implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String table = "EVENT_SPEAKER";

	private long id;

	@Field(name="created_on")
	private Timestamp createdOn;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	//bi-directional many-to-one association to Event
	private Event event;

	//bi-directional many-to-one association to Speaker
	private Speaker speaker;

	public EventSpeaker() {
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

	public Event getEvent() {
		return this.event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Speaker getSpeaker() {
		return this.speaker;
	}

	public void setSpeaker(Speaker speaker) {
		this.speaker = speaker;
	}

}