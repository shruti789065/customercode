package com.jakala.menarini.core.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.apache.tika.config.Field;


/**
 * The persistent class for the VENUE database table.
 * 
 */
public class Venue implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String table = "VENUE";

	private long id;

	@Field(name="created_on")
	private Timestamp createdOn;

	private String description;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	private String name;

	private String notes;

	//bi-directional many-to-one association to Event
	private List<Event> events;

	//bi-directional many-to-one association to EventSessionDate
	private List<EventSessionDate> eventSessionDates;

	//bi-directional many-to-one association to Location
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