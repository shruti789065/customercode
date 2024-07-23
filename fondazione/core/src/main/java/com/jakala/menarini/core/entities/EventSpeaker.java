package com.jakala.menarini.core.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the EVENT_SPEAKER database table.
 * 
 */
@Entity
@Table(name="EVENT_SPEAKER")
@NamedQuery(name="EventSpeaker.findAll", query="SELECT e FROM EventSpeaker e")
public class EventSpeaker implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="created_on")
	private Timestamp createdOn;

	@Column(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	//bi-directional many-to-one association to Event
	@ManyToOne
	@JoinColumn(name="event_id")
	private Event event;

	//bi-directional many-to-one association to Speaker
	@ManyToOne
	@JoinColumn(name="speaker_id")
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