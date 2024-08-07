/*
 * This file is generated by jOOQ.
 */
package com.jakala.menarini.core.entities;


import java.time.LocalDateTime;

import com.jakala.menarini.core.entities.utils.Keys;
import com.jakala.menarini.core.entities.utils.DbSchema;
import com.jakala.menarini.core.entities.records.TopicRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Topic extends TableImpl<TopicRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>menarini_dev.TOPIC</code>
     */
    public static final Topic TOPIC = new Topic();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TopicRecord> getRecordType() {
        return TopicRecord.class;
    }

    /**
     * The column <code>menarini_dev.TOPIC.id</code>.
     */
    public final TableField<TopicRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>menarini_dev.TOPIC.name</code>.
     */
    public final TableField<TopicRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>menarini_dev.TOPIC.created_on</code>.
     */
    public final TableField<TopicRecord, LocalDateTime> CREATED_ON = createField(DSL.name("created_on"), SQLDataType.LOCALDATETIME(0).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>menarini_dev.TOPIC.last_updated_on</code>.
     */
    public final TableField<TopicRecord, LocalDateTime> LAST_UPDATED_ON = createField(DSL.name("last_updated_on"), SQLDataType.LOCALDATETIME(0).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "");

    private Topic(Name alias, Table<TopicRecord> aliased) {
        this(alias, aliased, null);
    }

    private Topic(Name alias, Table<TopicRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>menarini_dev.TOPIC</code> table reference
     */
    public Topic(String alias) {
        this(DSL.name(alias), TOPIC);
    }

    /**
     * Create an aliased <code>menarini_dev.TOPIC</code> table reference
     */
    public Topic(Name alias) {
        this(alias, TOPIC);
    }

    /**
     * Create a <code>menarini_dev.TOPIC</code> table reference
     */
    public Topic() {
        this(DSL.name("TOPIC"), null);
    }

    public <O extends Record> Topic(Table<O> child, ForeignKey<O, TopicRecord> key) {
        super(child, key, TOPIC);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DbSchema.MENARINI;
    }

    @Override
    public Identity<TopicRecord, Long> getIdentity() {
        return (Identity<TopicRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<TopicRecord> getPrimaryKey() {
        return Keys.KEY_TOPIC_PRIMARY;
    }

    @Override
    public Topic as(String alias) {
        return new Topic(DSL.name(alias), this);
    }

    @Override
    public Topic as(Name alias) {
        return new Topic(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Topic rename(String name) {
        return new Topic(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Topic rename(Name name) {
        return new Topic(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
