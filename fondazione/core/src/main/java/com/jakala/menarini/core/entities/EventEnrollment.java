package com.jakala.menarini.core.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the EVENT_ENROLLMENT database table.
 * 
 */
@Entity
@Table(name="EVENT_ENROLLMENT")
@NamedQuery(name="EventEnrollment.findAll", query="SELECT e FROM EventEnrollment e")
public class EventEnrollment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="created_on")
	private Timestamp createdOn;

	@Column(name="in_person_participation_date_list")
	private Object inPersonParticipationDateList;

	@Column(name="is_live_stream", length=1)
	private String isLiveStream;

	@Column(name="is_residential", length=1)
	private String isResidential;

	@Column(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	@Column(name="live_stream_registration_ts")
	private Timestamp liveStreamRegistrationTs;

	@Column(name="residential_registration_ts")
	private Timestamp residentialRegistrationTs;

	//bi-directional many-to-one association to Event
	@ManyToOne
	@JoinColumn(name="event_id")
	private Event event;

	//bi-directional many-to-one association to RegisteredUser
	@ManyToOne
	@JoinColumn(name="registered_user_id")
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