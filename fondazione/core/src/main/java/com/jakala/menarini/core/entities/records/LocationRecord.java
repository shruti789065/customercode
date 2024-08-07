/*
 * This file is generated by jOOQ.
 */
package com.jakala.menarini.core.entities.records;


import java.time.LocalDateTime;

import com.jakala.menarini.core.entities.Location;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LocationRecord extends UpdatableRecordImpl<LocationRecord> implements Record6<Long, String, String, String, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>menarini_dev.LOCATION.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>menarini_dev.LOCATION.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>menarini_dev.LOCATION.city</code>.
     */
    public void setCity(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>menarini_dev.LOCATION.city</code>.
     */
    public String getCity() {
        return (String) get(1);
    }

    /**
     * Setter for <code>menarini_dev.LOCATION.country</code>.
     */
    public void setCountry(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>menarini_dev.LOCATION.country</code>.
     */
    public String getCountry() {
        return (String) get(2);
    }

    /**
     * Setter for <code>menarini_dev.LOCATION.notes</code>.
     */
    public void setNotes(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>menarini_dev.LOCATION.notes</code>.
     */
    public String getNotes() {
        return (String) get(3);
    }

    /**
     * Setter for <code>menarini_dev.LOCATION.created_on</code>.
     */
    public void setCreatedOn(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>menarini_dev.LOCATION.created_on</code>.
     */
    public LocalDateTime getCreatedOn() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>menarini_dev.LOCATION.last_updated_on</code>.
     */
    public void setLastUpdatedOn(LocalDateTime value) {
        set(5, value);
    }

    /**
     * Getter for <code>menarini_dev.LOCATION.last_updated_on</code>.
     */
    public LocalDateTime getLastUpdatedOn() {
        return (LocalDateTime) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, String, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<Long, String, String, String, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Location.LOCATION.ID;
    }

    @Override
    public Field<String> field2() {
        return Location.LOCATION.CITY;
    }

    @Override
    public Field<String> field3() {
        return Location.LOCATION.COUNTRY;
    }

    @Override
    public Field<String> field4() {
        return Location.LOCATION.NOTES;
    }

    @Override
    public Field<LocalDateTime> field5() {
        return Location.LOCATION.CREATED_ON;
    }

    @Override
    public Field<LocalDateTime> field6() {
        return Location.LOCATION.LAST_UPDATED_ON;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getCity();
    }

    @Override
    public String component3() {
        return getCountry();
    }

    @Override
    public String component4() {
        return getNotes();
    }

    @Override
    public LocalDateTime component5() {
        return getCreatedOn();
    }

    @Override
    public LocalDateTime component6() {
        return getLastUpdatedOn();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getCity();
    }

    @Override
    public String value3() {
        return getCountry();
    }

    @Override
    public String value4() {
        return getNotes();
    }

    @Override
    public LocalDateTime value5() {
        return getCreatedOn();
    }

    @Override
    public LocalDateTime value6() {
        return getLastUpdatedOn();
    }

    @Override
    public LocationRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public LocationRecord value2(String value) {
        setCity(value);
        return this;
    }

    @Override
    public LocationRecord value3(String value) {
        setCountry(value);
        return this;
    }

    @Override
    public LocationRecord value4(String value) {
        setNotes(value);
        return this;
    }

    @Override
    public LocationRecord value5(LocalDateTime value) {
        setCreatedOn(value);
        return this;
    }

    @Override
    public LocationRecord value6(LocalDateTime value) {
        setLastUpdatedOn(value);
        return this;
    }

    @Override
    public LocationRecord values(Long value1, String value2, String value3, String value4, LocalDateTime value5, LocalDateTime value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LocationRecord
     */
    public LocationRecord() {
        super(Location.LOCATION);
    }

    /**
     * Create a detached, initialised LocationRecord
     */
    public LocationRecord(Long id, String city, String country, String notes, LocalDateTime createdOn, LocalDateTime lastUpdatedOn) {
        super(Location.LOCATION);

        setId(id);
        setCity(city);
        setCountry(country);
        setNotes(notes);
        setCreatedOn(createdOn);
        setLastUpdatedOn(lastUpdatedOn);
    }
}
