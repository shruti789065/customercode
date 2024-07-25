package com.jakala.menarini.core.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.tika.config.Field;

import java.sql.Timestamp;


/**
 * The persistent class for the EVENT_SESSION_DATE database table.
 * 
 */
public class EventSessionDate implements Serializable {
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
	private Event event;

	//bi-directional many-to-one association to Location
	private Location location;

	//bi-directional many-to-one association to Venue
	private Venue venue;

	public EventSessionDate() {
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

	public Event getEvent() {
		return this.event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Venue getVenue() {
		return this.venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

}