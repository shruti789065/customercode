package com.jakala.menarini.core.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the SPEAKER database table.
 * 
 */
@Entity
@Table(name="SPEAKER")
@NamedQuery(name="Speaker.findAll", query="SELECT s FROM Speaker s")
public class Speaker implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(length=2000)
	private String bio;

	@Column(name="created_on")
	private Timestamp createdOn;

	@Lob
	private String curriculum;

	@Column(length=255)
	private String firstname;

	@Column(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	@Column(length=255)
	private String lastname;

	//bi-directional many-to-one association to EventSpeaker
	@OneToMany(mappedBy="speaker")
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