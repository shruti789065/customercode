package com.jakala.menarini.core.dto;

import java.io.Serializable;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

import org.apache.tika.config.Field;

@SuppressWarnings("squid:S2384")
public class EventDto implements Serializable {
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
	private LocationDto location;

	//bi-directional many-to-one association to Venue
	private VenueDto venue;

	//bi-directional many-to-one association to EventEnrollment
	private List<EventEnrollment> eventEnrollments;

	//bi-directional many-to-one association to EventSessionDate
	private List<EventSessionDateDto> eventSessionDates;

	//bi-directional many-to-one association to EventSpeaker
	private List<EventSpeakerDto> eventSpeakers;

	//bi-directional many-to-one association to EventTopic
	private List<EventTopicDto> eventTopics;

	public EventDto() {
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

	public LocationDto getLocation() {
		return this.location;
	}

	public void setLocation(LocationDto location) {
		this.location = location;
	}

	public VenueDto getVenue() {
		return this.venue;
	}

	public void setVenue(VenueDto venue) {
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

	public List<EventSessionDateDto> getEventSessionDates() {
		return this.eventSessionDates;
	}

	public void setEventSessionDates(List<EventSessionDateDto> eventSessionDates) {
		this.eventSessionDates = eventSessionDates;
	}

	public EventSessionDateDto addEventSessionDate(EventSessionDateDto eventSessionDate) {
		getEventSessionDates().add(eventSessionDate);
		eventSessionDate.setEvent(this);

		return eventSessionDate;
	}

	public EventSessionDateDto removeEventSessionDate(EventSessionDateDto eventSessionDate) {
		getEventSessionDates().remove(eventSessionDate);
		eventSessionDate.setEvent(null);

		return eventSessionDate;
	}

	public List<EventSpeakerDto> getEventSpeakers() {
		return this.eventSpeakers;
	}

	public void setEventSpeakers(List<EventSpeakerDto> eventSpeakers) {
		this.eventSpeakers = eventSpeakers;
	}

	public EventSpeakerDto addEventSpeaker(EventSpeakerDto eventSpeaker) {
		getEventSpeakers().add(eventSpeaker);
		eventSpeaker.setEvent(this);

		return eventSpeaker;
	}

	public EventSpeakerDto removeEventSpeaker(EventSpeakerDto eventSpeaker) {
		getEventSpeakers().remove(eventSpeaker);
		eventSpeaker.setEvent(null);

		return eventSpeaker;
	}

	public List<EventTopicDto> getEventTopics() {
		return this.eventTopics;
	}

	public void setEventTopics(List<EventTopicDto> eventTopics) {
		this.eventTopics = eventTopics;
	}

	public EventTopicDto addEventTopic(EventTopicDto eventTopic) {
		getEventTopics().add(eventTopic);
		eventTopic.setEvent(this);

		return eventTopic;
	}

	public EventTopicDto removeEventTopic(EventTopicDto eventTopic) {
		getEventTopics().remove(eventTopic);
		eventTopic.setEvent(null);

		return eventTopic;
	}

}