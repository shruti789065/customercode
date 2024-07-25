package com.jakala.menarini.core.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.apache.tika.config.Field;


/**
 * The persistent class for the LOCATION database table.
 * 
 */
public class Location implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String table = "LOCATION";

	private long id;

	private String city;

	private String country;

	@Field(name="created_on")
	private Timestamp createdOn;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	private String notes;

	//bi-directional many-to-one association to Event
	private List<Event> events;

	//bi-directional many-to-one association to EventSessionDate
	private List<EventSessionDate> eventSessionDates;

	//bi-directional many-to-one association to Venue
	private List<Venue> venues;

	public Location() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
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
		event.setLocation(this);

		return event;
	}

	public Event removeEvent(Event event) {
		getEvents().remove(event);
		event.setLocation(null);

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
		eventSessionDate.setLocation(this);

		return eventSessionDate;
	}

	public EventSessionDate removeEventSessionDate(EventSessionDate eventSessionDate) {
		getEventSessionDates().remove(eventSessionDate);
		eventSessionDate.setLocation(null);

		return eventSessionDate;
	}

	public List<Venue> getVenues() {
		return this.venues;
	}

	public void setVenues(List<Venue> venues) {
		this.venues = venues;
	}

	public Venue addVenue(Venue venue) {
		getVenues().add(venue);
		venue.setLocation(this);

		return venue;
	}

	public Venue removeVenue(Venue venue) {
		getVenues().remove(venue);
		venue.setLocation(null);

		return venue;
	}

}