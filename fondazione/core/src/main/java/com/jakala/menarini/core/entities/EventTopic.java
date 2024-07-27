/*
 * This file is generated by jOOQ.
 */
package com.jakala.menarini.core.entities;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import com.jakala.menarini.core.entities.utils.Indexes;
import com.jakala.menarini.core.entities.utils.Keys;
import com.jakala.menarini.core.entities.utils.DbSchema;
import com.jakala.menarini.core.entities.records.EventTopicRecord;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.12"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EventTopic extends TableImpl<EventTopicRecord> {

    private static final long serialVersionUID = -1050988511;

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
    public final TableField<EventTopicRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>menarini_dev.EVENT_TOPIC.priority</code>.
     */
    public final TableField<EventTopicRecord, Integer> PRIORITY = createField("priority", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>menarini_dev.EVENT_TOPIC.topic_id</code>.
     */
    public final TableField<EventTopicRecord, Long> TOPIC_ID = createField("topic_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>menarini_dev.EVENT_TOPIC.event_id</code>.
     */
    public final TableField<EventTopicRecord, Long> EVENT_ID = createField("event_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>menarini_dev.EVENT_TOPIC.created_on</code>.
     */
    public final TableField<EventTopicRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>menarini_dev.EVENT_TOPIC.last_updated_on</code>.
     */
    public final TableField<EventTopicRecord, Timestamp> LAST_UPDATED_ON = createField("last_updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * Create a <code>menarini_dev.EVENT_TOPIC</code> table reference
     */
    public EventTopic() {
        this(DSL.name("EVENT_TOPIC"), null);
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

    private EventTopic(Name alias, Table<EventTopicRecord> aliased) {
        this(alias, aliased, null);
    }

    private EventTopic(Name alias, Table<EventTopicRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> EventTopic(Table<O> child, ForeignKey<O, EventTopicRecord> key) {
        super(child, key, EVENT_TOPIC);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return DbSchema.MENARINI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.EVENT_TOPIC_EVENT_ID, Indexes.EVENT_TOPIC_PRIMARY, Indexes.EVENT_TOPIC_TOPIC_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<EventTopicRecord, Long> getIdentity() {
        return Keys.IDENTITY_EVENT_TOPIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<EventTopicRecord> getPrimaryKey() {
        return Keys.KEY_EVENT_TOPIC_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<EventTopicRecord>> getKeys() {
        return Arrays.<UniqueKey<EventTopicRecord>>asList(Keys.KEY_EVENT_TOPIC_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<EventTopicRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<EventTopicRecord, ?>>asList(Keys.EVENT_TOPIC_IBFK_1, Keys.EVENT_TOPIC_IBFK_2);
    }

    public Topic topic() {
        return new Topic(this, Keys.EVENT_TOPIC_IBFK_1);
    }

    public Event event() {
        return new Event(this, Keys.EVENT_TOPIC_IBFK_2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventTopic as(String alias) {
        return new EventTopic(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
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
}
