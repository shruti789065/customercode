package com.jakala.menarini.core.model;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.tika.config.Field;


/**
 * The persistent class for the EVENT_ENROLLMENT database table.
 * 
 */
public class EventEnrollment implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String table = "EVENT_ENROLLMENT";

	private long id;

	@Field(name="created_on")
	private Timestamp createdOn;

	@Field(name="in_person_participation_date_list")
	private Object inPersonParticipationDateList;

	@Field(name="is_live_stream")
	private String isLiveStream;

	@Field(name="is_residential")
	private String isResidential;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	@Field(name="live_stream_registration_ts")
	private Timestamp liveStreamRegistrationTs;

	@Field(name="residential_registration_ts")
	private Timestamp residentialRegistrationTs;

	//bi-directional many-to-one association to Event
	private Event event;

	//bi-directional many-to-one association to RegisteredUser
	private RegisteredUser registeredUser;

	public EventEnrollment() {
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

	public Object getInPersonParticipationDateList() {
		return this.inPersonParticipationDateList;
	}

	public void setInPersonParticipationDateList(Object inPersonParticipationDateList) {
		this.inPersonParticipationDateList = inPersonParticipationDateList;
	}

	public String getIsLiveStream() {
		return this.isLiveStream;
	}

	public void setIsLiveStream(String isLiveStream) {
		this.isLiveStream = isLiveStream;
	}

	public String getIsResidential() {
		return this.isResidential;
	}

	public void setIsResidential(String isResidential) {
		this.isResidential = isResidential;
	}

	public Timestamp getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Timestamp lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public Timestamp getLiveStreamRegistrationTs() {
		return this.liveStreamRegistrationTs;
	}

	public void setLiveStreamRegistrationTs(Timestamp liveStreamRegistrationTs) {
		this.liveStreamRegistrationTs = liveStreamRegistrationTs;
	}

	public Timestamp getResidentialRegistrationTs() {
		return this.residentialRegistrationTs;
	}

	public void setResidentialRegistrationTs(Timestamp residentialRegistrationTs) {
		this.residentialRegistrationTs = residentialRegistrationTs;
	}

	public Event getEvent() {
		return this.event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public RegisteredUser getRegisteredUser() {
		return this.registeredUser;
	}

	public void setRegisteredUser(RegisteredUser registeredUser) {
		this.registeredUser = registeredUser;
	}

}