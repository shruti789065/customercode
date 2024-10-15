package com.jakala.menarini.core.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.tika.config.Field;

import java.sql.Timestamp;

@SuppressWarnings("squid:S2384")
public class EventSessionDateDto implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String table = "EVENT_SESSION_DATE";

	private long id;

	@Field(name="created_on")
	private Timestamp createdOn;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	@Field(name="seq_no")
	private int seqNo;

	private String session;

	@Field(name="session_date")
	private Date sessionDate;

	//bi-directional many-to-one association to Event
	private EventDto event;

	//bi-directional many-to-one association to Location
	private LocationDto location;

	//bi-directional many-to-one association to Venue
	private VenueDto venue;

	public EventSessionDateDto() {
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

	public int getSeqNo() {
		return this.seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public String getSession() {
		return this.session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public Date getSessionDate() {
		return this.sessionDate;
	}

	public void setSessionDate(Date sessionDate) {
		this.sessionDate = sessionDate;
	}

	public EventDto getEvent() {
		return this.event;
	}

	public void setEvent(EventDto event) {
		this.event = event;
	}

	public LocationDto getLocation() {
		return this.location;
	}

	public void setLocation(LocationDto location) {
		this.location = location;
	}

	public VenueDto getVenue() {
		return this.venue;
	}

	public void setVenue(VenueDto venue) {
		this.venue = venue;
	}

}