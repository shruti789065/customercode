package com.jakala.menarini.core.model;

import java.io.Serializable;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

import org.apache.tika.config.Field;


/**
 * The persistent class for the EVENT database table.
 * 
 */
public class Event implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String table = "EVENT";

	private long id;

	private String coordinators;

	@Field(name="created_on")
	private Timestamp createdOn;

	private String description;

	@Field(name="end_date")
	private Date endDate;

	@Field(name="event_type")
	private String eventType;

	@Field(name="last_updated_on")
	private Timestamp lastUpdatedOn;

	@Field(name="start_date")
	private Date startDate;

	private String subscription;

	private String title;

	//bi-directional many-to-one association to Location
	private Location location;

	//bi-directional many-to-one association to Venue
	private Venue venue;

	//bi-directional many-to-one association to EventEnrollment
	private List<EventEnrollment> eventEnrollments;

	//bi-directional many-to-one association to EventSessionDate
	private List<EventSessionDate> eventSessionDates;

	//bi-directional many-to-one association to EventSpeaker
	private List<EventSpeaker> eventSpeakers;

	//bi-directional many-to-one association to EventTopic
	private List<EventTopic> eventTopics;

	public Event() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCoordinators() {
		return this.coordinators;
	}

	public void setCoordinators(String coordinators) {
		this.coordinators = coordinators;
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

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getEventType() {
		return this.eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Timestamp getLastUpdatedOn() {
		return this.lastUpdatedOn;
	}

	public void setLastUpdatedOn(Timestamp lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getSubscription() {
		return this.subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public List<EventEnrollment> getEventEnrollments() {
		return this.eventEnrollments;
	}

	public void setEventEnrollments(List<EventEnrollment> eventEnrollments) {
		this.eventEnrollments = eventEnrollments;
	}

	public EventEnrollment addEventEnrollment(EventEnrollment eventEnrollment) {
		getEventEnrollments().add(eventEnrollment);
		eventEnrollment.setEvent(this);

		return eventEnrollment;
	}

	public EventEnrollment removeEventEnrollment(EventEnrollment eventEnrollment) {
		getEventEnrollments().remove(eventEnrollment);
		eventEnrollment.setEvent(null);

		return eventEnrollment;
	}

	public List<EventSessionDate> getEventSessionDates() {
		return this.eventSessionDates;
	}

	public void setEventSessionDates(List<EventSessionDate> eventSessionDates) {
		this.eventSessionDates = eventSessionDates;
	}

	public EventSessionDate addEventSessionDate(EventSessionDate eventSessionDate) {
		getEventSessionDates().add(eventSessionDate);
		eventSessionDate.setEvent(this);

		return eventSessionDate;
	}

	public EventSessionDate removeEventSessionDate(EventSessionDate eventSessionDate) {
		getEventSessionDates().remove(eventSessionDate);
		eventSessionDate.setEvent(null);

		return eventSessionDate;
	}

	public List<EventSpeaker> getEventSpeakers() {
		return this.eventSpeakers;
	}

	public void setEventSpeakers(List<EventSpeaker> eventSpeakers) {
		this.eventSpeakers = eventSpeakers;
	}

	public EventSpeaker addEventSpeaker(EventSpeaker eventSpeaker) {
		getEventSpeakers().add(eventSpeaker);
		eventSpeaker.setEvent(this);

		return eventSpeaker;
	}

	public EventSpeaker removeEventSpeaker(EventSpeaker eventSpeaker) {
		getEventSpeakers().remove(eventSpeaker);
		eventSpeaker.setEvent(null);

		return eventSpeaker;
	}

	public List<EventTopic> getEventTopics() {
		return this.eventTopics;
	}

	public void setEventTopics(List<EventTopic> eventTopics) {
		this.eventTopics = eventTopics;
	}

	public EventTopic addEventTopic(EventTopic eventTopic) {
		getEventTopics().add(eventTopic);
		eventTopic.setEvent(this);

		return eventTopic;
	}

	public EventTopic removeEventTopic(EventTopic eventTopic) {
		getEventTopics().remove(eventTopic);
		eventTopic.setEvent(null);

		return eventTopic;
	}

}