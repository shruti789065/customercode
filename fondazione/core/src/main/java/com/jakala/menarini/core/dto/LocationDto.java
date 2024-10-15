package com.jakala.menarini.core.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.apache.tika.config.Field;

@SuppressWarnings("squid:S2384")
public class LocationDto implements Serializable {
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
	private List<EventDto> events;

	//bi-directional many-to-one association to EventSessionDate
	private List<EventSessionDateDto> eventSessionDates;

	//bi-directional many-to-one association to Venue
	private List<VenueDto> venues;

	public LocationDto() {
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

	public List<EventDto> getEvents() {
		return this.events;
	}

	public void setEvents(List<EventDto> events) {
		this.events = events;
	}

	public EventDto addEvent(EventDto event) {
		getEvents().add(event);
		event.setLocation(this);

		return event;
	}

	public EventDto removeEvent(EventDto event) {
		getEvents().remove(event);
		event.setLocation(null);

		return event;
	}

	public List<EventSessionDateDto> getEventSessionDates() {
		return this.eventSessionDates;
	}

	public void setEventSessionDates(List<EventSessionDateDto> eventSessionDates) {
		this.eventSessionDates = eventSessionDates;
	}

	public EventSessionDateDto addEventSessionDate(EventSessionDateDto eventSessionDate) {
		getEventSessionDates().add(eventSessionDate);
		eventSessionDate.setLocation(this);

		return eventSessionDate;
	}

	public EventSessionDateDto removeEventSessionDate(EventSessionDateDto eventSessionDate) {
		getEventSessionDates().remove(eventSessionDate);
		eventSessionDate.setLocation(null);

		return eventSessionDate;
	}

	public List<VenueDto> getVenues() {
		return this.venues;
	}

	public void setVenues(List<VenueDto> venues) {
		this.venues = venues;
	}

	public VenueDto addVenue(VenueDto venue) {
		getVenues().add(venue);
		venue.setLocation(this);

		return venue;
	}

	public VenueDto removeVenue(VenueDto venue) {
		getVenues().remove(venue);
		venue.setLocation(null);

		return venue;
	}

}