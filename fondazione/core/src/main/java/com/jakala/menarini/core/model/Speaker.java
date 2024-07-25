package com.jakala.menarini.core.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.apache.tika.config.Field;


/**
 * The persistent class for the SPEAKER database table.
 * 
 */
public class Speaker implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String table = "SPEAKER";

	private long id;

	private String bio;

	@Field(name="created_on")
	private Timestamp createdOn;

	private String curriculum;

	private String firstname;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	private String lastname;

	//bi-directional many-to-one association to EventSpeaker
	private List<EventSpeaker> eventSpeakers;

	public Speaker() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBio() {
		return this.bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public Timestamp getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getCurriculum() {
		return this.curriculum;
	}

	public void setCurriculum(String curriculum) {
		this.curriculum = curriculum;
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

	public List<EventSpeaker> getEventSpeakers() {
		return this.eventSpeakers;
	}

	public void setEventSpeakers(List<EventSpeaker> eventSpeakers) {
		this.eventSpeakers = eventSpeakers;
	}

	public EventSpeaker addEventSpeaker(EventSpeaker eventSpeaker) {
		getEventSpeakers().add(eventSpeaker);
		eventSpeaker.setSpeaker(this);

		return eventSpeaker;
	}

	public EventSpeaker removeEventSpeaker(EventSpeaker eventSpeaker) {
		getEventSpeakers().remove(eventSpeaker);
		eventSpeaker.setSpeaker(null);

		return eventSpeaker;
	}

}