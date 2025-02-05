/*
 * This file is generated by jOOQ.
 */
package com.jakala.menarini.core.entities.records;


import java.time.LocalDateTime;

import com.jakala.menarini.core.entities.Role;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RoleRecord extends UpdatableRecordImpl<RoleRecord> implements Record5<Long, String, String, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>menarini_dev.ROLE.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>menarini_dev.ROLE.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>menarini_dev.ROLE.name</code>.
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>menarini_dev.ROLE.name</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>menarini_dev.ROLE.description</code>.
     */
    public void setDescription(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>menarini_dev.ROLE.description</code>.
     */
    public String getDescription() {
        return (String) get(2);
    }

    /**
     * Setter for <code>menarini_dev.ROLE.created_on</code>.
     */
    public void setCreatedOn(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>menarini_dev.ROLE.created_on</code>.
     */
    public LocalDateTime getCreatedOn() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for <code>menarini_dev.ROLE.last_updated_on</code>.
     */
    public void setLastUpdatedOn(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>menarini_dev.ROLE.last_updated_on</code>.
     */
    public LocalDateTime getLastUpdatedOn() {
        return (LocalDateTime) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<Long, String, String, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Role.ROLE.ID;
    }

    @Override
    public Field<String> field2() {
        return Role.ROLE.NAME;
    }

    @Override
    public Field<String> field3() {
        return Role.ROLE.DESCRIPTION;
    }

    @Override
    public Field<LocalDateTime> field4() {
        return Role.ROLE.CREATED_ON;
    }

    @Override
    public Field<LocalDateTime> field5() {
        return Role.ROLE.LAST_UPDATED_ON;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getName();
    }

    @Override
    public String component3() {
        return getDescription();
    }

    @Override
    public LocalDateTime component4() {
        return getCreatedOn();
    }

    @Override
    public LocalDateTime component5() {
        return getLastUpdatedOn();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getName();
    }

    @Override
    public String value3() {
        return getDescription();
    }

    @Override
    public LocalDateTime value4() {
        return getCreatedOn();
    }

    @Override
    public LocalDateTime value5() {
        return getLastUpdatedOn();
    }

    @Override
    public RoleRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public RoleRecord value2(String value) {
        setName(value);
        return this;
    }

    @Override
    public RoleRecord value3(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public RoleRecord value4(LocalDateTime value) {
        setCreatedOn(value);
        return this;
    }

    @Override
    public RoleRecord value5(LocalDateTime value) {
        setLastUpdatedOn(value);
        return this;
    }

    @Override
    public RoleRecord values(Long value1, String value2, String value3, LocalDateTime value4, LocalDateTime value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RoleRecord
     */
    public RoleRecord() {
        super(Role.ROLE);
    }

    /**
     * Create a detached, initialised RoleRecord
     */
    public RoleRecord(Long id, String name, String description, LocalDateTime createdOn, LocalDateTime lastUpdatedOn) {
        super(Role.ROLE);

        setId(id);
        setName(name);
        setDescription(description);
        setCreatedOn(createdOn);
        setLastUpdatedOn(lastUpdatedOn);
    }
}
