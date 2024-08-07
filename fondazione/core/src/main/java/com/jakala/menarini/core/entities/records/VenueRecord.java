/*
 * This file is generated by jOOQ.
 */
package com.jakala.menarini.core.entities.records;


import java.time.LocalDateTime;

import com.jakala.menarini.core.entities.Venue;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class VenueRecord extends UpdatableRecordImpl<VenueRecord> implements Record7<Long, Long, String, String, String, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>menarini_dev.VENUE.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>menarini_dev.VENUE.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>menarini_dev.VENUE.location_id</code>.
     */
    public void setLocationId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>menarini_dev.VENUE.location_id</code>.
     */
    public Long getLocationId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>menarini_dev.VENUE.name</code>.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>menarini_dev.VENUE.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>menarini_dev.VENUE.description</code>.
     */
    public void setDescription(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>menarini_dev.VENUE.description</code>.
     */
    public String getDescription() {
        return (String) get(3);
    }

    /**
     * Setter for <code>menarini_dev.VENUE.notes</code>.
     */
    public void setNotes(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>menarini_dev.VENUE.notes</code>.
     */
    public String getNotes() {
        return (String) get(4);
    }

    /**
     * Setter for <code>menarini_dev.VENUE.created_on</code>.
     */
    public void setCreatedOn(LocalDateTime value) {
        set(5, value);
    }

    /**
     * Getter for <code>menarini_dev.VENUE.created_on</code>.
     */
    public LocalDateTime getCreatedOn() {
        return (LocalDateTime) get(5);
    }

    /**
     * Setter for <code>menarini_dev.VENUE.last_updated_on</code>.
     */
    public void setLastUpdatedOn(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>menarini_dev.VENUE.last_updated_on</code>.
     */
    public LocalDateTime getLastUpdatedOn() {
        return (LocalDateTime) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<Long, Long, String, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<Long, Long, String, String, String, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Venue.VENUE.ID;
    }

    @Override
    public Field<Long> field2() {
        return Venue.VENUE.LOCATION_ID;
    }

    @Override
    public Field<String> field3() {
        return Venue.VENUE.NAME;
    }

    @Override
    public Field<String> field4() {
        return Venue.VENUE.DESCRIPTION;
    }

    @Override
    public Field<String> field5() {
        return Venue.VENUE.NOTES;
    }

    @Override
    public Field<LocalDateTime> field6() {
        return Venue.VENUE.CREATED_ON;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return Venue.VENUE.LAST_UPDATED_ON;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getLocationId();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public String component4() {
        return getDescription();
    }

    @Override
    public String component5() {
        return getNotes();
    }

    @Override
    public LocalDateTime component6() {
        return getCreatedOn();
    }

    @Override
    public LocalDateTime component7() {
        return getLastUpdatedOn();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getLocationId();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public String value4() {
        return getDescription();
    }

    @Override
    public String value5() {
        return getNotes();
    }

    @Override
    public LocalDateTime value6() {
        return getCreatedOn();
    }

    @Override
    public LocalDateTime value7() {
        return getLastUpdatedOn();
    }

    @Override
    public VenueRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public VenueRecord value2(Long value) {
        setLocationId(value);
        return this;
    }

    @Override
    public VenueRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public VenueRecord value4(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public VenueRecord value5(String value) {
        setNotes(value);
        return this;
    }

    @Override
    public VenueRecord value6(LocalDateTime value) {
        setCreatedOn(value);
        return this;
    }

    @Override
    public VenueRecord value7(LocalDateTime value) {
        setLastUpdatedOn(value);
        return this;
    }

    @Override
    public VenueRecord values(Long value1, Long value2, String value3, String value4, String value5, LocalDateTime value6, LocalDateTime value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached VenueRecord
     */
    public VenueRecord() {
        super(Venue.VENUE);
    }

    /**
     * Create a detached, initialised VenueRecord
     */
    public VenueRecord(Long id, Long locationId, String name, String description, String notes, LocalDateTime createdOn, LocalDateTime lastUpdatedOn) {
        super(Venue.VENUE);

        setId(id);
        setLocationId(locationId);
        setName(name);
        setDescription(description);
        setNotes(notes);
        setCreatedOn(createdOn);
        setLastUpdatedOn(lastUpdatedOn);
    }
}
