package com.jakala.menarini.core.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the VENUE database table.
 * 
 */
@Entity
@Table(name="VENUE")
@NamedQuery(name="Venue.findAll", query="SELECT v FROM Venue v")
public class Venue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="created_on")
	private Timestamp createdOn;

	@Column(length=255)
	private String description;

	@Column(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	@Column(length=255)
	private String name;

	@Lob
	private String notes;

	//bi-directional many-to-one association to Event
	@OneToMany(mappedBy="venue")
	private List<Event> events;

	//bi-directional many-to-one association to EventSessionDate
	@OneToMany(mappedBy="venue")
	private List<EventSessionDate> eventSessionDates;

	//bi-directional many-to-one association to Location
	@ManyToOne
	@JoinColumn(name="location_id")
	private Location location;

	public Venue() {
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

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Timestamp lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public List<Event> getEvents() {
		return this.events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public Event addEvent(Event event) {
		getEvents().add(event);
		event.setVenue(this);

		return event;
	}

	public Event removeEvent(Event event) {
		getEvents().remove(event);
		event.setVenue(null);

		return event;
	}

	public List<EventSessionDate> getEventSessionDates() {
		return this.eventSessionDates;
	}

	public void setEventSessionDates(List<EventSessionDate> eventSessionDates) {
		this.eventSessionDates = eventSessionDates;
	}

	public EventSessionDate addEventSessionDate(EventSessionDate eventSessionDate) {
		getEventSessionDates().add(eventSessionDate);
		eventSessionDate.setVenue(this);

		return eventSessionDate;
	}

	public EventSessionDate removeEventSessionDate(EventSessionDate eventSessionDate) {
		getEventSessionDates().remove(eventSessionDate);
		eventSessionDate.setVenue(null);

		return eventSessionDate;
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}