/*
 * This file is generated by jOOQ.
 */
package com.jakala.menarini.core.entities.records;


import java.time.LocalDate;
import java.time.LocalDateTime;

import com.jakala.menarini.core.entities.Event;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record12;
import org.jooq.Row12;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EventRecord extends UpdatableRecordImpl<EventRecord> implements Record12<Long, String, String, String, String, String, LocalDate, LocalDate, Long, Long, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>menarini_dev.EVENT.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>menarini_dev.EVENT.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>menarini_dev.EVENT.event_type</code>.
     */
    public void setEventType(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>menarini_dev.EVENT.event_type</code>.
     */
    public String getEventType() {
        return (String) get(1);
    }

    /**
     * Setter for <code>menarini_dev.EVENT.title</code>.
     */
    public void setTitle(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>menarini_dev.EVENT.title</code>.
     */
    public String getTitle() {
        return (String) get(2);
    }

    /**
     * Setter for <code>menarini_dev.EVENT.description</code>.
     */
    public void setDescription(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>menarini_dev.EVENT.description</code>.
     */
    public String getDescription() {
        return (String) get(3);
    }

    /**
     * Setter for <code>menarini_dev.EVENT.coordinators</code>.
     */
    public void setCoordinators(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>menarini_dev.EVENT.coordinators</code>.
     */
    public String getCoordinators() {
        return (String) get(4);
    }

    /**
     * Setter for <code>menarini_dev.EVENT.subscription</code>.
     */
    public void setSubscription(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>menarini_dev.EVENT.subscription</code>.
     */
    public String getSubscription() {
        return (String) get(5);
    }

    /**
     * Setter for <code>menarini_dev.EVENT.start_date</code>.
     */
    public void setStartDate(LocalDate value) {
        set(6, value);
    }

    /**
     * Getter for <code>menarini_dev.EVENT.start_date</code>.
     */
    public LocalDate getStartDate() {
        return (LocalDate) get(6);
    }

    /**
     * Setter for <code>menarini_dev.EVENT.end_date</code>.
     */
    public void setEndDate(LocalDate value) {
        set(7, value);
    }

    /**
     * Getter for <code>menarini_dev.EVENT.end_date</code>.
     */
    public LocalDate getEndDate() {
        return (LocalDate) get(7);
    }

    /**
     * Setter for <code>menarini_dev.EVENT.location_id</code>.
     */
    public void setLocationId(Long value) {
        set(8, value);
    }

    /**
     * Getter for <code>menarini_dev.EVENT.location_id</code>.
     */
    public Long getLocationId() {
        return (Long) get(8);
    }

    /**
     * Setter for <code>menarini_dev.EVENT.venue_id</code>.
     */
    public void setVenueId(Long value) {
        set(9, value);
    }

    /**
     * Getter for <code>menarini_dev.EVENT.venue_id</code>.
     */
    public Long getVenueId() {
        return (Long) get(9);
    }

    /**
     * Setter for <code>menarini_dev.EVENT.created_on</code>.
     */
    public void setCreatedOn(LocalDateTime value) {
        set(10, value);
    }

    /**
     * Getter for <code>menarini_dev.EVENT.created_on</code>.
     */
    public LocalDateTime getCreatedOn() {
        return (LocalDateTime) get(10);
    }

    /**
     * Setter for <code>menarini_dev.EVENT.last_updated_on</code>.
     */
    public void setLastUpdatedOn(LocalDateTime value) {
        set(11, value);
    }

    /**
     * Getter for <code>menarini_dev.EVENT.last_updated_on</code>.
     */
    public LocalDateTime getLastUpdatedOn() {
        return (LocalDateTime) get(11);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record12 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row12<Long, String, String, String, String, String, LocalDate, LocalDate, Long, Long, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    @Override
    public Row12<Long, String, String, String, String, String, LocalDate, LocalDate, Long, Long, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row12) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Event.EVENT.ID;
    }

    @Override
    public Field<String> field2() {
        return Event.EVENT.EVENT_TYPE;
    }

    @Override
    public Field<String> field3() {
        return Event.EVENT.TITLE;
    }

    @Override
    public Field<String> field4() {
        return Event.EVENT.DESCRIPTION;
    }

    @Override
    public Field<String> field5() {
        return Event.EVENT.COORDINATORS;
    }

    @Override
    public Field<String> field6() {
        return Event.EVENT.SUBSCRIPTION;
    }

    @Override
    public Field<LocalDate> field7() {
        return Event.EVENT.START_DATE;
    }

    @Override
    public Field<LocalDate> field8() {
        return Event.EVENT.END_DATE;
    }

    @Override
    public Field<Long> field9() {
        return Event.EVENT.LOCATION_ID;
    }

    @Override
    public Field<Long> field10() {
        return Event.EVENT.VENUE_ID;
    }

    @Override
    public Field<LocalDateTime> field11() {
        return Event.EVENT.CREATED_ON;
    }

    @Override
    public Field<LocalDateTime> field12() {
        return Event.EVENT.LAST_UPDATED_ON;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getEventType();
    }

    @Override
    public String component3() {
        return getTitle();
    }

    @Override
    public String component4() {
        return getDescription();
    }

    @Override
    public String component5() {
        return getCoordinators();
    }

    @Override
    public String component6() {
        return getSubscription();
    }

    @Override
    public LocalDate component7() {
        return getStartDate();
    }

    @Override
    public LocalDate component8() {
        return getEndDate();
    }

    @Override
    public Long component9() {
        return getLocationId();
    }

    @Override
    public Long component10() {
        return getVenueId();
    }

    @Override
    public LocalDateTime component11() {
        return getCreatedOn();
    }

    @Override
    public LocalDateTime component12() {
        return getLastUpdatedOn();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getEventType();
    }

    @Override
    public String value3() {
        return getTitle();
    }

    @Override
    public String value4() {
        return getDescription();
    }

    @Override
    public String value5() {
        return getCoordinators();
    }

    @Override
    public String value6() {
        return getSubscription();
    }

    @Override
    public LocalDate value7() {
        return getStartDate();
    }

    @Override
    public LocalDate value8() {
        return getEndDate();
    }

    @Override
    public Long value9() {
        return getLocationId();
    }

    @Override
    public Long value10() {
        return getVenueId();
    }

    @Override
    public LocalDateTime value11() {
        return getCreatedOn();
    }

    @Override
    public LocalDateTime value12() {
        return getLastUpdatedOn();
    }

    @Override
    public EventRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public EventRecord value2(String value) {
        setEventType(value);
        return this;
    }

    @Override
    public EventRecord value3(String value) {
        setTitle(value);
        return this;
    }

    @Override
    public EventRecord value4(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public EventRecord value5(String value) {
        setCoordinators(value);
        return this;
    }

    @Override
    public EventRecord value6(String value) {
        setSubscription(value);
        return this;
    }

    @Override
    public EventRecord value7(LocalDate value) {
        setStartDate(value);
        return this;
    }

    @Override
    public EventRecord value8(LocalDate value) {
        setEndDate(value);
        return this;
    }

    @Override
    public EventRecord value9(Long value) {
        setLocationId(value);
        return this;
    }

    @Override
    public EventRecord value10(Long value) {
        setVenueId(value);
        return this;
    }

    @Override
    public EventRecord value11(LocalDateTime value) {
        setCreatedOn(value);
        return this;
    }

    @Override
    public EventRecord value12(LocalDateTime value) {
        setLastUpdatedOn(value);
        return this;
    }

    @Override
    public EventRecord values(Long value1, String value2, String value3, String value4, String value5, String value6, LocalDate value7, LocalDate value8, Long value9, Long value10, LocalDateTime value11, LocalDateTime value12) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached EventRecord
     */
    public EventRecord() {
        super(Event.EVENT);
    }

    /**
     * Create a detached, initialised EventRecord
     */
    public EventRecord(Long id, String eventType, String title, String description, String coordinators, String subscription, LocalDate startDate, LocalDate endDate, Long locationId, Long venueId, LocalDateTime createdOn, LocalDateTime lastUpdatedOn) {
        super(Event.EVENT);

        setId(id);
        setEventType(eventType);
        setTitle(title);
        setDescription(description);
        setCoordinators(coordinators);
        setSubscription(subscription);
        setStartDate(startDate);
        setEndDate(endDate);
        setLocationId(locationId);
        setVenueId(venueId);
        setCreatedOn(createdOn);
        setLastUpdatedOn(lastUpdatedOn);
    }
}
