package com.jakala.menarini.core.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.apache.tika.config.Field;

@SuppressWarnings("squid:S2384")
public class SpeakerDto implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String table = "SPEAKER";

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
	private List<EventSpeakerDto> eventSpeakers;

	public SpeakerDto() {
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

	public List<EventSpeakerDto> getEventSpeakers() {
		return this.eventSpeakers;
	}

	public void setEventSpeakers(List<EventSpeakerDto> eventSpeakers) {
		this.eventSpeakers = eventSpeakers;
	}

	public EventSpeakerDto addEventSpeaker(EventSpeakerDto eventSpeaker) {
		getEventSpeakers().add(eventSpeaker);
		eventSpeaker.setSpeaker(this);

		return eventSpeaker;
	}

	public EventSpeakerDto removeEventSpeaker(EventSpeakerDto eventSpeaker) {
		getEventSpeakers().remove(eventSpeaker);
		eventSpeaker.setSpeaker(null);

		return eventSpeaker;
	}

}