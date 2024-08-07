/*
 * This file is generated by jOOQ.
 */
package com.jakala.menarini.core.entities;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.jakala.menarini.core.entities.utils.Indexes;
import com.jakala.menarini.core.entities.utils.Keys;
import com.jakala.menarini.core.entities.utils.DbSchema;
import com.jakala.menarini.core.entities.records.EventTopicRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row6;
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
public class EventTopic extends TableImpl<EventTopicRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>menarini_dev.EVENT_TOPIC</code>
     */
    public static final EventTopic EVENT_TOPIC = new EventTopic();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<EventTopicRecord> getRecordType() {
        return EventTopicRecord.class;
    }

    /**
     * The column <code>menarini_dev.EVENT_TOPIC.id</code>.
     */
    public final TableField<EventTopicRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>menarini_dev.EVENT_TOPIC.priority</code>.
     */
    public final TableField<EventTopicRecord, Integer> PRIORITY = createField(DSL.name("priority"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>menarini_dev.EVENT_TOPIC.topic_id</code>.
     */
    public final TableField<EventTopicRecord, Long> TOPIC_ID = createField(DSL.name("topic_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>menarini_dev.EVENT_TOPIC.event_id</code>.
     */
    public final TableField<EventTopicRecord, Long> EVENT_ID = createField(DSL.name("event_id"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>menarini_dev.EVENT_TOPIC.created_on</code>.
     */
    public final TableField<EventTopicRecord, LocalDateTime> CREATED_ON = createField(DSL.name("created_on"), SQLDataType.LOCALDATETIME(0).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>menarini_dev.EVENT_TOPIC.last_updated_on</code>.
     */
    public final TableField<EventTopicRecord, LocalDateTime> LAST_UPDATED_ON = createField(DSL.name("last_updated_on"), SQLDataType.LOCALDATETIME(0).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "");

    private EventTopic(Name alias, Table<EventTopicRecord> aliased) {
        this(alias, aliased, null);
    }

    private EventTopic(Name alias, Table<EventTopicRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>menarini_dev.EVENT_TOPIC</code> table reference
     */
    public EventTopic(String alias) {
        this(DSL.name(alias), EVENT_TOPIC);
    }

    /**
     * Create an aliased <code>menarini_dev.EVENT_TOPIC</code> table reference
     */
    public EventTopic(Name alias) {
        this(alias, EVENT_TOPIC);
    }

    /**
     * Create a <code>menarini_dev.EVENT_TOPIC</code> table reference
     */
    public EventTopic() {
        this(DSL.name("EVENT_TOPIC"), null);
    }

    public <O extends Record> EventTopic(Table<O> child, ForeignKey<O, EventTopicRecord> key) {
        super(child, key, EVENT_TOPIC);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DbSchema.MENARINI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.EVENT_TOPIC_EVENT_ID, Indexes.EVENT_TOPIC_TOPIC_ID);
    }

    @Override
    public Identity<EventTopicRecord, Long> getIdentity() {
        return (Identity<EventTopicRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<EventTopicRecord> getPrimaryKey() {
        return Keys.KEY_EVENT_TOPIC_PRIMARY;
    }

    @Override
    public List<ForeignKey<EventTopicRecord, ?>> getReferences() {
        return Arrays.asList(Keys.EVENT_TOPIC_IBFK_1, Keys.EVENT_TOPIC_IBFK_2);
    }

    private transient Topic _topic;
    private transient Event _event;

    /**
     * Get the implicit join path to the <code>menarini_dev.TOPIC</code> table.
     */
    public Topic topic() {
        if (_topic == null)
            _topic = new Topic(this, Keys.EVENT_TOPIC_IBFK_1);

        return _topic;
    }

    /**
     * Get the implicit join path to the <code>menarini_dev.EVENT</code> table.
     */
    public Event event() {
        if (_event == null)
            _event = new Event(this, Keys.EVENT_TOPIC_IBFK_2);

        return _event;
    }

    @Override
    public EventTopic as(String alias) {
        return new EventTopic(DSL.name(alias), this);
    }

    @Override
    public EventTopic as(Name alias) {
        return new EventTopic(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public EventTopic rename(String name) {
        return new EventTopic(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public EventTopic rename(Name name) {
        return new EventTopic(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, Integer, Long, Long, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
