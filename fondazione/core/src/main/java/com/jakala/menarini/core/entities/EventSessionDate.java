package com.jakala.menarini.core.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;
import java.sql.Timestamp;


/**
 * The persistent class for the EVENT_SESSION_DATE database table.
 * 
 */
@Entity
@Table(name="EVENT_SESSION_DATE")
@NamedQuery(name="EventSessionDate.findAll", query="SELECT e FROM EventSessionDate e")
public class EventSessionDate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="created_on")
	private Timestamp createdOn;

	@Column(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	@Column(name="seq_no")
	private int seqNo;

	@Column(length=255)
	private String session;

	@Temporal(TemporalType.DATE)
	@Column(name="session_date")
	private Date sessionDate;

	//bi-directional many-to-one association to Event
	@ManyToOne
	@JoinColumn(name="event_id")
	private Event event;

	//bi-directional many-to-one association to Location
	@ManyToOne
	@JoinColumn(name="location_id")
	private Location location;

	//bi-directional many-to-one association to Venue
	@ManyToOne
	@JoinColumn(name="venue_id")
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