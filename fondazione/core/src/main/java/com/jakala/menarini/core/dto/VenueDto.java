package com.jakala.menarini.core.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.apache.tika.config.Field;

@SuppressWarnings("squid:S2384")
public class VenueDto implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String table = "VENUE";

	private long id;

	@Field(name="created_on")
	private Timestamp createdOn;

	private String description;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	private String name;

	private String notes;

	//bi-directional many-to-one association to Event
	private List<EventDto> events;

	//bi-directional many-to-one association to EventSessionDateDto
	private List<EventSessionDateDto> EventSessionDateDtos;

	//bi-directional many-to-one association to Location
	private LocationDto location;

	public VenueDto() {
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

	public List<EventDto> getEvents() {
		return this.events;
	}

	public void setEvents(List<EventDto> events) {
		this.events = events;
	}

	public EventDto addEvent(EventDto event) {
		getEvents().add(event);
		event.setVenue(this);

		return event;
	}

	public EventDto removeEvent(EventDto event) {
		getEvents().remove(event);
		event.setVenue(null);

		return event;
	}

	public List<EventSessionDateDto> getEventSessionDates() {
		return this.EventSessionDateDtos;
	}

	public void setEventSessionDates(List<EventSessionDateDto> EventSessionDateDtos) {
		this.EventSessionDateDtos = EventSessionDateDtos;
	}

	public EventSessionDateDto addEventSessionDateDto(EventSessionDateDto EventSessionDateDto) {
		getEventSessionDates().add(EventSessionDateDto);
		EventSessionDateDto.setVenue(this);

		return EventSessionDateDto;
	}

	public EventSessionDateDto removeEventSessionDate(EventSessionDateDto EventSessionDateDto) {
		getEventSessionDates().remove(EventSessionDateDto);
		EventSessionDateDto.setVenue(null);

		return EventSessionDateDto;
	}

	public LocationDto getLocation() {
		return this.location;
	}

	public void setLocation(LocationDto location) {
		this.location = location;
	}

}